package com.example.MiniTest.service;

import java.io.File;
import java.io.IOException;

import com.example.MiniTest.entity.API;

public interface ApiService {

	String extractText(File tempFile) throws IOException;
	
	API choiceQuestion(String textinput, int number);

	API descriptionQuestion(String textinput, int number);
	
	API holeQuestion(String textinput, int number);
	
}

