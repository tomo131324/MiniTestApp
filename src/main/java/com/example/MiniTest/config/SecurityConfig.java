package com.example.MiniTest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration	// Springの設定クラスであることを示すアノテーション
public class SecurityConfig {
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();	// BCryptアルゴリズムを利用するPasswordEncoderを返す
	}

	@Bean	// Springコンテナに登録されるBean定義
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/ocr")// /ocrエンドポイントでCSRF保護を無効化
            .ignoringRequestMatchers("/create")
            .ignoringRequestMatchers("/scoring")
            .ignoringRequestMatchers("/insert")
            .ignoringRequestMatchers("/createMiniTest")
            .ignoringRequestMatchers("/updateMiniTest")
        )
        .formLogin(login -> login
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/css/**").permitAll()
            .requestMatchers("/js/**").permitAll()
            .requestMatchers("/").permitAll()
            .requestMatchers("/guide").permitAll()
            .requestMatchers("/register/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
	
	
}
