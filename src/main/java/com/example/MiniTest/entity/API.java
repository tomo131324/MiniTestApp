package com.example.MiniTest.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class API {
    @JsonProperty("Question")
    private String question;

    @JsonProperty("Answer")
    private String answer;
    
    @JsonProperty("Answers")
    private List<String> answers;
    
    @JsonProperty("Choices")
    private List<String> choices;
    
    private Integer testId;
}