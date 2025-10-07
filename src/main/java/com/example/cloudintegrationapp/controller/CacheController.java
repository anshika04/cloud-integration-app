package com.example.cloudintegrationapp.controller;

import com.example.cloudintegrationapp.model.ApiResponse;
import com.example.cloudintegrationapp.model.CacheData;
import com.example.cloudintegrationapp.model.DataEntity;
import com.example.cloudintegrationapp.service.DataService;
import com.example.cloudintegrationapp.service.RedisCacheService;
import com.example.cloudintegrationapp.service.ReferenceIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/cache")
public class CacheController {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);
    
    @Autowired
    private RedisCacheService redisCacheService;
    
    @Autowired
    private DataService dataService;
    
    @Autowired
    private ReferenceIdGenerator referenceIdGenerator;
    
    // ===== REFERENCE ID GENERATION ENDPOINTS =====
    
    @GetMapping("/generate-reference-id")
    public ResponseEntity<ApiResponse<String>> generateReferenceId(@RequestParam(required = false) String prefix) {
        try {
            String referenceId = prefix != null ? 
                referenceIdGenerator.generateReferenceId(prefix) : 
                referenceIdGenerator.generateReferenceId();
            
            logger.info("Generated reference ID: {} with prefix: {}", referenceId, prefix);
            return ResponseEntity.ok(ApiResponse.success("Reference ID generated successfully", referenceId, referenceId));
            
        } catch (Exception e) {
            logger.error("Error generating reference ID", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error generating reference ID: " + e.getMessage()));
        }
    }
    
    @GetMapping("/generate-reference-id/{type}")
    public ResponseEntity<ApiResponse<String>> generateReferenceIdByType(@PathVariable String type) {
        try {
            String referenceId;
            
            switch (type.toUpperCase()) {
                case "AZURE":
                    referenceId = referenceIdGenerator.generateAzureReferenceId();
                    break;
                case "GCP":
                    referenceId = referenceIdGenerator.generateGcpReferenceId();
                    break;
                case "SPLUNK":
                    referenceId = referenceIdGenerator.generateSplunkReferenceId();
                    break;
                case "USER":
                    referenceId = referenceIdGenerator.generateUserReferenceId();
                    break;
                case "DOCUMENT":
                    referenceId = referenceIdGenerator.generateDocumentReferenceId();
                    break;
                case "TRANSACTION":
                    referenceId = referenceIdGenerator.generateTransactionReferenceId();
                    break;
                case "LOG":
                    referenceId = referenceIdGenerator.generateLogReferenceId();
                    break;
                case "CACHE":
                    referenceId = referenceIdGenerator.generateCacheReferenceId();
                    break;
                case "SYSTEM":
                    referenceId = referenceIdGenerator.generateSystemReferenceId();
                    break;
                default:
                    referenceId = referenceIdGenerator.generateReferenceId(type);
            }
            
            logger.info("Generated {} reference ID: {}", type, referenceId);
            return ResponseEntity.ok(ApiResponse.success("Reference ID generated successfully", referenceId, referenceId));
            
        } catch (Exception e) {
            logger.error("Error generating reference ID for type: {}", type, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error generating reference ID: " + e.getMessage()));
        }
    }
    
    @PostMapping("/generate-custom-reference-id")
    public ResponseEntity<ApiResponse<String>> generateCustomReferenceId(@RequestBody Map<String, Object> config) {
        try {
            String prefix = (String) config.getOrDefault("prefix", "CLD");
            Boolean includeTimestamp = (Boolean) config.getOrDefault("includeTimestamp", true);
            Integer randomLength = (Integer) config.getOrDefault("randomLength", 6);
            Boolean includeSequence = (Boolean) config.getOrDefault("includeSequence", true);
            
            String referenceId = referenceIdGenerator.generateCustomReferenceId(
                prefix, includeTimestamp, randomLength, includeSequence);
            
            logger.info("Generated custom reference ID: {}", referenceId);
            return ResponseEntity.ok(ApiResponse.success("Custom reference ID generated successfully", referenceId, referenceId));
            
        } catch (Exception e) {
            logger.error("Error generating custom reference ID", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error generating custom reference ID: " + e.getMessage()));
        }
    }
    
    // ===== DATA ENTITY MANAGEMENT ENDPOINTS =====
    
    @PostMapping("/data-entity")
    public ResponseEntity<ApiResponse<DataEntity>> createDataEntity(@RequestBody Map<String, String> entityData) {
        try {
            String name = entityData.get("name");
            String description = entityData.get("description");
            String category = entityData.get("category");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Name is required"));
            }
            
            ApiResponse<DataEntity> result = dataService.createDataEntity(name, description, category);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error creating data entity", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error creating data entity: " + e.getMessage()));
        }
    }
    
    @GetMapping("/data-entity/{referenceId}")
    public ResponseEntity<ApiResponse<DataEntity>> getDataEntity(@PathVariable String referenceId) {
        try {
            ApiResponse<DataEntity> result = dataService.getDataEntity(referenceId);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving data entity for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving data entity: " + e.getMessage()));
        }
    }
    
    @PutMapping("/data-entity/{referenceId}")
    public ResponseEntity<ApiResponse<DataEntity>> updateDataEntity(
            @PathVariable String referenceId, 
            @RequestBody Map<String, String> updateData) {
        try {
            String name = updateData.get("name");
            String description = updateData.get("description");
            String category = updateData.get("category");
            String status = updateData.get("status");
            
            ApiResponse<DataEntity> result = dataService.updateDataEntity(referenceId, name, description, category, status);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error updating data entity for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error updating data entity: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/data-entity/{referenceId}")
    public ResponseEntity<ApiResponse<String>> deleteDataEntity(@PathVariable String referenceId) {
        try {
            ApiResponse<String> result = dataService.deleteDataEntity(referenceId);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error deleting data entity for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error deleting data entity: " + e.getMessage()));
        }
    }
    
    @GetMapping("/data-entities")
    public ResponseEntity<ApiResponse<List<DataEntity>>> getAllDataEntities(@RequestParam(required = false) String pattern) {
        try {
            ApiResponse<List<DataEntity>> result = dataService.getAllDataEntities(pattern);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error retrieving all data entities", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving data entities: " + e.getMessage()));
        }
    }
    
    // ===== CUSTOM DATA STORAGE ENDPOINTS =====
    
    @PostMapping("/store")
    public ResponseEntity<ApiResponse<String>> storeCustomData(@RequestBody Map<String, Object> request) {
        try {
            String prefix = (String) request.getOrDefault("prefix", "CLD");
            Object data = request.get("data");
            String dataType = (String) request.getOrDefault("dataType", "CUSTOM");
            Long ttlSeconds = request.get("ttlSeconds") != null ? 
                Long.valueOf(request.get("ttlSeconds").toString()) : null;
            
            if (data == null) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Data is required"));
            }
            
            ApiResponse<String> result = dataService.storeCustomData(prefix, data, dataType, ttlSeconds);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error storing custom data", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error storing custom data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/retrieve/{referenceId}")
    public ResponseEntity<ApiResponse<Object>> retrieveCustomData(@PathVariable String referenceId) {
        try {
            ApiResponse<Object> result = dataService.getCustomData(referenceId);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving custom data for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving custom data: " + e.getMessage()));
        }
    }
    
    // ===== CACHE MANAGEMENT ENDPOINTS =====
    
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats() {
        try {
            ApiResponse<Map<String, Object>> result = dataService.getCacheStatistics();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error retrieving cache statistics", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving cache statistics: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearAllCache() {
        try {
            ApiResponse<String> result = dataService.clearAllCache();
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error clearing cache", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error clearing cache: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/delete/{referenceId}")
    public ResponseEntity<ApiResponse<String>> deleteCacheEntry(@PathVariable String referenceId) {
        try {
            boolean deleted = redisCacheService.deleteData(referenceId);
            
            if (deleted) {
                logger.info("Deleted cache entry for reference ID: {}", referenceId);
                return ResponseEntity.ok(ApiResponse.success("Cache entry deleted successfully", referenceId, referenceId));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cache entry not found for reference ID: " + referenceId));
            }
            
        } catch (Exception e) {
            logger.error("Error deleting cache entry for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error deleting cache entry: " + e.getMessage()));
        }
    }
    
    @GetMapping("/exists/{referenceId}")
    public ResponseEntity<ApiResponse<Boolean>> checkCacheExists(@PathVariable String referenceId) {
        try {
            boolean exists = redisCacheService.exists(referenceId);
            return ResponseEntity.ok(ApiResponse.success("Cache existence checked", exists, referenceId));
            
        } catch (Exception e) {
            logger.error("Error checking cache existence for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error checking cache existence: " + e.getMessage()));
        }
    }
    
    @GetMapping("/ttl/{referenceId}")
    public ResponseEntity<ApiResponse<Long>> getCacheTtl(@PathVariable String referenceId) {
        try {
            long ttl = redisCacheService.getTtl(referenceId);
            return ResponseEntity.ok(ApiResponse.success("Cache TTL retrieved", ttl, referenceId));
            
        } catch (Exception e) {
            logger.error("Error getting cache TTL for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error getting cache TTL: " + e.getMessage()));
        }
    }
    
    @PutMapping("/ttl/{referenceId}")
    public ResponseEntity<ApiResponse<String>> setCacheTtl(@PathVariable String referenceId, @RequestParam long ttlSeconds) {
        try {
            boolean set = redisCacheService.setTtl(referenceId, ttlSeconds);
            
            if (set) {
                return ResponseEntity.ok(ApiResponse.success("Cache TTL set successfully", referenceId, referenceId));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to set cache TTL for reference ID: " + referenceId));
            }
            
        } catch (Exception e) {
            logger.error("Error setting cache TTL for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error setting cache TTL: " + e.getMessage()));
        }
    }
    
    // ===== BULK OPERATIONS =====
    
    @PostMapping("/bulk-create")
    public ResponseEntity<ApiResponse<List<String>>> bulkCreateDataEntities(@RequestBody List<Map<String, String>> entityDataList) {
        try {
            ApiResponse<List<String>> result = dataService.bulkCreateDataEntities(entityDataList);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
            
        } catch (Exception e) {
            logger.error("Error in bulk creation", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error in bulk creation: " + e.getMessage()));
        }
    }
    
    @PostMapping("/async-process/{referenceId}")
    public ResponseEntity<ApiResponse<String>> processDataAsync(@PathVariable String referenceId, @RequestParam String operation) {
        try {
            CompletableFuture<ApiResponse<String>> future = dataService.processDataAsync(referenceId, operation);
            
            // Return immediately with processing status
            return ResponseEntity.ok(ApiResponse.success("Async processing started for reference ID: " + referenceId, referenceId, referenceId));
            
        } catch (Exception e) {
            logger.error("Error starting async processing for reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error starting async processing: " + e.getMessage()));
        }
    }
    
    // ===== UTILITY ENDPOINTS =====
    
    @GetMapping("/validate-reference-id/{referenceId}")
    public ResponseEntity<ApiResponse<Boolean>> validateReferenceId(@PathVariable String referenceId) {
        try {
            boolean valid = referenceIdGenerator.isValidReferenceId(referenceId);
            return ResponseEntity.ok(ApiResponse.success("Reference ID validation completed", valid, referenceId));
            
        } catch (Exception e) {
            logger.error("Error validating reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error validating reference ID: " + e.getMessage()));
        }
    }
    
    @GetMapping("/extract-prefix/{referenceId}")
    public ResponseEntity<ApiResponse<String>> extractPrefix(@PathVariable String referenceId) {
        try {
            String prefix = referenceIdGenerator.extractPrefix(referenceId);
            return ResponseEntity.ok(ApiResponse.success("Prefix extracted", prefix, referenceId));
            
        } catch (Exception e) {
            logger.error("Error extracting prefix from reference ID: {}", referenceId, e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error extracting prefix: " + e.getMessage()));
        }
    }
    
    @GetMapping("/generator-stats")
    public ResponseEntity<ApiResponse<ReferenceIdGenerator.ReferenceIdStats>> getGeneratorStats() {
        try {
            ReferenceIdGenerator.ReferenceIdStats stats = referenceIdGenerator.getStats();
            return ResponseEntity.ok(ApiResponse.success("Generator statistics retrieved", stats));
            
        } catch (Exception e) {
            logger.error("Error retrieving generator statistics", e);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving generator statistics: " + e.getMessage()));
        }
    }
}
