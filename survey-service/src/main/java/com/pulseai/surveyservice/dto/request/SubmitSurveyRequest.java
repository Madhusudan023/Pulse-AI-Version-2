package com.pulseai.surveyservice.dto.request;

import java.util.List;

public class SubmitSurveyRequest {
    private String responseDuration;
    private List<AnswerRequest> answers;
    public SubmitSurveyRequest() {}
    public String getResponseDuration() { return this.responseDuration; }
    public List<AnswerRequest> getAnswers() { return this.answers; }
    public void setResponseDuration(String responseDuration) { this.responseDuration = responseDuration; }
    public void setAnswers(List<AnswerRequest> answers) { this.answers = answers; }
}
