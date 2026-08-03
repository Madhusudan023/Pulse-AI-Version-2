package com.pulseai.surveyservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "survey_questions")
public class SurveyQuestion extends BaseEntity {

    private Long surveyId;
    
    private Long questionId;
    
    private Integer displayOrder;
    public Long getSurveyId() { return this.surveyId; }
    public Long getQuestionId() { return this.questionId; }
    public Integer getDisplayOrder() { return this.displayOrder; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
