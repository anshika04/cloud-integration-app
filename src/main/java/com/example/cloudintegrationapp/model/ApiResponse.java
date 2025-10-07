package com.example.cloudintegrationapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String referenceId;
    private String error;
    private List<String> errors;
    private Map<String, Object> metadata;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private Integer statusCode;
    
    // Default constructor
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    // Success constructors
    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this(success, message);
        this.data = data;
    }
    
    public ApiResponse(boolean success, String message, T data, String referenceId) {
        this(success, message, data);
        this.referenceId = referenceId;
    }
    
    // Error constructors
    public ApiResponse(String error) {
        this();
        this.success = false;
        this.error = error;
    }
    
    public ApiResponse(String error, List<String> errors) {
        this(error);
        this.errors = errors;
    }
    
    // Static factory methods
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data, String referenceId) {
        return new ApiResponse<>(true, message, data, referenceId);
    }
    
    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(error);
    }
    
    public static <T> ApiResponse<T> error(String error, List<String> errors) {
        return new ApiResponse<>(error, errors);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", referenceId='" + referenceId + '\'' +
                ", error='" + error + '\'' +
                ", errors=" + errors +
                ", metadata=" + metadata +
                ", timestamp=" + timestamp +
                ", statusCode=" + statusCode +
                '}';
    }
}
