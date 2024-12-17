package com.example.MiniTest.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.config.LoginUserDetails;
import com.example.MiniTest.entity.Question;
import com.example.MiniTest.repository.QuestionRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@Transactional
public class SaveServiceImpl implements SaveService {
	
    @Autowired
    QuestionRepository questionRepository;
   
    @Override
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
        return userDetails.getUserId(); // ログイン中のユーザーIDを取得
    }
	
	//問題保存
    @Override
    public void insert(List<Question> questions) {
        questionRepository.saveAll(questions);
    }
    
    


}
