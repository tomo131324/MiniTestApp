package com.example.MiniTest.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.MiniTest.entity.User;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class LoginUserDetails implements UserDetails {
	private final String email;
	private final String password;
	private final Integer userId;
	
	public LoginUserDetails(User user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.userId = user.getUser_id();
	}
	
	// ???
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
	
	@Override
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return email;
	}
	
	public Integer getUserId() {
		return userId;
	}
}
