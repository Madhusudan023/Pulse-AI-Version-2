package com.pulseai.authservice.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    private String timestamp = java.time.LocalDateTime.now().toString();
    private String traceId;

    public ApiResponse() {}
    public ApiResponse(boolean success, String message, T data, String timestamp, String traceId) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }
    public boolean isSuccess() { return this.success; }
    public String getMessage() { return this.message; }
    public T getData() { return this.data; }
    public String getTimestamp() { return this.timestamp; }
    public String getTraceId() { return this.traceId; }
    
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public static <T> ApiResponseBuilder<T> builder() { return new ApiResponseBuilder<>(); }
    
    public static class ApiResponseBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private String timestamp;
        private String traceId;
        
        public ApiResponseBuilder<T> success(boolean success) { this.success = success; return this; }
        public ApiResponseBuilder<T> message(String message) { this.message = message; return this; }
        public ApiResponseBuilder<T> data(T data) { this.data = data; return this; }
        public ApiResponseBuilder<T> timestamp(String timestamp) { this.timestamp = timestamp; return this; }
        public ApiResponseBuilder<T> traceId(String traceId) { this.traceId = traceId; return this; }
        
        public ApiResponse<T> build() { return new ApiResponse<>(this.success, this.message, this.data, this.timestamp, this.traceId); }
    }
}
