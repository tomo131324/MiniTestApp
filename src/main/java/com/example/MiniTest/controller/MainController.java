package com.example.MiniTest.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.MiniTest.entity.API;
import com.example.MiniTest.entity.Question;
import com.example.MiniTest.entity.User;
import com.example.MiniTest.form.RegisterForm;
import com.example.MiniTest.service.ApiService;
import com.example.MiniTest.service.QuestionService;
import com.example.MiniTest.service.SaveService;
import com.example.MiniTest.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.apache.commons.codec.binary.Base64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private ApiService apiservice;
    @Autowired
    private UserService userService;
    @Autowired
    private SaveService saveService;
    @Autowired
    private QuestionService questionService;
	
    // サイドバーに問題追加
    private void sidebar(Model model) {
        Integer userId = userService.getCurrentUserId();
        List<Map<String, Object>> getQuestions = questionService.getFirstQuestions(userId);
        model.addAttribute("getQuestions", getQuestions);
    }
    
    // 初期画面
    @GetMapping
    public String showCameraPage(Model model) {
    	sidebar(model);
        return "index";
    }

    // ログイン画面を表示
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // パスワードリセットページの表示
    @GetMapping("/password-reset")
    public String showPasswordResetPage(Model model) {
    	model.addAttribute("registerForm", new RegisterForm());
        return "password-reset"; // password-reset.html に遷移
    }

    // パスワードリセットの処理
    @PostMapping("/password-reset")
    public String resetPassword(@RequestParam("email") String email,
            @Valid @ModelAttribute("registerForm") RegisterForm registerForm, BindingResult result,
            Model model) {
    	try {
    		// パスワードチェック
    		if (result.hasErrors()) {
    			return "password-reset";
    		}
    		// メールアドレスが登録されているかチェック
    		if (userService.changePassword(registerForm.getEmail(), registerForm.getPassword())) {
    			model.addAttribute("message", "パスワードが変更されました。");
    			return "password-reset-success";
    		} else {
    			model.addAttribute("error", "そのメールアドレスは登録されていません。");
    			return "password-reset";
    		}
    	} catch (Exception e){
    		model.addAttribute("errorMessage", "パスワードリセットの処理中にエラーが起きました。");
    		return "error";
    	}
    }

    // 新規登録画面を表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
    	// パスワードチェック
        model.addAttribute("registerForm", new RegisterForm());
        return "signup";
    }

    @PostMapping("/register/create")
    public String createUser(@Valid @ModelAttribute("registerForm") RegisterForm registerForm, BindingResult result,
            Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
    	try {
    		// メールアドレスの重複チェック
            if (userService.emailExists(registerForm.getEmail())) {
                redirectAttributes.addFlashAttribute("emailError", "このメールアドレスは既に登録されています。");
                return "redirect:/register";
            }	
    		
            if (result.hasErrors()) {
            	return "signup";
            }

        User user = new User();
        user.setEmail(registerForm.getEmail());
        user.setPassword(registerForm.getPassword());
        user.setCreatedAt(Instant.now());

        userService.createUser(user);

        try {
            // サーバーサイドでログイン処理を実行
            request.login(registerForm.getEmail(), registerForm.getPassword());
        } catch (ServletException e) {
            // エラー処理
            redirectAttributes.addFlashAttribute("loginError", "自動ログインに失敗しました。もう一度お試しください。");
            return "redirect:/login";
        }

        return "redirect:/";
        
    	} catch (Exception e) {
    		model.addAttribute("errorMessage", "新規登録の処理中にエラーが起きました。");
    		return "error";
    	}
    }
   
    
    //削除処理
    @GetMapping("/{testId}/delete")
    public String deleteQuestions(@PathVariable("testId") Integer testId) {
    	Integer userId = userService.getCurrentUserId();
    	saveService.deleteQuestionsById(testId,userId);
    	return "redirect:/";
    }
    
    //お気に入りから問題画面
    @GetMapping("/{testId}")
    public String pastQuestion(@PathVariable("testId") Integer testId, Model model) {
    	Integer userId = userService.getCurrentUserId();
    	Iterable<Question> questions = questionService.getQuestions(userId,testId);
    	Iterator<Question> iterator = questions.iterator();
    	String form = iterator.next().getQuestionType();
    	
    	model.addAttribute("questions",questions);
    	model.addAttribute("form", form);
    	sidebar(model);
    	return "questions";
    }
    
    // result表示
    @GetMapping("/result")
    public String showResult(Model model) {
    	sidebar(model);
    	return "result";
    }
    
    @PostMapping("/ocr")
    public String ocr(@RequestPart("image") MultipartFile image, RedirectAttributes redirectAttributes, Model model) throws IOException {
    	try {       	
        	// ファイル形式チェック
            String contentType = image.getContentType();
            if (!("image/png".equals(contentType) || "image/jpeg".equals(contentType) || "image/jpg".equals(contentType))) {
                redirectAttributes.addFlashAttribute("errorMessage", "対応していない画像形式です。JPEGまたはPNG形式の画像を使用してください。");
                return "redirect:/";
            }
        	
        	byte[] imageBytes = image.getBytes();

            // ファイルサイズチェック (20MB以上の場合エラー)
            if (imageBytes.length > 20 * 1024 * 1024) {
            	redirectAttributes.addFlashAttribute("errorMessage", "ファイルサイズが大きすぎます。20MB以下の画像を使用してください。");
                return "redirect:/";
            }
            
            // 一時ファイルに保存
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir, "user_image.png");
            Files.write(tempFile.toPath(), imageBytes);

            // OCR実行
            String text = apiservice.extractText(tempFile);

            // 禁止文字チェック
            String[] forbiddenPatterns = {"<script>", "</script>"};
            for (String pattern : forbiddenPatterns) {
                if (text.contains(pattern)) {
                    redirectAttributes.addFlashAttribute("errorMessage", "禁止された文字が含まれています。");
                    return "redirect:/";
                }
            }
            
            // テキストnullチェック
            if (text == "") {
            	redirectAttributes.addFlashAttribute("errorMessage", "アップロードされた画像にテキストが含まれていません。");
            	return "redirect:/";
            }
            
            redirectAttributes.addFlashAttribute("text", text);
            return "redirect:/result";
            
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "ファイルの読み込み中にエラーが発生しました。");
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "画像の解析中にエラーが発生しました。");
            return "error";
        }
    }
    
    //問題作成
    @PostMapping("/create")
    public String create(@RequestParam("number") int number, @RequestParam("form") String form, @RequestParam("textinput") String textinput,RedirectAttributes redirectAttributes, Model model){
    	try {
    		//　禁止文字チェック
    		String[] forbiddenPatterns = {"<script>", "</script>"};
    		for (String pattern : forbiddenPatterns) {
    			if (textinput.contains(pattern)) {
    				redirectAttributes.addFlashAttribute("errorMessage", "禁止された文字が含まれています。");
    				redirectAttributes.addFlashAttribute("text", textinput);
    				return "redirect:/result";
    			}
    		}
        
    		// 文字数チェック
    		if (textinput.length() > 10000) {
    			redirectAttributes.addFlashAttribute("errorMessage", "文字数が多すぎます。10000文字以下で入力してください。");
    			redirectAttributes.addFlashAttribute("text", textinput);
    			return "redirect:/result";
    		}
    		//ユーザーIDと最新テストID取得
    		Integer userId = userService.getCurrentUserId();
    		Integer latestTestId = questionService.getLatestTestId(userId);
    	
    		List<API> Questions = apiservice.createQuestion(textinput,number,form);
    		model.addAttribute("questions", Questions);
    		model.addAttribute("form", form);
    		sidebar(model);
    		//問題保存
    		saveService.addQuestion(Questions,userId,form,latestTestId);
    	
    		return "questions";
    	} catch (Exception e) {
    		model.addAttribute("errorMessage", "問題作成の処理中にエラーが起きました。");
    		return "error";
    	}
    }
    
    
    //採点
    @PostMapping("/scoring")
    public String scoring(@RequestParam Map<String, String> answers, @RequestParam(name = "testId", required = false) Integer testId, @RequestParam("form") String form, Model model) {
    	try {
    		//ユーザーIDと最新テストID取得
    		Integer userId = userService.getCurrentUserId();
    		// Mapからリストに変換
    		List<Object> userAnswer = new ArrayList<>();

    		for (Map.Entry<String, String> entry : answers.entrySet()) {
    			String key = entry.getKey(); //answersのキー取得
    			String value = entry.getValue(); //answersのvalue取得

    			// 穴埋め問題の処理（キー形式: answers[数字][数字]）
    			if (key.matches("answers\\[\\d+]\\[\\d+]")) {
                
    				int mainIndex = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
    				int subIndex = Integer.parseInt(key.substring(key.lastIndexOf('[') + 1, key.lastIndexOf(']')));

    				// 必要に応じてリストを拡張
    				while (userAnswer.size() <= mainIndex) {
    					userAnswer.add(new ArrayList<String>());
    				}

    				// リストを取得して値を設定
    				List<String> subList = (List<String>) userAnswer.get(mainIndex);
    				while (subList.size() <= subIndex) {
    					subList.add(null);
    				}
    				subList.set(subIndex, value);

    				// 記述問題または4択問題の処理（キー形式: answers[数字]）
    			} else if (key.matches("answers\\[\\d+]")) {
                
    				int index = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));

    				// 必要に応じてリストを拡張
    				while (userAnswer.size() <= index) {
    					userAnswer.add(null);
    				}

    				// 値を設定
    				userAnswer.set(index, value);
    			}
    		}
        
    		//testIdがnullの場合は、最新のテストIDを取得
    		if (testId == null) {
    			testId = questionService.getLatestTestId(userId);
    		}
    	
    		//正誤取得
    		if ("4択問題".equals(form)) { 
    			List<Boolean> correction = questionService.scoring(userAnswer,testId,userId);
    			model.addAttribute("correction", correction);
    		} else {
    			Iterable<Question> questions = questionService.getQuestions(userId,testId);
    			List<List<String>> correctionAnswers = new ArrayList<>();
    			List<String>correctionAnswer = new ArrayList<String>();
    			List<String> questionTexts = new ArrayList<String>();
    			String questionText;
    		    for (Question question : questions) {
    		    	questionText = question.getQuestion();
    		    	questionTexts.add(questionText);
    		    	correctionAnswers.add(question.getAnswers());
    		    	correctionAnswer.add(question.getAnswer());
    		    	}
    		    List<List<Boolean>> correction = apiservice.scoring(questionTexts,userAnswer,correctionAnswers,correctionAnswer,form,testId,userId);
    		    System.out.println(correction);
    			model.addAttribute("correction", correction);
    		}
        
    		model.addAttribute("questions", questionService.getQuestions(userId, testId));
    		model.addAttribute("form", form);
    		model.addAttribute("userAnswer", userAnswer);
    		sidebar(model);

    		return "scoring";

    	} catch (Exception e) {
    		model.addAttribute("errorMessage", "採点の処理中にエラーが起きました。");
    		return "error";
    	}
    }
}



