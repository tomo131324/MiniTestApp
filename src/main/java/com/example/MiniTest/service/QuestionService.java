package com.example.MiniTest.service;

import java.util.List;

import com.example.MiniTest.entity.Question;

public interface QuestionService {
	
	Iterable<Question> getQuestions(Integer userId,Integer testId);
	
	Integer getCurrentUserId();
	
	Integer getLatestTestId(Integer userId);
	
	List<Boolean> scoring(List<String> answers);
}
