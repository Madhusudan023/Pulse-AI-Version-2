package com.pulseai.googleformservice.dto.event;

public class SurveyClosedEvent {
    private Long surveyId;
    private String title;

    public SurveyClosedEvent() {
    }

    public SurveyClosedEvent(Long surveyId, String title) {
        this.surveyId = surveyId;
        this.title = title;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
