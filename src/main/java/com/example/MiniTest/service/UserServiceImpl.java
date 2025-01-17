package com.example.MiniTest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.entity.User;
import com.example.MiniTest.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
        // return userRepository.findByEmail(email) != null;
    }
    
    // 新規登録
    @Override
    public void createUser(User user) {
    	String password = passwordEncoder.encode(user.getPassword());
    	user.setPassword(password);
        userRepository.save(user);
    }
    
    // メールアドレスでユーザーを検索
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // ユーザー情報を保存
    @Override
    public void save(User user) {
        userRepository.save(user);
    }
    
    // パスワードを変更する
    @Override
    public boolean changePassword(String email, String newPassword) {
        // メールアドレスでユーザーを検索
        return userRepository.findByEmail(email).map(user -> {
            // 新しいパスワードをハッシュ化
            String hashedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(hashedPassword); // ハッシュ化したパスワードをセット
            userRepository.save(user); // データベースを更新
            return true; // 成功
        }).orElse(false); // ユーザーが見つからなかった場合は false
    }
}







