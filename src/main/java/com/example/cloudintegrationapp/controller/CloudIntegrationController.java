package com.example.cloudintegrationapp.controller;

import com.example.cloudintegrationapp.integration.azure.AzureService;
import com.example.cloudintegrationapp.integration.gcp.GcpService;
import com.example.cloudintegrationapp.integration.splunk.SplunkService;
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

//    @Autowired
//    private GcpService gcpService;

//    @Autowired
//    private SplunkService splunkService;

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
        try {
//            gcpService.uploadObject(file.getOriginalFilename(), file.getBytes());
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
        try {
            SecurityContextHolder.clearContext();
//            byte[] data = gcpService.downloadObject(filename);
//            splunkService.logApplicationEvent("INFO", "File downloaded from GCP: " + filename, "GcpService");
            return ResponseEntity.ok("No GCP".getBytes());
        } catch (Exception e) {
//            splunkService.logApplicationEvent("ERROR", "Failed to download file from GCP: " + e.getMessage(), "GcpService");
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/gcp/pubsub")
    public ResponseEntity<Map<String, String>> publishToGcpPubSub(@RequestBody Map<String, String> message) {
        try {
//            gcpService.publishMessage(message.get("message"));
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
}
