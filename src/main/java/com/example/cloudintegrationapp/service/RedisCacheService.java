package com.example.cloudintegrationapp.service;

import com.example.cloudintegrationapp.model.CacheData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final ValueOperations<String, String> valueOperations;
    
    public RedisCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }
    
    // Store data with reference ID
    public boolean storeData(String referenceId, CacheData cacheData) {
        try {
            String key = generateKey(referenceId);
            String jsonData = objectMapper.writeValueAsString(cacheData);
            
            if (cacheData.getTtlSeconds() != null && cacheData.getTtlSeconds() > 0) {
                valueOperations.set(key, jsonData, Duration.ofSeconds(cacheData.getTtlSeconds()));
                logger.info("Stored data with reference ID: {} and TTL: {} seconds", referenceId, cacheData.getTtlSeconds());
            } else {
                valueOperations.set(key, jsonData);
                logger.info("Stored data with reference ID: {} (no TTL)", referenceId);
            }
            
            return true;
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize data for reference ID: {}", referenceId, e);
            return false;
        } catch (Exception e) {
            logger.error("Failed to store data with reference ID: {}", referenceId, e);
            return false;
        }
    }
    
    // Retrieve data by reference ID
    public Optional<CacheData> getData(String referenceId) {
        try {
            String key = generateKey(referenceId);
            String jsonData = valueOperations.get(key);
            
            if (jsonData == null) {
                logger.debug("No data found for reference ID: {}", referenceId);
                return Optional.empty();
            }
            
            CacheData cacheData = objectMapper.readValue(jsonData, CacheData.class);
            
            // Check if data is expired
            if (cacheData.isExpired()) {
                logger.info("Data expired for reference ID: {}, removing from cache", referenceId);
                deleteData(referenceId);
                return Optional.empty();
            }
            
            logger.debug("Retrieved data for reference ID: {}", referenceId);
            return Optional.of(cacheData);
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize data for reference ID: {}", referenceId, e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to retrieve data with reference ID: {}", referenceId, e);
            return Optional.empty();
        }
    }
    
    // Store simple key-value pair
    public boolean storeValue(String key, Object value) {
        return storeValue(key, value, null);
    }
    
    // Store simple key-value pair with TTL
    public boolean storeValue(String key, Object value, Long ttlSeconds) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            
            if (ttlSeconds != null && ttlSeconds > 0) {
                valueOperations.set(key, jsonValue, Duration.ofSeconds(ttlSeconds));
                logger.debug("Stored value for key: {} with TTL: {} seconds", key, ttlSeconds);
            } else {
                valueOperations.set(key, jsonValue);
                logger.debug("Stored value for key: {} (no TTL)", key);
            }
            
            return true;
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize value for key: {}", key, e);
            return false;
        } catch (Exception e) {
            logger.error("Failed to store value for key: {}", key, e);
            return false;
        }
    }
    
    // Retrieve simple value
    public <T> Optional<T> getValue(String key, Class<T> valueType) {
        try {
            String jsonValue = valueOperations.get(key);
            
            if (jsonValue == null) {
                logger.debug("No value found for key: {}", key);
                return Optional.empty();
            }
            
            T value = objectMapper.readValue(jsonValue, valueType);
            logger.debug("Retrieved value for key: {}", key);
            return Optional.of(value);
            
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize value for key: {}", key, e);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Failed to retrieve value for key: {}", key, e);
            return Optional.empty();
        }
    }
    
    // Delete data by reference ID
    public boolean deleteData(String referenceId) {
        try {
            String key = generateKey(referenceId);
            Boolean deleted = redisTemplate.delete(key);
            logger.info("Deleted data for reference ID: {}, success: {}", referenceId, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            logger.error("Failed to delete data with reference ID: {}", referenceId, e);
            return false;
        }
    }
    
    // Delete simple key
    public boolean deleteValue(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            logger.debug("Deleted value for key: {}, success: {}", key, deleted);
            return Boolean.TRUE.equals(deleted);
        } catch (Exception e) {
            logger.error("Failed to delete value for key: {}", key, e);
            return false;
        }
    }
    
    // Check if data exists
    public boolean exists(String referenceId) {
        try {
            String key = generateKey(referenceId);
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            logger.error("Failed to check existence for reference ID: {}", referenceId, e);
            return false;
        }
    }
    
    // Set TTL for existing key
    public boolean setTtl(String referenceId, long ttlSeconds) {
        try {
            String key = generateKey(referenceId);
            Boolean result = redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
            logger.info("Set TTL for reference ID: {} to {} seconds, success: {}", referenceId, ttlSeconds, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("Failed to set TTL for reference ID: {}", referenceId, e);
            return false;
        }
    }
    
    // Get TTL for key
    public long getTtl(String referenceId) {
        try {
            String key = generateKey(referenceId);
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl != null ? ttl : -1;
        } catch (Exception e) {
            logger.error("Failed to get TTL for reference ID: {}", referenceId, e);
            return -1;
        }
    }
    
    // Get all keys matching pattern
    public Set<String> getKeys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            return keys != null ? keys : new HashSet<>();
        } catch (Exception e) {
            logger.error("Failed to get keys for pattern: {}", pattern, e);
            return new HashSet<>();
        }
    }
    
    // Get cache statistics
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            Properties info = redisTemplate.getConnectionFactory().getConnection().info();
            stats.put("redis_version", info.getProperty("redis_version"));
            stats.put("used_memory", info.getProperty("used_memory_human"));
            stats.put("connected_clients", info.getProperty("connected_clients"));
            stats.put("total_commands_processed", info.getProperty("total_commands_processed"));
            stats.put("keyspace_hits", info.getProperty("keyspace_hits"));
            stats.put("keyspace_misses", info.getProperty("keyspace_misses"));
            
            // Get count of our application keys
            Set<String> appKeys = getKeys("cloud-integration:*");
            stats.put("application_keys_count", appKeys.size());
            
            logger.debug("Retrieved cache statistics");
        } catch (Exception e) {
            logger.error("Failed to get cache statistics", e);
            stats.put("error", "Failed to retrieve statistics");
        }
        return stats;
    }
    
    // Bulk operations
    public Map<String, CacheData> getAllDataByPattern(String pattern) {
        Map<String, CacheData> result = new HashMap<>();
        try {
            Set<String> keys = getKeys(pattern);
            for (String key : keys) {
                String referenceId = extractReferenceId(key);
                Optional<CacheData> data = getData(referenceId);
                if (data.isPresent()) {
                    result.put(referenceId, data.get());
                }
            }
            logger.debug("Retrieved {} data entries for pattern: {}", result.size(), pattern);
        } catch (Exception e) {
            logger.error("Failed to get all data for pattern: {}", pattern, e);
        }
        return result;
    }
    
    // Clear all application cache
    public boolean clearAllCache() {
        try {
            Set<String> keys = getKeys("cloud-integration:*");
            if (keys.isEmpty()) {
                logger.info("No cache keys to clear");
                return true;
            }
            
            Long deletedCount = redisTemplate.delete(keys);
            logger.info("Cleared {} cache entries", deletedCount);
            return deletedCount > 0;
        } catch (Exception e) {
            logger.error("Failed to clear all cache", e);
            return false;
        }
    }
    
    // Private helper methods
    private String generateKey(String referenceId) {
        return "cloud-integration:data:" + referenceId;
    }
    
    private String extractReferenceId(String key) {
        return key.replace("cloud-integration:data:", "");
    }
    
    // Store with metadata
    public boolean storeDataWithMetadata(String referenceId, CacheData cacheData, Map<String, Object> metadata) {
        try {
            // Store the main data
            boolean stored = storeData(referenceId, cacheData);
            if (!stored) {
                return false;
            }
            
            // Store metadata separately
            if (metadata != null && !metadata.isEmpty()) {
                String metadataKey = generateMetadataKey(referenceId);
                storeValue(metadataKey, metadata, cacheData.getTtlSeconds());
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Failed to store data with metadata for reference ID: {}", referenceId, e);
            return false;
        }
    }
    
    // Get metadata
    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> getMetadata(String referenceId) {
        try {
            String metadataKey = generateMetadataKey(referenceId);
            return getValue(metadataKey, (Class<Map<String, Object>>) (Class<?>) Map.class);
        } catch (Exception e) {
            logger.error("Failed to get metadata for reference ID: {}", referenceId, e);
            return Optional.empty();
        }
    }
    
    private String generateMetadataKey(String referenceId) {
        return "cloud-integration:metadata:" + referenceId;
    }
}
