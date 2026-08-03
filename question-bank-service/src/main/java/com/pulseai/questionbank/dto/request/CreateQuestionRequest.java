package com.pulseai.questionbank.dto.request;

import com.pulseai.questionbank.enums.QuestionCategory;
import com.pulseai.questionbank.enums.QuestionType;
import com.pulseai.questionbank.enums.SurveyType;

public class CreateQuestionRequest {
    private String questionText;
    private QuestionType questionType;
    private QuestionCategory category;
    private String region;
    private Integer month;
    private Integer year;
    private SurveyType surveyType;
    private String remarks;
    private java.util.List<String> options;
    public CreateQuestionRequest() {}
    public String getQuestionText() { return this.questionText; }
    public QuestionType getQuestionType() { return this.questionType; }
    public QuestionCategory getCategory() { return this.category; }
    public String getRegion() { return this.region; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public SurveyType getSurveyType() { return this.surveyType; }
    public String getRemarks() { return this.remarks; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
    public void setCategory(QuestionCategory category) { this.category = category; }
    public void setRegion(String region) { this.region = region; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public java.util.List<String> getOptions() { return options; }
    public void setOptions(java.util.List<String> options) { this.options = options; }
}
