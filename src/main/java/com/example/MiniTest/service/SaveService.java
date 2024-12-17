package com.example.MiniTest.service;

import java.util.List;

import com.example.MiniTest.entity.Question;

public interface SaveService {
	
	Integer getCurrentUserId(); 

	void insert(List<Question> questions);
}
