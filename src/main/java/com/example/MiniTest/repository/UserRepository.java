package com.example.MiniTest.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import com.example.MiniTest.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	Optional<User> findByEmail(String email);
}