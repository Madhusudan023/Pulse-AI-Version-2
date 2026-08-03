package com.pulseai.surveyservice.dto.response;


public class QuestionResponseDTO {
    private Long id;
    private String questionText;
    private String questionType;
    private String category;
    public QuestionResponseDTO() {}
    public Long getId() { return this.id; }
    public String getQuestionText() { return this.questionText; }
    public String getQuestionType() { return this.questionType; }
    public String getCategory() { return this.category; }
    public void setId(Long id) { this.id = id; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public void setCategory(String category) { this.category = category; }
}
