package com.example.MiniTest.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.example.MiniTest.entity.API;
import com.example.MiniTest.entity.Question;
import com.example.MiniTest.entity.User;
import com.example.MiniTest.form.MainForm;
import com.example.MiniTest.form.RegisterForm;
import com.example.MiniTest.service.ApiService;
import com.example.MiniTest.service.QuestionService;
import com.example.MiniTest.service.SaveService;
import com.example.MiniTest.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.apache.commons.codec.binary.Base64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    private File tempFile;
    
    private String questionType;
	
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

    // 新規登録画面を表示
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register-form";
    }

    @PostMapping("/register/create")
    public String createUser(@Valid @ModelAttribute("registerForm") RegisterForm registerForm, BindingResult result,Model model) {
    	if (result.hasErrors() ) {
    		return "register-form";
    	}
    	
    	User user = new User();
    	user.setEmail(registerForm.getEmail());
    	user.setPassword(registerForm.getPassword());
    	user.setCreatedAt(Instant.now());
    	
    	userService.createUser(user);
    	return "redirect:/";
    }
    
    //テキスト入力
    @GetMapping("/text-input")
    public String textInput() {
        return "text-input";
    }

    //利用ガイド
    @GetMapping("/guide")
    public String guide() {
        return "guide";
    }

    //お気に入り
    @GetMapping("/favorite")
    public String favorite(Model model) {
        return "favorite";
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

            // 結果画面にリダイレクト
            return "redirect:/result";
        }
        return "/error";
    }

    // 結果ページ
    @GetMapping("/result") 
    public String result(HttpSession session, Model model, SessionStatus sessionStatus) {
        // セッションから値を取得
        String text = (String) session.getAttribute("ocrResult");
        if (text != null) {
            model.addAttribute("text", text);
            sessionStatus.setComplete();
        } else {
            model.addAttribute("text", "");
            sessionStatus.setComplete();
        }
        return "result";
    }
    
    //問題作成
    @PostMapping("/create")
    public String create(@RequestParam("number") int number, @RequestParam("form") String form, @RequestParam("textinput") String textinput,HttpSession session, Model model){
    	//ユーザーIDと最新テストID取得
    	Integer userId = questionService.getCurrentUserId();
    	Integer latestTestId = questionService.getLatestTestId(userId);
    	
    	switch (form) {
        case "記述問題":
        	List<API> descriptionQuestions = apiservice.descriptionQuestion(textinput,number);
            model.addAttribute("questions", descriptionQuestions);
            questionType = "記述問題";
            break;
        case "4択問題":
        	List<API> choiceQuestions = apiservice.choiceQuestion(textinput,number);
            model.addAttribute("questions", choiceQuestions);
            questionType = "4択問題";
            saveService.addQuestion(choiceQuestions,userId,questionType,latestTestId);
            break;
        case "穴埋め問題":
        	List<API> holeQuestions = apiservice.holeQuestion(textinput,number);
            model.addAttribute("questions", holeQuestions);
            questionType = "穴埋め問題";
            break;
        }
    	
    	return "choiceQuestion";
    }
    
    
    //採点
    @PostMapping("/scoring")
    public String scoring(@RequestParam Map<String, String> answers, Model model) {
    	//ユーザーIDと最新テストID取得
    	Integer userId = questionService.getCurrentUserId();
    	Integer latestTestId = questionService.getLatestTestId(userId);
        // MapからListに変換
        List<String> userAnswer = new ArrayList<>(answers.values());
    	//正誤取得
    	List<Boolean> correction = questionService.scoring(userAnswer);
    	
    	model.addAttribute("questions", questionService.getQuestions(userId, latestTestId));
    	model.addAttribute("userAnswer", userAnswer);
    	model.addAttribute("correction", correction);
    	return "/scoring";
    }
    
   
}




