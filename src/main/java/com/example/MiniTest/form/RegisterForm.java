package com.example.MiniTest.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/* 新規登録フォームに入力された値を格納するためのクラス */
public class RegisterForm {
	@NotEmpty(message = "メールアドレスは必須です")
	@Email(message = "メールアドレスの形式が不正です")
	private String email;
	
	@NotEmpty(message = "パスワードは必須です")
	private String password;

}
