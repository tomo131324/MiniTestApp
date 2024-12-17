package com.example.MiniTest.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoreMiniTest {

	@Id
	private Integer testId;
	private Integer questionId;
}
