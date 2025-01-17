package com.example.MiniTest.service;

import java.util.List;
import java.util.Map;

import com.example.MiniTest.entity.Question;

public interface QuestionService {
	
	Iterable<Question> getQuestions(Integer userId,Integer testId);
	
	Integer getCurrentUserId();
	
	Integer getLatestTestId(Integer userId);
	
	Iterable<Question> getQuestions(Integer userId);
	
	List<Boolean> scoring(List<Object> answers, Integer testId);
	
	List<Map<String, Object>> getFirstQuestions(Integer userId);
}
