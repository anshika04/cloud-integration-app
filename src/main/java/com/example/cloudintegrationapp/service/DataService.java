package com.example.cloudintegrationapp.service;

import com.example.cloudintegrationapp.model.ApiResponse;
import com.example.cloudintegrationapp.model.CacheData;
import com.example.cloudintegrationapp.model.DataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DataService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataService.class);
    
    @Autowired
    private RedisCacheService redisCacheService;
    
    @Autowired
    private ReferenceIdGenerator referenceIdGenerator;
    
    /**
     * Create and store data entity with generated reference ID
     */
    public ApiResponse<DataEntity> createDataEntity(String name, String description, String category) {
        try {
            String referenceId = referenceIdGenerator.generateReferenceId();
            
            DataEntity entity = new DataEntity(referenceId, name, description);
            entity.setCategory(category);
            entity.setStatus("ACTIVE");
            
            // Create cache data
            CacheData cacheData = new CacheData(referenceId, "DATA_ENTITY", entity);
            cacheData.setMetadata("Created via DataService");
            
            // Store in Redis cache
            boolean stored = redisCacheService.storeData(referenceId, cacheData);
            
            if (stored) {
                logger.info("Created data entity with reference ID: {}", referenceId);
                return ApiResponse.success("Data entity created successfully", entity, referenceId);
            } else {
                logger.error("Failed to store data entity in cache for reference ID: {}", referenceId);
                return ApiResponse.error("Failed to store data entity");
            }
            
        } catch (Exception e) {
            logger.error("Error creating data entity", e);
            return ApiResponse.error("Error creating data entity: " + e.getMessage());
        }
    }
    
    /**
     * Retrieve data entity by reference ID
     */
    public ApiResponse<DataEntity> getDataEntity(String referenceId) {
        try {
            Optional<CacheData> cacheDataOpt = redisCacheService.getData(referenceId);
            
            if (cacheDataOpt.isPresent()) {
                CacheData cacheData = cacheDataOpt.get();
                if (cacheData.getContent() instanceof DataEntity) {
                    DataEntity entity = (DataEntity) cacheData.getContent();
                    logger.info("Retrieved data entity with reference ID: {}", referenceId);
                    return ApiResponse.success("Data entity retrieved successfully", entity, referenceId);
                } else {
                    logger.warn("Content is not a DataEntity for reference ID: {}", referenceId);
                    return ApiResponse.error("Invalid data type for reference ID: " + referenceId);
                }
            } else {
                logger.warn("No data found for reference ID: {}", referenceId);
                return ApiResponse.error("Data entity not found for reference ID: " + referenceId);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving data entity for reference ID: {}", referenceId, e);
            return ApiResponse.error("Error retrieving data entity: " + e.getMessage());
        }
    }
    
    /**
     * Update data entity
     */
    public ApiResponse<DataEntity> updateDataEntity(String referenceId, String name, String description, String category, String status) {
        try {
            Optional<CacheData> cacheDataOpt = redisCacheService.getData(referenceId);
            
            if (cacheDataOpt.isPresent()) {
                CacheData cacheData = cacheDataOpt.get();
                if (cacheData.getContent() instanceof DataEntity) {
                    DataEntity entity = (DataEntity) cacheData.getContent();
                    
                    // Update fields
                    if (name != null && !name.trim().isEmpty()) {
                        entity.setName(name);
                    }
                    if (description != null && !description.trim().isEmpty()) {
                        entity.setDescription(description);
                    }
                    if (category != null && !category.trim().isEmpty()) {
                        entity.setCategory(category);
                    }
                    if (status != null && !status.trim().isEmpty()) {
                        entity.setStatus(status);
                    }
                    
                    entity.setUpdatedAt(LocalDateTime.now());
                    
                    // Update cache
                    cacheData.setContent(entity);
                    cacheData.setMetadata("Updated via DataService");
                    
                    boolean updated = redisCacheService.storeData(referenceId, cacheData);
                    
                    if (updated) {
                        logger.info("Updated data entity with reference ID: {}", referenceId);
                        return ApiResponse.success("Data entity updated successfully", entity, referenceId);
                    } else {
                        logger.error("Failed to update data entity in cache for reference ID: {}", referenceId);
                        return ApiResponse.error("Failed to update data entity");
                    }
                } else {
                    return ApiResponse.error("Invalid data type for reference ID: " + referenceId);
                }
            } else {
                return ApiResponse.error("Data entity not found for reference ID: " + referenceId);
            }
            
        } catch (Exception e) {
            logger.error("Error updating data entity for reference ID: {}", referenceId, e);
            return ApiResponse.error("Error updating data entity: " + e.getMessage());
        }
    }
    
    /**
     * Delete data entity
     */
    public ApiResponse<String> deleteDataEntity(String referenceId) {
        try {
            boolean deleted = redisCacheService.deleteData(referenceId);
            
            if (deleted) {
                logger.info("Deleted data entity with reference ID: {}", referenceId);
                return ApiResponse.success("Data entity deleted successfully", referenceId, referenceId);
            } else {
                logger.warn("Failed to delete data entity for reference ID: {}", referenceId);
                return ApiResponse.error("Failed to delete data entity or entity not found");
            }
            
        } catch (Exception e) {
            logger.error("Error deleting data entity for reference ID: {}", referenceId, e);
            return ApiResponse.error("Error deleting data entity: " + e.getMessage());
        }
    }
    
    /**
     * Store any data with custom reference ID
     */
    public ApiResponse<String> storeCustomData(String prefix, Object data, String dataType, Long ttlSeconds) {
        try {
            String referenceId = referenceIdGenerator.generateReferenceId(prefix);
            
            CacheData cacheData = new CacheData(referenceId, dataType, data, ttlSeconds);
            cacheData.setMetadata("Stored via DataService.storeCustomData");
            
            boolean stored = redisCacheService.storeData(referenceId, cacheData);
            
            if (stored) {
                logger.info("Stored custom data with reference ID: {}", referenceId);
                return ApiResponse.success("Custom data stored successfully", referenceId, referenceId);
            } else {
                logger.error("Failed to store custom data for reference ID: {}", referenceId);
                return ApiResponse.error("Failed to store custom data");
            }
            
        } catch (Exception e) {
            logger.error("Error storing custom data", e);
            return ApiResponse.error("Error storing custom data: " + e.getMessage());
        }
    }
    
    /**
     * Retrieve any data by reference ID
     */
    public ApiResponse<Object> getCustomData(String referenceId) {
        try {
            Optional<CacheData> cacheDataOpt = redisCacheService.getData(referenceId);
            
            if (cacheDataOpt.isPresent()) {
                CacheData cacheData = cacheDataOpt.get();
                logger.info("Retrieved custom data with reference ID: {}", referenceId);
                return ApiResponse.success("Custom data retrieved successfully", cacheData.getContent(), referenceId);
            } else {
                logger.warn("No custom data found for reference ID: {}", referenceId);
                return ApiResponse.error("Custom data not found for reference ID: " + referenceId);
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving custom data for reference ID: {}", referenceId, e);
            return ApiResponse.error("Error retrieving custom data: " + e.getMessage());
        }
    }
    
    /**
     * Get all data entities by pattern
     */
    public ApiResponse<List<DataEntity>> getAllDataEntities(String pattern) {
        try {
            String searchPattern = pattern != null ? pattern : "cloud-integration:data:*";
            Map<String, CacheData> allData = redisCacheService.getAllDataByPattern(searchPattern);
            
            List<DataEntity> entities = allData.values().stream()
                .filter(cacheData -> cacheData.getContent() instanceof DataEntity)
                .map(cacheData -> (DataEntity) cacheData.getContent())
                .collect(Collectors.toList());
            
            logger.info("Retrieved {} data entities for pattern: {}", entities.size(), pattern);
            return ApiResponse.success("Data entities retrieved successfully", entities);
            
        } catch (Exception e) {
            logger.error("Error retrieving all data entities", e);
            return ApiResponse.error("Error retrieving data entities: " + e.getMessage());
        }
    }
    
    /**
     * Get cache statistics
     */
    public ApiResponse<Map<String, Object>> getCacheStatistics() {
        try {
            Map<String, Object> stats = redisCacheService.getCacheStats();
            logger.info("Retrieved cache statistics");
            return ApiResponse.success("Cache statistics retrieved successfully", stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving cache statistics", e);
            return ApiResponse.error("Error retrieving cache statistics: " + e.getMessage());
        }
    }
    
    /**
     * Clear all cache data
     */
    public ApiResponse<String> clearAllCache() {
        try {
            boolean cleared = redisCacheService.clearAllCache();
            
            if (cleared) {
                logger.info("Cleared all cache data");
                return ApiResponse.success("All cache data cleared successfully");
            } else {
                logger.warn("Failed to clear cache data");
                return ApiResponse.error("Failed to clear cache data");
            }
            
        } catch (Exception e) {
            logger.error("Error clearing cache data", e);
            return ApiResponse.error("Error clearing cache data: " + e.getMessage());
        }
    }
    
    /**
     * Bulk create data entities
     */
    public ApiResponse<List<String>> bulkCreateDataEntities(List<Map<String, String>> entityDataList) {
        try {
            List<String> referenceIds = new ArrayList<>();
            
            for (Map<String, String> entityData : entityDataList) {
                String name = entityData.get("name");
                String description = entityData.get("description");
                String category = entityData.get("category");
                
                if (name != null && !name.trim().isEmpty()) {
                    ApiResponse<DataEntity> result = createDataEntity(name, description, category);
                    if (result.isSuccess()) {
                        referenceIds.add(result.getReferenceId());
                    }
                }
            }
            
            logger.info("Bulk created {} data entities", referenceIds.size());
            return ApiResponse.success("Bulk creation completed", referenceIds);
            
        } catch (Exception e) {
            logger.error("Error in bulk creation of data entities", e);
            return ApiResponse.error("Error in bulk creation: " + e.getMessage());
        }
    }
    
    /**
     * Async data processing
     */
    public CompletableFuture<ApiResponse<String>> processDataAsync(String referenceId, String operation) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate async processing
                Thread.sleep(1000);
                
                Optional<CacheData> cacheDataOpt = redisCacheService.getData(referenceId);
                if (cacheDataOpt.isPresent()) {
                    // Process the data based on operation
                    CacheData cacheData = cacheDataOpt.get();
                    cacheData.setMetadata("Processed with operation: " + operation);
                    redisCacheService.storeData(referenceId, cacheData);
                    
                    logger.info("Async processing completed for reference ID: {} with operation: {}", referenceId, operation);
                    return ApiResponse.success("Async processing completed", referenceId, referenceId);
                } else {
                    return ApiResponse.error("Data not found for reference ID: " + referenceId);
                }
                
            } catch (Exception e) {
                logger.error("Error in async processing for reference ID: {}", referenceId, e);
                return ApiResponse.error("Error in async processing: " + e.getMessage());
            }
        });
    }
}
