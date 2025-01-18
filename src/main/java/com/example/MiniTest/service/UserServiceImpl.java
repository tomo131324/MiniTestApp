package com.example.MiniTest.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.config.LoginUserDetails;
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
    
	//ログイン中のユーザーID取得
    @Override
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 認証情報が存在しない、または匿名ユーザーの場合
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null; // 未ログイン時は null を返す
        }
        
        // 認証済みユーザーの場合、型を確認して処理
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetails) {
            return ((LoginUserDetails) principal).getUserId(); // LoginUserDetails から userId を取得
        }
        
        // 予期しないケース（安全のため例外をスローするか、null を返す）
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
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







