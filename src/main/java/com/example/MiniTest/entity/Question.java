package com.example.MiniTest.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

	@Id
	@Column(value = "question_id") 
    private Integer questionId;
	
	private Integer user_id;
	
	private String question_type;
	
	private String question;
	
	private String answer;
	
	private List<String> choices;
	
	private Instant created_at;
	
}
