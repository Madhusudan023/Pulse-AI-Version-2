package com.pulseai.surveyservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "answers")
public class Answer extends BaseEntity {

    private Long responseId;
    
    private Long questionId;
    
    private Integer ratingAnswer;
    
    @Column(columnDefinition = "TEXT")
    private String textAnswer;
    
    private String optionAnswer;
    public Long getResponseId() { return this.responseId; }
    public Long getQuestionId() { return this.questionId; }
    public Integer getRatingAnswer() { return this.ratingAnswer; }
    public String getTextAnswer() { return this.textAnswer; }
    public String getOptionAnswer() { return this.optionAnswer; }
    public void setResponseId(Long responseId) { this.responseId = responseId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setRatingAnswer(Integer ratingAnswer) { this.ratingAnswer = ratingAnswer; }
    public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    public void setOptionAnswer(String optionAnswer) { this.optionAnswer = optionAnswer; }
}
