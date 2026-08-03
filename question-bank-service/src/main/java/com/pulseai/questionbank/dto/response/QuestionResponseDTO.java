package com.pulseai.questionbank.dto.response;

import com.pulseai.questionbank.enums.*;

import java.time.LocalDateTime;

public class QuestionResponseDTO {
    private Long id;
    private String questionText;
    private QuestionType questionType;
    private QuestionCategory category;
    private QuestionSource source;
    private QuestionStatus status;
    private String region;
    private Integer month;
    private Integer year;
    private SurveyType surveyType;
    private Integer version;
    private String remarks;
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    // Flexible Rating Scale
    private Integer positiveFrom;
    private Integer positiveTo;
    private Integer neutralFrom;
    private Integer neutralTo;
    private Integer negativeFrom;
    private Integer negativeTo;
    private java.util.List<String> options;
    public QuestionResponseDTO() {}
    public Long getId() { return this.id; }
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
    public LocalDateTime getCreatedAt() { return this.createdAt; }
    public LocalDateTime getUpdatedAt() { return this.updatedAt; }
    public String getCreatedBy() { return this.createdBy; }
    public void setId(Long id) { this.id = id; }
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
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
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
    public java.util.List<String> getOptions() { return options; }
    public void setOptions(java.util.List<String> options) { this.options = options; }
}
