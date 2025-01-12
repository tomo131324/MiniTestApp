package com.example.MiniTest.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.config.LoginUserDetails;
import com.example.MiniTest.entity.Question;

import com.example.MiniTest.repository.QuestionRepository;


@Service
@Transactional
public class QuestionServiceImpl implements QuestionService{
	
	@Autowired
	QuestionRepository questionRepository;
	
	/*
	//全件取得
	@Override
	public Iterable<Question> getQuestions(){
		return questionRepository.findAll();
	}*/
	
	//テスト内全件取得
	@Override
	public Iterable<Question> getQuestions(Integer userId,Integer testId){
		return questionRepository.findByUserIdAndTestId(userId,testId);
    }
	
	//ログイン中のユーザーID取得
    @Override
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
        return userDetails.getUserId(); // ログイン中のユーザーIDを取得
    }
	
    //ユーザーごとの最新のテストID取得
    @Override
    public Integer getLatestTestId(Integer userId) {
    	return questionRepository.findMaxTestId(userId).orElse(0);
    }
    
	//採点
	@Override
	public List<String> scoring(List<String> userAnswers){
		Integer userId = getCurrentUserId();
		Integer latestTestId = getLatestTestId(userId);
		List<String> correction = new ArrayList<String>();
		List<String> answers = new ArrayList<String>();
		Iterable<Question> questions = getQuestions(userId,latestTestId);
		
		//correctionに正解の答えを入れる
		for (Question question : questions) {
			answers.add(question.getAnswer());
		}
		
		//正解の答えとユーザーの答え比較
		if (answers.equals(userAnswers)) {
			correction.add("正解");
		} else {
			correction.add("不正解");
		}
		
		return correction;
	}

}