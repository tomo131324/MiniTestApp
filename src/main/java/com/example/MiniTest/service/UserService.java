
package com.example.MiniTest.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.MiniTest.entity.User;

@Service
public interface UserService {
	
	boolean emailExists(String email);

	void createUser(User user);
	
	Integer getCurrentUserId();
	
	Optional<User> findByEmail(String email);
	
	void save(User user);
	
	boolean changePassword(String email, String newPassword);
   
}
