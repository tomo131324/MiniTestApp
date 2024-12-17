package com.example.MiniTest.service;

import java.util.Optional;

import com.example.MiniTest.entity.Question;

public interface QuestionService {

	Iterable<Question> selectAll();
	
	Optional<Question> findByQuestionId(Integer questionId);
}
