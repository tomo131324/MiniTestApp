package com.example.MiniTest.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.MiniTest.entity.Question;


public interface QuestionRepository extends CrudRepository<Question, Integer>{
	 Iterable<Question> findByUserIdAndTestId(Integer userId, Integer testId);
	 
	 @Modifying
	 @Query("DELETE FROM Question q WHERE q.test_id = :testId AND q.user_id = :userId")
	 int deleteByTestIdAndUserId(Integer testId,Integer userId);

	 
	 //最新のテストIDを取得
	 @Query("SELECT MAX(q.test_id) FROM Question q WHERE q.user_id = :userId")
	 Optional<Integer> findMaxTestId(Integer userId);
	 
	 Iterable<Question> findByUserId(Integer userId);
	 
}
