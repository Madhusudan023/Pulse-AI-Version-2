package com.pulseai.surveyservice.dto.request;

import java.util.List;

public class PublishSurveyRequest {
    private List<String> customEmails;

    public List<String> getCustomEmails() {
        return customEmails;
    }

    public void setCustomEmails(List<String> customEmails) {
        this.customEmails = customEmails;
    }
}
