package com.pulseai.questionbank.entity;

import com.pulseai.questionbank.enums.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @Enumerated(EnumType.STRING)
    private QuestionCategory category;

    @Enumerated(EnumType.STRING)
    private QuestionSource source;

    @Enumerated(EnumType.STRING)
    private QuestionStatus status = QuestionStatus.DRAFT;

    private String region;
    
    private Integer month;
    
    private Integer year;

    @Enumerated(EnumType.STRING)
    private SurveyType surveyType;

    private Integer version = 1;
    
    private String remarks;

    private Integer usageCount = 0;

    @jakarta.persistence.ElementCollection
    @jakarta.persistence.CollectionTable(name = "question_options", joinColumns = @jakarta.persistence.JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    private java.util.List<String> options = new java.util.ArrayList<>();

    // Flexible Rating Scale (defaults: Positive 8-10, Neutral 5-7, Negative 1-4)
    private Integer positiveFrom = 8;
    private Integer positiveTo   = 10;
    private Integer neutralFrom  = 5;
    private Integer neutralTo    = 7;
    private Integer negativeFrom = 1;
    private Integer negativeTo   = 4;

    public String getQuestionText() { return this.questionText; }
    public QuestionType getQuestionType() { return this.questionType; }
    public QuestionCategory getCategory() { return this.category; }
    public QuestionSource getSource() { return this.source; }
    public QuestionStatus getStatus() { return this.status; }
    public String getRegion() { return this.region; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public SurveyType getSurveyType() { return this.surveyType; }
    public Integer getVersion() { return this.version; }
    public String getRemarks() { return this.remarks; }
    public Integer getUsageCount() { return this.usageCount; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
    public void setCategory(QuestionCategory category) { this.category = category; }
    public void setSource(QuestionSource source) { this.source = source; }
    public void setStatus(QuestionStatus status) { this.status = status; }
    public void setRegion(String region) { this.region = region; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }
    public void setVersion(Integer version) { this.version = version; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
    public java.util.List<String> getOptions() { return options; }
    public void setOptions(java.util.List<String> options) { this.options = options; }
    public Integer getPositiveFrom() { return positiveFrom; }
    public Integer getPositiveTo()   { return positiveTo; }
    public Integer getNeutralFrom()  { return neutralFrom; }
    public Integer getNeutralTo()    { return neutralTo; }
    public Integer getNegativeFrom() { return negativeFrom; }
    public Integer getNegativeTo()   { return negativeTo; }
    public void setPositiveFrom(Integer positiveFrom) { this.positiveFrom = positiveFrom; }
    public void setPositiveTo(Integer positiveTo)     { this.positiveTo = positiveTo; }
    public void setNeutralFrom(Integer neutralFrom)   { this.neutralFrom = neutralFrom; }
    public void setNeutralTo(Integer neutralTo)       { this.neutralTo = neutralTo; }
    public void setNegativeFrom(Integer negativeFrom) { this.negativeFrom = negativeFrom; }
    public void setNegativeTo(Integer negativeTo)     { this.negativeTo = negativeTo; }
}
