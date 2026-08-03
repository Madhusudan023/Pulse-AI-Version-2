package com.pulseai.sentimentservice.dto.response;


public class SentimentAnalysisResult {
    private Integer overallScore;
    private Integer positivePercentage;
    private Integer neutralPercentage;
    private Integer negativePercentage;
    private String executiveSummary;
    private java.util.List<String> positiveThemes;
    private java.util.List<String> negativeThemes;
    private java.util.List<String> recommendations;
    private java.util.List<SuggestedQuestion> suggestedQuestions;
    private java.util.List<QuestionAnalysis> questionWiseAnalysis;
    
    public static class QuestionAnalysis {
        private String questionText;
        private Integer positivePercentage;
        private Integer neutralPercentage;
        private Integer negativePercentage;
        private String summary;
        
        public QuestionAnalysis() {}
        
        public String getQuestionText() { return this.questionText; }
        public Integer getPositivePercentage() { return this.positivePercentage; }
        public Integer getNeutralPercentage() { return this.neutralPercentage; }
        public Integer getNegativePercentage() { return this.negativePercentage; }
        public String getSummary() { return this.summary; }
        
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public void setPositivePercentage(Integer positivePercentage) { this.positivePercentage = positivePercentage; }
        public void setNeutralPercentage(Integer neutralPercentage) { this.neutralPercentage = neutralPercentage; }
        public void setNegativePercentage(Integer negativePercentage) { this.negativePercentage = negativePercentage; }
        public void setSummary(String summary) { this.summary = summary; }
    }
    
    public static class SuggestedQuestion {
        private String questionText;
        private String category;
        private String questionType;
        
        public SuggestedQuestion() {}
        
        public String getQuestionText() { return this.questionText; }
        public String getCategory() { return this.category; }
        public String getQuestionType() { return this.questionType; }
        
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public void setCategory(String category) { this.category = category; }
        public void setQuestionType(String questionType) { this.questionType = questionType; }
    }
    public SentimentAnalysisResult() {}
    public Integer getOverallScore() { return this.overallScore; }
    public Integer getPositivePercentage() { return this.positivePercentage; }
    public Integer getNeutralPercentage() { return this.neutralPercentage; }
    public Integer getNegativePercentage() { return this.negativePercentage; }
    public String getExecutiveSummary() { return this.executiveSummary; }
    public java.util.List<String> getPositiveThemes() { return this.positiveThemes; }
    public java.util.List<String> getNegativeThemes() { return this.negativeThemes; }
    public java.util.List<String> getRecommendations() { return this.recommendations; }
    public java.util.List<SuggestedQuestion> getSuggestedQuestions() { return this.suggestedQuestions; }
    public java.util.List<QuestionAnalysis> getQuestionWiseAnalysis() { return this.questionWiseAnalysis; }
    
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    public void setPositivePercentage(Integer positivePercentage) { this.positivePercentage = positivePercentage; }
    public void setNeutralPercentage(Integer neutralPercentage) { this.neutralPercentage = neutralPercentage; }
    public void setNegativePercentage(Integer negativePercentage) { this.negativePercentage = negativePercentage; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    public void setPositiveThemes(java.util.List<String> positiveThemes) { this.positiveThemes = positiveThemes; }
    public void setNegativeThemes(java.util.List<String> negativeThemes) { this.negativeThemes = negativeThemes; }
    public void setRecommendations(java.util.List<String> recommendations) { this.recommendations = recommendations; }
    public void setSuggestedQuestions(java.util.List<SuggestedQuestion> suggestedQuestions) { this.suggestedQuestions = suggestedQuestions; }
    public void setQuestionWiseAnalysis(java.util.List<QuestionAnalysis> questionWiseAnalysis) { this.questionWiseAnalysis = questionWiseAnalysis; }
}
