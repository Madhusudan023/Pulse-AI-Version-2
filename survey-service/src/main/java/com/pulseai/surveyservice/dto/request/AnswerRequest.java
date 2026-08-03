package com.pulseai.surveyservice.dto.request;


public class AnswerRequest {
    private Long questionId;
    private Integer ratingAnswer;
    private String textAnswer;
    private String optionAnswer;
    public AnswerRequest() {}
    public Long getQuestionId() { return this.questionId; }
    public Integer getRatingAnswer() { return this.ratingAnswer; }
    public String getTextAnswer() { return this.textAnswer; }
    public String getOptionAnswer() { return this.optionAnswer; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public void setRatingAnswer(Integer ratingAnswer) { this.ratingAnswer = ratingAnswer; }
    public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    public void setOptionAnswer(String optionAnswer) { this.optionAnswer = optionAnswer; }
}
