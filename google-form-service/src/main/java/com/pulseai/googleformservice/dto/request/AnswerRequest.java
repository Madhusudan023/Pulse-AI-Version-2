package com.pulseai.googleformservice.dto.request;

public class AnswerRequest {
    private Long questionId;
    private Integer ratingAnswer;
    private String textAnswer;
    private String optionAnswer;

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }
    public Integer getRatingAnswer() { return ratingAnswer; }
    public void setRatingAnswer(Integer ratingAnswer) { this.ratingAnswer = ratingAnswer; }
    public String getTextAnswer() { return textAnswer; }
    public void setTextAnswer(String textAnswer) { this.textAnswer = textAnswer; }
    public String getOptionAnswer() { return optionAnswer; }
    public void setOptionAnswer(String optionAnswer) { this.optionAnswer = optionAnswer; }
}
