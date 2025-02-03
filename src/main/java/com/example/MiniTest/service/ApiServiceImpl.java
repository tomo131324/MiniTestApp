package com.example.MiniTest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.JSONObject;
import org.json.JSONArray;

import com.example.MiniTest.entity.API;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;

import com.example.MiniTest.entity.Question;

@Service
@Transactional
public class ApiServiceImpl implements ApiService {
	
	@Autowired
    private QuestionService questionService;
	
    // OCR処理を行い、抽出テキストを返すメソッド
	@Override
    public String extractText(File tempFile) throws IOException {
        // 画像をバイト配列として読み込む
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(tempFile));

        // Vision APIのリクエストを作成
        Image image = Image.newBuilder().setContent(imgBytes).build();
        Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feature)
                .setImage(image)
                .build();

        List<AnnotateImageRequest> requests = new ArrayList<>();
        requests.add(request);

        // Vision APIを呼び出し
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            List<AnnotateImageResponse> responses = client.batchAnnotateImages(requests).getResponsesList();

            for (AnnotateImageResponse response : responses) {
                if (response.hasError()) {
                    throw new IOException("Error: " + response.getError().getMessage());
                }

                // 抽出したテキストを返す
                TextAnnotation annotation = response.getFullTextAnnotation();
                return annotation.getText();
            }
        }
        return null; // エラー時やテキストがない場合
    }
	
	//OpenAIを使い問題を作成するメソッド
	@Override
	public List<API> createQuestion(String textinput, int number, String form) {
	    try {
	        // OpenAI APIキー（環境変数から取得）
	        String apiKey = System.getenv("OPENAI_API_KEY");
	        if (apiKey == null || apiKey.isEmpty()) {
	        	throw new IllegalStateException("APIキーが設定されていません。環境変数 'OPENAI_API_KEY' を設定してください。");
	        }

	        // APIエンドポイント
	        String endpoint = "https://api.openai.com/v1/chat/completions";

	        // リクエストペイロードの作成
	        JsonObject systemMessage = new JsonObject();
	        systemMessage.addProperty("role", "system");
	        systemMessage.addProperty("content", "あなたは問題を作るAIです。JSONで結果を出力します。");

	        JsonObject userMessage = new JsonObject();
	        userMessage.addProperty("role", "user");
	        
	        switch (form) {
	        case "記述問題":
	        	userMessage.addProperty("content",
	              	    "以下のテキストを基に、記述問題を" + number + "問作成してください。生成結果をJSON形式で出力してください。出力フォーマットは以下のようにしてください。\n\n" +
	              	    "{\n" +
	              	    "  \"questions\": [\n" +
	        			"    {\n" +
	              	    "	   \"Question\": \"問題文\",\n" +
	              	    "      \"Answer\": \"正解\",\n" +
	        			"    }\n" +
	        			"  ]\n" +
	        			"}\n\n" +
	        			"以下のテキストを使用してください：\n==\n" + textinput
	              	);
	        	break;
	        case "4択問題" :
	        	userMessage.addProperty("content",
	        			"以下のテキストを基に、4択問題を" + number + "問作成してください。生成結果をJSON形式で出力してください。出力フォーマットは以下のようにしてください。\n\n" +
	        			"{\n" +
	        			"  \"questions\": [\n" +
	        			"    {\n" +
	        			"      \"Question\": \"問題文\",\n" +
	        			"      \"Answer\": \"正解\",\n" +
	        			"      \"Choices\": [\"選択肢1\", \"選択肢2\", \"選択肢3\", \"選択肢4\"]\n" +
	        			"    }\n" +
	        			"  ]\n" +
	        			"}\n\n" +
	        			"以下のテキストを使用してください：\n==\n" + textinput
	        		);
	        	break;
	        case "穴埋め問題":
	        	userMessage.addProperty("content",
	        		    "以下のテキストを基に、指定の形式で複数穴埋め問題を" + number + "問作成してください。生成結果をJSON形式で出力してください。出力フォーマットは以下のようにしてください。\n\n" +
	        		    "{\n" +
	        		    "  \"questions\": [\n" +
	        		    "    {\n" +
	        		    "      \"Question\": \"問題文（例: ソフトウェア品質評価には、（ ① ）と（ ② ）が重要である。）\",\n" +
	        		    "      \"Answers\": [\"正解1\", \"正解2\"]\n" +
	        		    "    }\n" +
	        		    "  ]\n" +
	        		    "}\n\n" +
	        		    "作成する問題の条件は以下の通りです:\n" +
	        		    "1. 一つの問題には必ず2個以上3個以下の穴埋めが含まれること。\n" +
	        		    "2. 問題形式の例:\n" +
	        		    "【問題】ソフトウェア品質評価\n" +
	        		    "ソフトウェア品質評価には、次の 2 つの概念がある。\n" +
	        		    "詳細設計工程を例にすると、（ ① ）では、詳細設計結果が詳細設計への入力である基本設計書や開発規約などに適合していることを確認する。一方、（ ② ）では、詳細設計結果通りに実現されたソフトウェアがユーザニーズを満たすことを確認する。\n" +
	        		    "【問題】運用保守のマネジメント\n" +
	        		    "ソフトウェアの不具合が発生した場合、原因を除去することによって同種の不具合が再発しないようにすることを（ ① ）処置という。原因を除去することによって、まだ起きていない類似の不具合を発生させないようにすることを（ ② ）処置という。\n" +
	        		    "3. 問題文は入力テキストを基にするが、文章構造を適切に調整して穴埋め問題を作成すること。\n\n" +
	        		    "以下のテキストを使用してください:\n==\n" + textinput
	        		);
	        	break;
	        }

	        JsonArray messages = new JsonArray();
	        messages.add(systemMessage);
	        messages.add(userMessage);

	        JsonObject payload = new JsonObject();
	        payload.addProperty("model", "gpt-4");
	        payload.add("messages", messages);

	        // HTTPリクエストの設定
	        URL url = new URL(endpoint);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setDoOutput(true);

	        // リクエストボディを送信
	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
	            os.write(input, 0, input.length);
	        }

	        // レスポンスを取得
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) {
	            String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

	            // JSONを解析してcontentを取得
	            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
	            String content = jsonObject
	                .getAsJsonArray("choices")
	                .get(0)
	                .getAsJsonObject()
	                .getAsJsonObject("message")
	                .get("content")
	                .getAsString();

	            // contentをJSONとしてパース
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode rootNode = objectMapper.readTree(content);

	            // "questions"配列をリストに変換
	            List<API> apiList = objectMapper.convertValue(
	                rootNode.get("questions"),
	                objectMapper.getTypeFactory().constructCollectionType(List.class, API.class)
	            );

	            return apiList;
	        } else {
	            String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
	            throw new IOException("APIからエラーが返されました: " + responseCode + " - " + errorResponse);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("エラーが発生しました: " + e.getMessage(), e);
	    }
	}
	
	//OpenAIを使い問題を採点するメソッド
	@Override
	public List<List<Boolean>> scoring(List<String> questionTexts, List<Object> userAnswers, List<List<String>> correctionAnswers, List<String> correctionAnswer, String form, Integer testId, Integer userId) {
	    List<List<Boolean>> correctionList = new ArrayList<>();
	    
	    try {
	        String apiKey = System.getenv("OPENAI_API_KEY");
	        if (apiKey == null || apiKey.isEmpty()) {
	            throw new IllegalStateException("APIキーが設定されていません。環境変数 'OPENAI_API_KEY' を設定してください。");
	        }

	        String endpoint = "https://api.openai.com/v1/chat/completions";

	        // OpenAIへのリクエストを作成
	        JsonObject systemMessage = new JsonObject();
	        systemMessage.addProperty("role", "system");
	        systemMessage.addProperty("content", "あなたは試験の採点官です。JSONで結果を出力します。");

	        JsonObject userMessage = new JsonObject();
	        JSONObject textJson = new JSONObject().put("問題文", new JSONArray(questionTexts));
	        JSONObject userJson = new JSONObject().put("ユーザーの解答", new JSONArray(userAnswers));
	        
	        if ("穴埋め問題".equals(form)) {
	        	JSONObject correctJson = new JSONObject().put("正解", new JSONArray(correctionAnswers));
	        	String prompt = String.format(
	        			"次の穴埋め問題の採点を行ってください。\n\n" +
	        			"【採点基準】\n" +
	        			"1. 正解と完全一致、正解と意味が同じものの場合のみ正解とする。\n" +
	        			"2. 大文字・小文字の違いは無視する。\n" +
	        			"3. 誤字脱字、余計な単語が含まれている場合は不正解。\n" +
	        			"【問題文】\n%s\n\n" +
	        			"【正解】\n%s\n\n" +
	        			"【ユーザーの解答】\n%s\n\n" +
	        			"### 出力フォーマット（厳守）：\n" +
	        			"{ \"correction\": [[true, false], [true, true]] }\n" +
	        			"出力はJSONのみで、余計な説明は不要です。",
	        			textJson.toString(), correctJson.toString(), userJson.toString()
	        	);
		        userMessage.addProperty("role", "user");
		        userMessage.addProperty("content", prompt);
		        
	        } else if ("記述問題".equals(form)) {
	        	JSONObject correctJson = new JSONObject().put("正解", new JSONArray(correctionAnswer));
		        String prompt = String.format(
		        		"以下の記述問題の採点を行ってください。\n\n" +
		        		"【採点基準】\n" +
		        		"1. 完全一致の場合は`true`。\n" +
		        		"2. 重要な部分が正しければ部分的に正しいとして`[true, false]`とする。\n" +
		        		"3. 全く一致しない場合は`[false, false]`。\n" +
		        	    "4. 同義語や表現の違いがあっても意味が同じ場合は`true`。\n" +
		        	    "5. 誤字脱字があっても意味が正確であれば`true`。\n" +
		        	    "6. 余計な情報が含まれている場合は`false`。\n\n" +
		        		"【問題文】\n%s\n\n" +
		        		"【正解】\n%s\n\n" +
		        		"【ユーザーの解答】\n%s\n\n" +
		        		"### 【出力フォーマット】(厳守):\n" +
		        		"{ \\\"correction\\\": [[true, false], [true, true], [false, false]] }\n" +
		        		"出力はJSONのみで、余計な説明は不要です。",
		        		textJson.toString(), correctJson.toString(), userJson.toString()
		        			);

		        userMessage.addProperty("role", "user");
		        userMessage.addProperty("content", prompt);
	        }


	        JsonArray messages = new JsonArray();
	        messages.add(systemMessage);
	        messages.add(userMessage);

	        JsonObject payload = new JsonObject();
	        payload.addProperty("model", "gpt-4");
	        payload.add("messages", messages);

	        URL url = new URL(endpoint);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
	        connection.setRequestProperty("Content-Type", "application/json");
	        connection.setDoOutput(true);

	        try (OutputStream os = connection.getOutputStream()) {
	            byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
	            os.write(input, 0, input.length);
	        }

	        // レスポンスを取得
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) {
	            String jsonResponse = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

	            // JSONを解析して `correction` を取得
	            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
	            String content = jsonObject
	                .getAsJsonArray("choices")
	                .get(0)
	                .getAsJsonObject()
	                .get("message")
	                .getAsJsonObject()
	                .get("content")
	                .getAsString();

	            // OpenAIのレスポンスから `correction` を取得
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode rootNode = objectMapper.readTree(content);
	            JsonNode correctionNode = rootNode.get("correction");

	            if (correctionNode != null && correctionNode.isArray()) {
	                for (JsonNode innerArray : correctionNode) {
	                    List<Boolean> innerList = new ArrayList<>();
	                    for (JsonNode value : innerArray) {
	                        innerList.add(value.asBoolean());
	                    }
	                    correctionList.add(innerList);
	                }
	            }
	        } else {
	            String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
	            throw new IOException("APIからエラーが返されました: " + responseCode + " - " + errorResponse);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("エラーが発生しました: " + e.getMessage(), e);
	    }

	    return correctionList;
	}

	
}