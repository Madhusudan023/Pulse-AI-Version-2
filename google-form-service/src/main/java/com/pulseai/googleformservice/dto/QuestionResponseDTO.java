package com.pulseai.googleformservice.dto;

import com.pulseai.googleformservice.enums.QuestionType;

public class QuestionResponseDTO {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private Integer positiveTo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
    public Integer getPositiveTo() { return positiveTo; }
    public void setPositiveTo(Integer positiveTo) { this.positiveTo = positiveTo; }
}
