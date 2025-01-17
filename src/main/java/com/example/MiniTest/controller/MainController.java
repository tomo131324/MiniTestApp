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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/")
@SessionAttributes("ocrResult")  // セッションに保存する属性を指定
public class MainController {

    @Autowired
    private ApiService apiservice;
    @Autowired
    private UserService userService;
    @Autowired
    private SaveService saveService;
    @Autowired
    private QuestionService questionService;
	
    // 初期画面
    @GetMapping
    public String showCameraPage() {
        return "index";
    }

    // ログイン画面を表示
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    // パスワードリセットページの表示
    @GetMapping("/password-reset")
    public String showPasswordResetPage() {
        return "password-reset"; // password-reset.html に遷移
    }

    // パスワードリセットの処理
    @PostMapping("/password-reset")
    public String resetPassword(@RequestParam("email") String email,
            @RequestParam("newPassword") String newPassword,
            Model model) {
        // メールアドレスが登録されているかチェック
        if (userService.changePassword(email, newPassword)) {
            // パスワードが正常に変更されたことをユーザーに伝える
            model.addAttribute("message", "パスワードが変更されました。");
            return "password-reset-success"; // 成功メッセージ画面
        } else {
            // メールアドレスが見つからない場合
            model.addAttribute("error", "そのメールアドレスは登録されていません。");
            return "password-reset"; // エラーメッセージを表示
        }
    }

    // 新規登録画面を表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "signup";
    }

    @PostMapping("/register/create")
    public String createUser(@Valid @ModelAttribute("registerForm") RegisterForm registerForm, BindingResult result,
            Model model, HttpSession session, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "signup";
        }

        // メールアドレスの重複チェック
        if (userService.emailExists(registerForm.getEmail())) {
            redirectAttributes.addFlashAttribute("emailError", "このメールアドレスは既に登録されています。");
            return "redirect:/register";
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
    }
   
    //お気に入り
    @GetMapping("/favorite")
    public String favorite(Model model) {
    	Integer userId = questionService.getCurrentUserId();
    	List<Map<String, Object>> question = questionService.getFirstQuestions(userId);
    	model.addAttribute("questions", question);
        return "favorite";
    }
    
    //削除処理
    @PostMapping("/{testId}/delete")
    public String deleteQuestions(@RequestParam("testId") Integer testId) {
    	Integer userId = questionService.getCurrentUserId();
    	saveService.deleteQuestionsById(testId,userId);
    	return "redirect:/";
    }
    
    //お気に入りから問題画面
    @PostMapping("/{testId}")
    public String pastQuestion(@RequestParam("testId") Integer testId, Model model) {
    	Integer userId = questionService.getCurrentUserId();
    	Iterable<Question> questions = questionService.getQuestions(userId,testId);
    	Iterator<Question> iterator = questions.iterator();
    	String form = iterator.next().getQuestionType();
    	
    	model.addAttribute("questions",questions);
    	model.addAttribute("form", form);
    	return "questions";
    }
    
    @PostMapping("/ocr")
    public String ocr(@RequestParam("image") String base64Image, HttpSession session) throws IOException {
        if (base64Image != null && !base64Image.isEmpty()) {
            // Base64データをデコード
            byte[] imageBytes = Base64.decodeBase64(base64Image.split(",")[1]);

            // 一時ファイルに保存
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir, "user_image.png");
            Files.write(tempFile.toPath(), imageBytes);

            // OCR実行
            String text = apiservice.extractText(tempFile);

            // セッションにOCR結果を保存
            session.setAttribute("ocrResult", text);

            // 結果画面に遷移
            return "result";
        }
        return "error";
    }

    // 結果ページ
    @GetMapping("/result") 
    public String result(HttpSession session, Model model, SessionStatus sessionStatus) {
        // セッションから値を取得
        String text = (String) session.getAttribute("ocrResult");
        if (text != null) {
            model.addAttribute("text", text);
        } else {
            model.addAttribute("text", "");
            
        }
        
        // セッションの値を使用した後にセッション破棄
        sessionStatus.setComplete();
        return "result";
    }
    
    //問題作成
    @PostMapping("/create")
    public String create(@RequestParam("number") int number, @RequestParam("form") String form, @RequestParam("textinput") String textinput, Model model){
    	//ユーザーIDと最新テストID取得
    	Integer userId = questionService.getCurrentUserId();
    	Integer latestTestId = questionService.getLatestTestId(userId);
    	
        List<API> Questions = apiservice.createQuestion(textinput,number,form);
        model.addAttribute("questions", Questions);
        model.addAttribute("form", form);
        //問題保存
        saveService.addQuestion(Questions,userId,form,latestTestId);
    	
    	return "questions";
    }
    
    
    //採点
    @PostMapping("/scoring")
    public String scoring(@RequestParam Map<String, String> answers, @RequestParam(name = "testId", required = false) Integer testId, @RequestParam("form") String form, Model model) {
    	//ユーザーIDと最新テストID取得
    	Integer userId = questionService.getCurrentUserId();
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
        	List<Boolean> correction = questionService.scoring(userAnswer,testId);
        	model.addAttribute("correction", correction);
        }
        
    	model.addAttribute("questions", questionService.getQuestions(userId, testId));
    	model.addAttribute("form", form);
    	model.addAttribute("userAnswer", userAnswer);

    	return "scoring";
    }
    
}




