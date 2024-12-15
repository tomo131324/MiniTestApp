
package com.example.MiniTest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.entity.User;
import com.example.MiniTest.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(User user) {
    	String password = passwordEncoder.encode(user.getPassword());
    	user.setPassword(password);
        userRepository.save(user);
    }
}
