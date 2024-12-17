package com.example.MiniTest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.entity.Question;
import com.example.MiniTest.repository.QuestionRepository;


@Service
@Transactional
public class QuestionImpl implements QuestionService{
	
	@Autowired
	QuestionRepository questionRepository;

	//全件取得
	@Override
	public Iterable<Question> selectAll(){
		return questionRepository.findAll();
	}
	
	//1件取得
	@Override
	public Optional<Question> findByQuestionId(Integer questionId){
		return questionRepository.findByQuestionId(questionId);
    }
}