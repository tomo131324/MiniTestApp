package com.example.MiniTest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration	// Springの設定クラスであることを示すアノテーション
public class SecurityConfig {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();	// BCryptアルゴリズムを利用するPasswordEncoderを返す
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
	     return configuration.getAuthenticationManager();
	    }
	
	@Bean	// Springコンテナに登録されるBean定義
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/ocr", "/create", "/scoring", "/{testId}", "/{testId}/delete")// /ocrエンドポイントでCSRF保護を無効化
        )
        .formLogin(login -> login
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
        )
        .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/css/**", "/js/**", "/register/**", "/password-reset").permitAll() // トップページのパス `/` を許可
                .anyRequest().authenticated()
            );
return http.build();
}
	
	
}
