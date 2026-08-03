package com.pulseai.surveyservice.dto.event;


public class SurveyReminderEvent {
    private Long surveyId;
    private String region;
    private String title;
    private java.util.List<Long> employeeIds;
    public SurveyReminderEvent() {}
    public SurveyReminderEvent(Long surveyId, String region, String title, java.util.List<Long> employeeIds) {
        this.surveyId = surveyId;
        this.region = region;
        this.title = title;
        this.employeeIds = employeeIds;
    }
    public Long getSurveyId() { return this.surveyId; }
    public String getRegion() { return this.region; }
    public String getTitle() { return this.title; }
    public java.util.List<Long> getEmployeeIds() { return this.employeeIds; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }
    public void setRegion(String region) { this.region = region; }
    public void setTitle(String title) { this.title = title; }
    public void setEmployeeIds(java.util.List<Long> employeeIds) { this.employeeIds = employeeIds; }
    public static  SurveyReminderEventBuilder builder() { return new SurveyReminderEventBuilder(); }
    public static class SurveyReminderEventBuilder {
        private Long surveyId;
        private String region;
        private String title;
        private java.util.List<Long> employeeIds;
        public SurveyReminderEventBuilder surveyId(Long surveyId) { this.surveyId = surveyId; return this; }
        public SurveyReminderEventBuilder region(String region) { this.region = region; return this; }
        public SurveyReminderEventBuilder title(String title) { this.title = title; return this; }
        public SurveyReminderEventBuilder employeeIds(java.util.List<Long> employeeIds) { this.employeeIds = employeeIds; return this; }
        public SurveyReminderEvent build() { return new SurveyReminderEvent(this.surveyId, this.region, this.title, this.employeeIds); }
    }
}
