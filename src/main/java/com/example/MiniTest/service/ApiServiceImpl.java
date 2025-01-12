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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


@Service
@Transactional
public class ApiServiceImpl implements ApiService {
	
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
	
	//OpenAIを使い問題を作成するメソッド（記述問題）
	@Override
	public List<API> descriptionQuestion(String textinput, int number){
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
			userMessage.addProperty("content",
          	    "以下のテキストを基に、記述問題を" + number + "問作成してください。生成結果をJSON形式で出力してください。出力フォーマットは以下のようにしてください。\n\n" +
          	    "{\n" +
          	    "	\"Question\": \"問題文\",\n" +
          	    "   \"Answer\": \"正解\",\n" +
          	    ",\n" +
          	    "\n" +
          	    " 	\"Question\": \"問題文\",\n" +
          	    "   \"Answer\": \"正解\",\n" +
          	    ",\n" +
          	    "\n\n" +
          	    "以下のテキストを使用してください：\n==\n" + textinput
          	);

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
			if (connection.getResponseCode() == 200) {
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
              
				// contentをJSON配列としてパース
	            ObjectMapper objectMapper = new ObjectMapper();
	            // 複数問題をリストとしてデシリアライズ
	            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, API.class));
			} else {
	            throw new IOException("APIからエラーが返されました: " + connection.getResponseCode());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("エラーが発生しました: " + e.getMessage());
	    }
	}

	//OpenAIを使い問題を作成するメソッド（4択問題）
	@Override
	public List<API> choiceQuestion(String textinput, int number) {
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


	//OpenAIを使い問題を作成するメソッド（穴埋め問題）
	@Override
	public List<API> holeQuestion(String textinput, int number){
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
			userMessage.addProperty("content",
          	    "以下のテキストを基に、穴埋め問題を" + number + "問作成してください。生成結果をJSON形式で出力してください。出力フォーマットは以下のようにしてください。\n\n" +
          	    "{\n" +
          	    "	\"Question\": \"問題文\",\n" +
          	    "   \"Answer\": \"正解\",\n" +
          	    ",\n" +
          	    "\n" +
          	    " 	\"Question\": \"問題文\",\n" +
          	    "   \"Answer\": \"正解\",\n" +
          	    ",\n" +
          	    "\n\n" +
          	    "以下のテキストを使用してください：\n==\n" + textinput
          	);

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
			if (connection.getResponseCode() == 200) {
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
              
				// contentをJSON配列としてパース
	            ObjectMapper objectMapper = new ObjectMapper();
	            // 複数問題をリストとしてデシリアライズ
	            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, API.class));

			} else {
	            throw new IOException("APIからエラーが返されました: " + connection.getResponseCode());
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("エラーが発生しました: " + e.getMessage());
	    }
	}
	
	
}