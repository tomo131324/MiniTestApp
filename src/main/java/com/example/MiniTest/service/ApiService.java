package com.example.MiniTest.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.example.MiniTest.entity.API;

public interface ApiService {

	String extractText(File tempFile) throws IOException;
	
	List<API> createQuestion(String textinput, int number, String form);

	
}

