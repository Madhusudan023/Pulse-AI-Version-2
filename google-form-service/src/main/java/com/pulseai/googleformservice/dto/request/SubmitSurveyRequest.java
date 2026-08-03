package com.pulseai.googleformservice.dto.request;

import java.util.List;

public class SubmitSurveyRequest {
    private String responseDuration;
    private List<AnswerRequest> answers;

    public String getResponseDuration() { return responseDuration; }
    public void setResponseDuration(String responseDuration) { this.responseDuration = responseDuration; }
    public List<AnswerRequest> getAnswers() { return answers; }
    public void setAnswers(List<AnswerRequest> answers) { this.answers = answers; }
}
