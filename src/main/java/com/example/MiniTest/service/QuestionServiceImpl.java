package com.example.MiniTest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MiniTest.config.LoginUserDetails;
import com.example.MiniTest.entity.Question;

import com.example.MiniTest.repository.QuestionRepository;


@Service
@Transactional
public class QuestionServiceImpl implements QuestionService{
	
	@Autowired
	QuestionRepository questionRepository;
	
	
	//テスト内全件取得
	@Override
	public Iterable<Question> getQuestions(Integer userId,Integer testId){
		return questionRepository.findByUserIdAndTestId(userId,testId);
    }
	
	
    //ユーザーごとの最新のテストID取得
    @Override
    public Integer getLatestTestId(Integer userId) {
    	return questionRepository.findMaxTestId(userId).orElse(0);
    }
    
	//全件取得
	@Override
	public Iterable<Question> getQuestions(Integer userId){
		return questionRepository.findByUserId(userId);
	}
    
	//採点
	@Override
	public List<Boolean> scoring(List<Object> userAnswers, Integer testId, Integer userId){
		List<Boolean> correction = new ArrayList<Boolean>();
		List<String> answers = new ArrayList<String>();
		if (testId == null) {
			testId = getLatestTestId(userId);
		}
		
		Iterable<Question> questions = getQuestions(userId,testId);
		
		// answersに正解の答えを入れる
	    for (Question question : questions) {
	    	answers.add(question.getAnswer());
	    	}
	    
	    // 正解の答えとユーザーの答えを比較
	    for (int i = 0; i < answers.size(); i++) {
	        if (i < userAnswers.size() && answers.get(i).equals(userAnswers.get(i))) {
	        	correction.add(true);
	        } else {
	            correction.add(false);
	        }
	    }
	    
	    return correction;
	}
	
	//問題文抽出
	@Override
    public List<Map<String, Object>> getFirstQuestions(Integer userId) {
        // 結果リストを作成
        List<Map<String, Object>> result = new ArrayList<>();
        
		if (userId != null) {
        // 全データを取得
        Iterable<Question> allQuestions = getQuestions(userId);

        // userId と testId ごとに最初の質問を保存するマップ
        Map<String, Question> firstQuestionMap = new HashMap<>();

        // データをループして userId と testId ごとに最初の質問を取得
        for (Question question : allQuestions) {
            String key = question.getUserId() + "-" + question.getTestId();

            if (!firstQuestionMap.containsKey(key)) {
                firstQuestionMap.put(key, question);
            } else {
                Question existingQuestion = firstQuestionMap.get(key);
                if (question.getCreatedAt().compareTo(existingQuestion.getCreatedAt()) < 0) {
                    firstQuestionMap.put(key, question);
                }
            }
        }

        for (Map.Entry<String, Question> entry : firstQuestionMap.entrySet()) {
            Question question = entry.getValue();
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("question", question.getQuestion().substring(0, Math.min(10, question.getQuestion().length())));
            resultMap.put("testId", question.getTestId());
            result.add(resultMap);
        }
        
        // testId を降順にソート
        Collections.sort(result, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> map1, Map<String, Object> map2) {
                Integer testId1 = (Integer) map1.get("testId");
                Integer testId2 = (Integer) map2.get("testId");
                return testId2.compareTo(testId1); // 降順にソート
            }
        });
    }
        return result;
    }
}