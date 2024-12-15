package com.example.MiniTest.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.MiniTest.entity.Question;


public interface QuestionRepository extends CrudRepository<Question, Integer>{
}
