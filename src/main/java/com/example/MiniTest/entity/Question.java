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
	
	@Column(value = "user_id")
	private Integer userId;
	
	@Column(value = "test_id")
	private Integer testId;
	
	private String question;
	
	private String answer;
	
	private List<String> answers;
	
	private List<String> choices;
	
	@Column(value = "question_type")
	private String questionType;
	
	@Column(value = "created_at")
	private Instant createdAt;
	
}
