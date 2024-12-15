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
	
    @JsonProperty("Question1")
    private String question1;

    @JsonProperty("Answer1")
    private String answer1;

    @JsonProperty("Choices1")
    private List<String> choices1;

    @JsonProperty("Question2")
    private String question2;

    @JsonProperty("Answer2")
    private String answer2;

    @JsonProperty("Choices2")
    private List<String> choices2;
    
    @JsonProperty("Question3")
    private String question3;

    @JsonProperty("Answer3")
    private String answer3;

    @JsonProperty("Choices3")
    private List<String> choices3;

    @JsonProperty("Question4")
    private String question4;

    @JsonProperty("Answer4")
    private String answer4;

    @JsonProperty("Choices4")
    private List<String> choices4;

    @JsonProperty("Question5")
    private String question5;

    @JsonProperty("Answer5")
    private String answer5;

    @JsonProperty("Choices5")
    private List<String> choices5;

    @JsonProperty("Question6")
    private String question6;

    @JsonProperty("Answer6")
    private String answer6;

    @JsonProperty("Choices6")
    private List<String> choices6;

    @JsonProperty("Question7")
    private String question7;

    @JsonProperty("Answer7")
    private String answer7;

    @JsonProperty("Choices7")
    private List<String> choices7;
    
    @JsonProperty("Question8")
    private String question8;

    @JsonProperty("Answer8")
    private String answer8;

    @JsonProperty("Choices8")
    private List<String> choices8;
    
    @JsonProperty("Question9")
    private String question9;

    @JsonProperty("Answer9")
    private String answer9;

    @JsonProperty("Choices9")
    private List<String> choices9;

    @JsonProperty("Question10")
    private String question10;

    @JsonProperty("Answer10")
    private String answer10;

    @JsonProperty("Choices10")
    private List<String> choices10;

}
