package com.pulseai.sentimentservice.dto.request;


public class CreateQuestionRequest {
    private String questionText;
    private String questionType;
    private String category;
    private String region;
    private Integer month;
    private Integer year;
    private String surveyType;
    private String remarks;
    public CreateQuestionRequest() {}
    public CreateQuestionRequest(String questionText, String questionType, String category, String region, Integer month, Integer year, String surveyType, String remarks) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.category = category;
        this.region = region;
        this.month = month;
        this.year = year;
        this.surveyType = surveyType;
        this.remarks = remarks;
    }
    public String getQuestionText() { return this.questionText; }
    public String getQuestionType() { return this.questionType; }
    public String getCategory() { return this.category; }
    public String getRegion() { return this.region; }
    public Integer getMonth() { return this.month; }
    public Integer getYear() { return this.year; }
    public String getSurveyType() { return this.surveyType; }
    public String getRemarks() { return this.remarks; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public void setCategory(String category) { this.category = category; }
    public void setRegion(String region) { this.region = region; }
    public void setMonth(Integer month) { this.month = month; }
    public void setYear(Integer year) { this.year = year; }
    public void setSurveyType(String surveyType) { this.surveyType = surveyType; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public static  CreateQuestionRequestBuilder builder() { return new CreateQuestionRequestBuilder(); }
    public static class CreateQuestionRequestBuilder {
        private String questionText;
        private String questionType;
        private String category;
        private String region;
        private Integer month;
        private Integer year;
        private String surveyType;
        private String remarks;
        public CreateQuestionRequestBuilder questionText(String questionText) { this.questionText = questionText; return this; }
        public CreateQuestionRequestBuilder questionType(String questionType) { this.questionType = questionType; return this; }
        public CreateQuestionRequestBuilder category(String category) { this.category = category; return this; }
        public CreateQuestionRequestBuilder region(String region) { this.region = region; return this; }
        public CreateQuestionRequestBuilder month(Integer month) { this.month = month; return this; }
        public CreateQuestionRequestBuilder year(Integer year) { this.year = year; return this; }
        public CreateQuestionRequestBuilder surveyType(String surveyType) { this.surveyType = surveyType; return this; }
        public CreateQuestionRequestBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public CreateQuestionRequest build() { return new CreateQuestionRequest(this.questionText, this.questionType, this.category, this.region, this.month, this.year, this.surveyType, this.remarks); }
    }
}
