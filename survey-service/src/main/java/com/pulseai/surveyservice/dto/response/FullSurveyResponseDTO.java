package com.pulseai.surveyservice.dto.response;

import com.pulseai.surveyservice.entity.Answer;
import com.pulseai.surveyservice.entity.SurveyResponse;

import java.util.List;

public class FullSurveyResponseDTO {
    private SurveyResponse response;
    private List<Answer> answers;
    public FullSurveyResponseDTO() {}
    public SurveyResponse getResponse() { return this.response; }
    public List<Answer> getAnswers() { return this.answers; }
    public void setResponse(SurveyResponse response) { this.response = response; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
}
