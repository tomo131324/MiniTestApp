package com.example.MiniTest.service;

import java.util.List;

import com.example.MiniTest.entity.API;

public interface SaveService {
	
	void addQuestion(List<API> Questions, Integer userId, String questionType, Integer latestTestId);
	
	void deleteQuestionsById(Integer testId, Integer userId);
}
