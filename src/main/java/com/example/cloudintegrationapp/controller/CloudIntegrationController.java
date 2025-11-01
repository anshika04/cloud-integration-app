package com.example.cloudintegrationapp.controller;

import com.example.cloudintegrationapp.integration.azure.AzureService;
import com.example.cloudintegrationapp.integration.gcp.GcpService;
import com.example.cloudintegrationapp.service.DataService;
import com.example.cloudintegrationapp.service.ExcelProcessingService;
import com.example.cloudintegrationapp.service.RedisCacheService;
import com.example.cloudintegrationapp.service.ReferenceIdGenerator;
import com.example.cloudintegrationapp.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cloud")
public class CloudIntegrationController {

    @Autowired(required = false)
    private AzureService azureService;

    @Autowired(required = false)
    private GcpService gcpService;
    
    @Autowired
    private ExcelProcessingService excelProcessingService;

//    @Autowired
//    private SplunkService splunkService;
    
    @Autowired
    private DataService dataService;
    
    @Autowired
    private RedisCacheService redisCacheService;
    
    @Autowired
    private ReferenceIdGenerator referenceIdGenerator;

    @PostMapping("/azure/upload")
    public ResponseEntity<Map<String, String>> uploadToAzure(@RequestParam("file") MultipartFile file) {
        if (azureService == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure integration is disabled"));
        }
        
        try {
            azureService.uploadBlob(file.getOriginalFilename(), file.getBytes());
//            splunkService.logApplicationEvent("INFO", "File uploaded to Azure: " + file.getOriginalFilename(), "AzureService");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "File uploaded successfully to Azure");
            response.put("filename", file.getOriginalFilename());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to upload file to Azure: " + e.getMessage(), "AzureService");
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/azure/download/{filename}")
    public ResponseEntity<byte[]> downloadFromAzure(@PathVariable String filename) {
        if (azureService == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            byte[] data = azureService.downloadBlob(filename);
//            splunkService.logApplicationEvent("INFO", "File downloaded from Azure: " + filename, "AzureService");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to download file from Azure: " + e.getMessage(), "AzureService");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/azure/queue")
    public ResponseEntity<Map<String, String>> sendToAzureQueue(@RequestBody Map<String, String> message) {
        if (azureService == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Azure integration is disabled"));
        }
        
        try {
            azureService.sendQueueMessage(message.get("message"));
//            splunkService.logApplicationEvent("INFO", "Message sent to Azure queue", "AzureService");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message sent to Azure queue successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to send message to Azure queue: " + e.getMessage(), "AzureService");
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/gcp/upload")
    public ResponseEntity<Map<String, String>> uploadToGcp(@RequestParam("file") MultipartFile file) {
        if (gcpService == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "GCP integration is disabled"));
        }
        
        try {
            gcpService.uploadObject(file.getOriginalFilename(), file.getBytes());
//            splunkService.logApplicationEvent("INFO", "File uploaded to GCP: " + file.getOriginalFilename(), "GcpService");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "File uploaded successfully to GCP");
            response.put("filename", file.getOriginalFilename());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to upload file to GCP: " + e.getMessage(), "GcpService");
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/gcp/download/{filename}")
    public ResponseEntity<byte[]> downloadFromGcp(@PathVariable String filename) {
        if (gcpService == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            SecurityContextHolder.clearContext();
            byte[] data = gcpService.downloadObject(filename);
//            splunkService.logApplicationEvent("INFO", "File downloaded from GCP: " + filename, "GcpService");
            return ResponseEntity.ok(data);
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to download file from GCP: " + e.getMessage(), "GcpService");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/gcp/pubsub")
    public ResponseEntity<Map<String, String>> publishToGcpPubSub(@RequestBody Map<String, String> message) {
        if (gcpService == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "GCP integration is disabled"));
        }
        
        try {
            gcpService.publishMessage(message.get("message"));
//            splunkService.logApplicationEvent("INFO", "Message published to GCP Pub/Sub", "GcpService");
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message published to GCP Pub/Sub successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to publish message to GCP Pub/Sub: " + e.getMessage(), "GcpService");
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/splunk/log")
    public ResponseEntity<Map<String, String>> logToSplunk(@RequestBody Map<String, String> logData) {
        try {
//            splunkService.logEvent(
//                logData.get("event"),
//                logData.get("source"),
//                logData.get("sourcetype")
//            );
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Event logged to Splunk successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {

        SecurityContextHolder.clearContext();
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Cloud Integration Service");
        return ResponseEntity.ok(health);
    }

    @GetMapping(value = "/test", produces = "application/json")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Docker test endpoint is working!");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("environment", "docker");
        response.put("security_context", "anonymous");
        
        // Get security context info
        try {
            var context = SecurityContextHolder.getContext();
            var auth = context.getAuthentication();
            if (auth != null) {
                response.put("user", auth.getName());
                response.put("authorities", auth.getAuthorities().toString());
            } else {
                response.put("user", "no authentication");
            }
        } catch (Exception e) {
            response.put("security_error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ===== REDIS CACHE INTEGRATION ENDPOINTS =====
    
    @PostMapping("/generate-reference")
    public ResponseEntity<ApiResponse<String>> generateReferenceId(@RequestParam(required = false) String prefix) {
        try {
            String referenceId = prefix != null ? 
                referenceIdGenerator.generateReferenceId(prefix) : 
                referenceIdGenerator.generateReferenceId();
            
            return ResponseEntity.ok(ApiResponse.success("Reference ID generated", referenceId, referenceId));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error generating reference ID: " + e.getMessage()));
        }
    }
    
    @PostMapping("/store-data")
    public ResponseEntity<ApiResponse<String>> storeData(@RequestBody Map<String, Object> request) {
        try {
            String prefix = (String) request.getOrDefault("prefix", "CLD");
            Object data = request.get("data");
            String dataType = (String) request.getOrDefault("dataType", "CLOUD_DATA");
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
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error storing data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/retrieve-data/{referenceId}")
    public ResponseEntity<ApiResponse<Object>> retrieveData(@PathVariable String referenceId) {
        try {
            ApiResponse<Object> result = dataService.getCustomData(referenceId);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving data: " + e.getMessage()));
        }
    }
    
    @GetMapping("/cache-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats() {
        try {
            ApiResponse<Map<String, Object>> result = dataService.getCacheStatistics();
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving cache statistics: " + e.getMessage()));
        }
    }
    
    @PostMapping("/azure/upload-with-cache")
    public ResponseEntity<ApiResponse<String>> uploadToAzureWithCache(@RequestParam("file") MultipartFile file) {
        if (azureService == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Azure integration is disabled"));
        }
        
        try {
            // Generate reference ID for this upload
            String referenceId = referenceIdGenerator.generateAzureReferenceId();
            
            // Store file metadata in cache
            Map<String, Object> fileMetadata = new HashMap<>();
            fileMetadata.put("originalName", file.getOriginalFilename());
            fileMetadata.put("size", file.getSize());
            fileMetadata.put("contentType", file.getContentType());
            fileMetadata.put("uploadTime", java.time.LocalDateTime.now().toString());
            
            // Store in cache with 1 hour TTL
            redisCacheService.storeData(referenceId, 
                new com.example.cloudintegrationapp.model.CacheData(referenceId, "AZURE_UPLOAD", fileMetadata, 3600L));
            
            // Upload to Azure (original functionality)
            azureService.uploadBlob(file.getOriginalFilename(), file.getBytes());
            
            // Update cache with success status
            fileMetadata.put("status", "UPLOADED");
            fileMetadata.put("azureBlobName", file.getOriginalFilename());
            redisCacheService.storeData(referenceId, 
                new com.example.cloudintegrationapp.model.CacheData(referenceId, "AZURE_UPLOAD", fileMetadata, 3600L));
            
            return ResponseEntity.ok(ApiResponse.success("File uploaded to Azure with cache tracking", referenceId, referenceId));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }
    
    @GetMapping("/azure/upload-status/{referenceId}")
    public ResponseEntity<ApiResponse<Object>> getAzureUploadStatus(@PathVariable String referenceId) {
        try {
            ApiResponse<Object> result = dataService.getCustomData(referenceId);
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving upload status: " + e.getMessage()));
        }
    }
    
    // ===== GCP CONFIGURATION ENDPOINTS =====
    
    @GetMapping("/gcp/config/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGcpConfigStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("enabled", gcpService != null);
            status.put("message", gcpService != null ? 
                "GCP integration is enabled and configured" : 
                "GCP integration is disabled. Please configure connection details.");
            
            Map<String, Object> config = new HashMap<>();
            config.put("projectId", System.getenv("GCP_PROJECT_ID") != null ? 
                "***configured***" : "not set");
            config.put("bucketName", System.getenv("GCP_STORAGE_BUCKET") != null ? 
                "***configured***" : "not set");
            config.put("credentials", System.getenv("GCP_SERVICE_ACCOUNT_KEY") != null ? 
                "***configured***" : "not set");
            status.put("configuration", config);
            
            return ResponseEntity.ok(ApiResponse.success("GCP configuration status retrieved", status));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error retrieving GCP configuration status: " + e.getMessage()));
        }
    }
    
    @PostMapping("/gcp/config/connect")
    public ResponseEntity<ApiResponse<Map<String, Object>>> configureGcpConnection(@RequestBody Map<String, String> config) {
        try {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> missingFields = new HashMap<>();
            
            // Validate required fields
            String projectId = config.get("projectId");
            String bucketName = config.get("bucketName");
            String topicName = config.get("topicName");
            String subscriptionName = config.get("subscriptionName");
            String serviceAccountKey = config.get("serviceAccountKey");
            
            if (projectId == null || projectId.isEmpty()) {
                missingFields.put("projectId", "GCP Project ID is required");
            }
            if (bucketName == null || bucketName.isEmpty()) {
                missingFields.put("bucketName", "GCP Storage Bucket name is required");
            }
            if (topicName == null || topicName.isEmpty()) {
                missingFields.put("topicName", "GCP Pub/Sub Topic name is required");
            }
            if (subscriptionName == null || subscriptionName.isEmpty()) {
                missingFields.put("subscriptionName", "GCP Pub/Sub Subscription name is required");
            }
            if (serviceAccountKey == null || serviceAccountKey.isEmpty()) {
                missingFields.put("serviceAccountKey", "GCP Service Account Key (JSON or Base64) is required");
            }
            
            if (!missingFields.isEmpty()) {
                response.put("missingFields", missingFields);
                response.put("message", "Please provide all required GCP connection details");
                response.put("instructions", "To enable GCP integration, set the following environment variables or update application.yml:\n" +
                    "  - GCP_PROJECT_ID: " + (projectId != null ? projectId : "your-project-id") + "\n" +
                    "  - GCP_STORAGE_BUCKET: " + (bucketName != null ? bucketName : "your-bucket-name") + "\n" +
                    "  - GCP_PUBSUB_TOPIC: " + (topicName != null ? topicName : "your-topic-name") + "\n" +
                    "  - GCP_PUBSUB_SUBSCRIPTION: " + (subscriptionName != null ? subscriptionName : "your-subscription-name") + "\n" +
                    "  - GCP_SERVICE_ACCOUNT_KEY: Base64 encoded service account JSON key\n" +
                    "  - GCP_ENABLED: true (or add 'gcp.enabled: true' to application.yml)\n" +
                    "\nAfter setting these values, restart the application for changes to take effect.");
                response.put("error", "Missing required configuration fields");
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Missing required configuration fields"));
            }
            
            // Validate service account key format (basic check)
            boolean isValidKey = false;
            if (serviceAccountKey != null && !serviceAccountKey.isEmpty()) {
                try {
                    // Try to parse as JSON
                    if (serviceAccountKey.trim().startsWith("{")) {
                        isValidKey = true;
                    } else {
                        // Try Base64 decode
                        byte[] decoded = java.util.Base64.getDecoder().decode(serviceAccountKey);
                        String decodedStr = new String(decoded);
                        if (decodedStr.trim().startsWith("{")) {
                            isValidKey = true;
                        }
                    }
                } catch (Exception e) {
                    // Invalid format
                }
            }
            
            if (!isValidKey) {
                response.put("message", "Service Account Key must be a valid JSON string or Base64 encoded JSON");
                response.put("hint", "The key should start with '{' if JSON, or be a valid Base64 string");
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid service account key format"));
            }
            
            // Store configuration information
            Map<String, Object> configInfo = new HashMap<>();
            configInfo.put("projectId", projectId);
            configInfo.put("bucketName", bucketName);
            configInfo.put("topicName", topicName);
            configInfo.put("subscriptionName", subscriptionName);
            configInfo.put("credentialsConfigured", true);
            configInfo.put("note", "Credentials are stored (truncated for security)");
            
            // Store in cache with reference ID
            String referenceId = referenceIdGenerator.generateReferenceId("GCP");
            Map<String, Object> configData = new HashMap<>();
            configData.put("type", "GCP_CONFIG");
            configData.put("projectId", projectId);
            configData.put("bucketName", bucketName);
            configData.put("topicName", topicName);
            configData.put("subscriptionName", subscriptionName);
            configData.put("configuredAt", java.time.LocalDateTime.now().toString());
            String keyPreview = serviceAccountKey != null && serviceAccountKey.length() > 50 ? 
                serviceAccountKey.substring(0, 50) + "..." : (serviceAccountKey != null ? serviceAccountKey : "");
            configData.put("keyPreview", keyPreview);
            
            redisCacheService.storeData(referenceId, 
                new com.example.cloudintegrationapp.model.CacheData(referenceId, "GCP_CONFIG", configData, 86400L));
            
            response.put("message", "GCP connection details received and validated. Please update your configuration to enable GCP integration.");
            response.put("referenceId", referenceId);
            response.put("configuration", configInfo);
            response.put("nextSteps", Map.of(
                "1", "Set environment variables or update application.yml with the provided values",
                "2", "Set GCP_ENABLED=true or add 'gcp.enabled: true' to application.yml",
                "3", "Restart the application",
                "4", "Verify connection using GET /cloud/gcp/config/status"
            ));
            response.put("environmentVariables", Map.of(
                "GCP_PROJECT_ID", projectId,
                "GCP_STORAGE_BUCKET", bucketName,
                "GCP_PUBSUB_TOPIC", topicName,
                "GCP_PUBSUB_SUBSCRIPTION", subscriptionName,
                "GCP_SERVICE_ACCOUNT_KEY", "***[configured - use provided value]***",
                "GCP_ENABLED", "true"
            ));
            
            return ResponseEntity.ok(ApiResponse.success(
                "GCP connection details validated successfully. Please update configuration and restart application.", 
                response));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error configuring GCP connection: " + e.getMessage()));
        }
    }
    
    @GetMapping("/gcp/files")
    public ResponseEntity<ApiResponse<java.util.List<Map<String, Object>>>> listGcpFiles() {
        if (gcpService == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("GCP integration is disabled"));
        }
        
        try {
            java.util.List<Map<String, Object>> files = gcpService.listObjects();
            return ResponseEntity.ok(ApiResponse.success("Files retrieved successfully", files));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error listing files: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/gcp/files/{filename}")
    public ResponseEntity<ApiResponse<String>> deleteGcpFile(@PathVariable String filename) {
        if (gcpService == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("GCP integration is disabled"));
        }
        
        try {
            gcpService.deleteObject(filename);
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", filename));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error deleting file: " + e.getMessage()));
        }
    }
    
    @PostMapping("/gcp/excel/parse/{filename}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parseExcelFromGcp(@PathVariable String filename) {
        if (gcpService == null) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("GCP integration is disabled"));
        }
        
        try {
            Map<String, Object> result = excelProcessingService.parseExcelFromGcp(filename);
            return ResponseEntity.ok(ApiResponse.success("Excel file parsed and cached successfully", result));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("Error parsing Excel file: " + e.getMessage()));
        }
    }
}
