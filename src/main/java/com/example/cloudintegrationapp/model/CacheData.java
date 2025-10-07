package com.example.cloudintegrationapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class CacheData {
    
    @JsonProperty("reference_id")
    private String referenceId;
    
    @JsonProperty("data_type")
    private String dataType;
    
    @JsonProperty("content")
    private Object content;
    
    @JsonProperty("metadata")
    private String metadata;
    
    @JsonProperty("ttl_seconds")
    private Long ttlSeconds;
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonProperty("expires_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;
    
    // Default constructor
    public CacheData() {}
    
    // Constructor with required fields
    public CacheData(String referenceId, String dataType, Object content) {
        this.referenceId = referenceId;
        this.dataType = dataType;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.ttlSeconds = 3600L; // Default 1 hour TTL
        this.expiresAt = this.createdAt.plusSeconds(this.ttlSeconds);
    }
    
    // Constructor with TTL
    public CacheData(String referenceId, String dataType, Object content, Long ttlSeconds) {
        this.referenceId = referenceId;
        this.dataType = dataType;
        this.content = content;
        this.ttlSeconds = ttlSeconds;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusSeconds(this.ttlSeconds);
    }
    
    // Getters and Setters
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getDataType() {
        return dataType;
    }
    
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public Object getContent() {
        return content;
    }
    
    public void setContent(Object content) {
        this.content = content;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public Long getTtlSeconds() {
        return ttlSeconds;
    }
    
    public void setTtlSeconds(Long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
        if (createdAt != null) {
            this.expiresAt = this.createdAt.plusSeconds(this.ttlSeconds);
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    // Utility methods
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public long getRemainingTtl() {
        if (expiresAt == null) return 0;
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) return 0;
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheData cacheData = (CacheData) o;
        return Objects.equals(referenceId, cacheData.referenceId) && 
               Objects.equals(dataType, cacheData.dataType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(referenceId, dataType);
    }
    
    @Override
    public String toString() {
        return "CacheData{" +
                "referenceId='" + referenceId + '\'' +
                ", dataType='" + dataType + '\'' +
                ", content=" + content +
                ", metadata='" + metadata + '\'' +
                ", ttlSeconds=" + ttlSeconds +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
