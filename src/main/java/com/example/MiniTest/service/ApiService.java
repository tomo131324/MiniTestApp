package com.example.MiniTest.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.MiniTest.entity.API;

public interface ApiService {

	String extractText(File tempFile) throws IOException;
	
	List<API> choiceQuestion(String textinput, int number);

	List<API> descriptionQuestion(String textinput, int number);
	
	List<API> holeQuestion(String textinput, int number);
	
}

