package com.example.MiniTest.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoreCategory {

	@Id
	private Integer categoryId;
	private Integer questionId;
}
