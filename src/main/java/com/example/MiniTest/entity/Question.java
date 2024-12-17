package com.example.MiniTest.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

	@Id
	private Integer question_id;
	
	private Integer user_id;
	
	private String question_type;
	
	private String question;
	
	private String answer;
	
	private List<String> choices;
	
	private Instant created_at;
	
}
