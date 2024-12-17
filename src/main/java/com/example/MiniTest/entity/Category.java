package com.example.MiniTest.entity;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

	@Id
	private Integer categoryId;
	private Integer userId;
	private String categoryName;
}
