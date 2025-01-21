package com.example.MiniTest.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/* 新規登録フォームに入力された値を格納するためのクラス */
public class RegisterForm {
    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "正しいメールアドレス形式で入力してください。")
    private String email;
	
    @NotBlank(message = "パスワードを入力してください。")
    @Size(min = 8, max = 20, message = "パスワードは8文字以上20文字以内で入力してください。")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[A-Za-z\\d]{8,20}$",
        message = "パスワードは半角英数字のみ、大文字、小文字、数字を1文字以上含める必要があります。"
    )
    private String password;

}
