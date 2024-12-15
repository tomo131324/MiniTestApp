package com.example.MiniTest.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users") // データベースのテーブル名
public class User {
	
	@Id
	private Integer user_id;
	private String email;
	private String password;
	private Instant createdAt;
}