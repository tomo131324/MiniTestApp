package com.example.MiniTest.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiniTest {

	@Id
	private Integer testId;
	private Integer userId;
	private String testName;
}
