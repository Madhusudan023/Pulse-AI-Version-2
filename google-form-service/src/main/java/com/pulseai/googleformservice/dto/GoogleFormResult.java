package com.pulseai.googleformservice.dto;

public class GoogleFormResult {
    private String formId;
    private String formUrl;
    public GoogleFormResult(String formId, String formUrl) {
        this.formId = formId;
        this.formUrl = formUrl;
    }
    public String getFormId() { return formId; }
    public String getFormUrl() { return formUrl; }
}
