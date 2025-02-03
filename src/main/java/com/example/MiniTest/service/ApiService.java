package com.example.MiniTest.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.MiniTest.entity.API;

public interface ApiService {

	String extractText(File tempFile) throws IOException;
	
	List<API> createQuestion(String textinput, int number, String form);

	List<List<Boolean>> scoring(List<String> questionTexts, List<Object> userAnswers, List<List<String>> correctionAnswers,  List<String> correctionAnswer, String form, Integer testId, Integer userId);
	
}

