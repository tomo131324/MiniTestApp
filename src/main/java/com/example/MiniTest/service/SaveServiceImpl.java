package com.example.MiniTest.service;

import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.config.LoginUserDetails;
import com.example.MiniTest.entity.API;
import com.example.MiniTest.entity.Question;

import com.example.MiniTest.repository.QuestionRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class SaveServiceImpl implements SaveService {
	
    @Autowired
    QuestionRepository questionRepository;
    
	//問題保存
    @Override
    public void addQuestion(List<API> choiceQuestions, Integer userId, String questionType, Integer latestTestId) {
    	//テストID更新
    	int testId = latestTestId + 1;
    	
    	for (API questions : choiceQuestions) {
    		Question question = new Question();
			question.setUserId(userId);
			question.setTestId(testId);
			question.setQuestion(questions.getQuestion());
			question.setAnswer(questions.getAnswer());
			question.setChoices(questions.getChoices());
			question.setQuestionType(questionType);
			question.setCreatedAt(Instant.now());
			questionRepository.save(question);
    	}
    	

    }
    
}


