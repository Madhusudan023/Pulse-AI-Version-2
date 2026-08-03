package com.pulseai.surveyservice.dto.request;

import java.util.List;

public class AddQuestionsBulkRequest {
    private List<Long> questionIds;
    public AddQuestionsBulkRequest() {}
    public List<Long> getQuestionIds() { return this.questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }
}
