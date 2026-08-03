package com.pulseai.questionbank.dto.request;

import com.pulseai.questionbank.enums.QuestionCategory;
import com.pulseai.questionbank.enums.QuestionType;

public class UpdateQuestionRequest {
    private String questionText;
    private QuestionType questionType;
    private QuestionCategory category;
    private String remarks;
    // Flexible Rating Scale (nullable — defaults applied server-side)
    private Integer positiveFrom;
    private Integer positiveTo;
    private Integer neutralFrom;
    private Integer neutralTo;
    private Integer negativeFrom;
    private Integer negativeTo;
    private com.pulseai.questionbank.enums.SurveyType surveyType;
    private java.util.List<String> options;
    public UpdateQuestionRequest() {}
    public String getQuestionText() { return this.questionText; }
    public QuestionType getQuestionType() { return this.questionType; }
    public QuestionCategory getCategory() { return this.category; }
    public String getRemarks() { return this.remarks; }
    public Integer getPositiveFrom() { return positiveFrom; }
    public Integer getPositiveTo()   { return positiveTo; }
    public Integer getNeutralFrom()  { return neutralFrom; }
    public Integer getNeutralTo()    { return neutralTo; }
    public Integer getNegativeFrom() { return negativeFrom; }
    public Integer getNegativeTo()   { return negativeTo; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
    public void setCategory(QuestionCategory category) { this.category = category; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public void setPositiveFrom(Integer positiveFrom) { this.positiveFrom = positiveFrom; }
    public void setPositiveTo(Integer positiveTo)     { this.positiveTo = positiveTo; }
    public void setNeutralFrom(Integer neutralFrom)   { this.neutralFrom = neutralFrom; }
    public void setNeutralTo(Integer neutralTo)       { this.neutralTo = neutralTo; }
    public void setNegativeFrom(Integer negativeFrom) { this.negativeFrom = negativeFrom; }
    public void setNegativeTo(Integer negativeTo)     { this.negativeTo = negativeTo; }
    public com.pulseai.questionbank.enums.SurveyType getSurveyType() { return surveyType; }
    public void setSurveyType(com.pulseai.questionbank.enums.SurveyType surveyType) { this.surveyType = surveyType; }
    public java.util.List<String> getOptions() { return options; }
    public void setOptions(java.util.List<String> options) { this.options = options; }
}
