package com.example.cloudintegrationapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReferenceIdGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(ReferenceIdGenerator.class);
    
    private final SecureRandom secureRandom = new SecureRandom();
    private final AtomicLong sequenceCounter = new AtomicLong(1);
    
    // Different prefix patterns for different data types
    private static final String[] PREFIXES = {
        "CLD", "AZR", "GCP", "SPL", "USR", "DOC", "TXN", "LOG", "CACHE", "SYS"
    };
    
    /**
     * Generate a unique reference ID with timestamp and random component
     * Format: PREFIX-YYYYMMDDHHMMSS-RANDOM-SEQUENCE
     */
    public String generateReferenceId(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "CLD"; // Default prefix
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = generateRandomString(6);
        String sequence = String.format("%04d", sequenceCounter.getAndIncrement() % 10000);
        
        String referenceId = String.format("%s-%s-%s-%s", prefix.toUpperCase(), timestamp, random, sequence);
        
        logger.debug("Generated reference ID: {}", referenceId);
        return referenceId;
    }
    
    /**
     * Generate a reference ID with default CLD prefix
     */
    public String generateReferenceId() {
        return generateReferenceId("CLD");
    }
    
    /**
     * Generate reference ID for Azure operations
     */
    public String generateAzureReferenceId() {
        return generateReferenceId("AZR");
    }
    
    /**
     * Generate reference ID for GCP operations
     */
    public String generateGcpReferenceId() {
        return generateReferenceId("GCP");
    }
    
    /**
     * Generate reference ID for Splunk operations
     */
    public String generateSplunkReferenceId() {
        return generateReferenceId("SPL");
    }
    
    /**
     * Generate reference ID for user operations
     */
    public String generateUserReferenceId() {
        return generateReferenceId("USR");
    }
    
    /**
     * Generate reference ID for document operations
     */
    public String generateDocumentReferenceId() {
        return generateReferenceId("DOC");
    }
    
    /**
     * Generate reference ID for transaction operations
     */
    public String generateTransactionReferenceId() {
        return generateReferenceId("TXN");
    }
    
    /**
     * Generate reference ID for logging operations
     */
    public String generateLogReferenceId() {
        return generateReferenceId("LOG");
    }
    
    /**
     * Generate reference ID for cache operations
     */
    public String generateCacheReferenceId() {
        return generateReferenceId("CACHE");
    }
    
    /**
     * Generate reference ID for system operations
     */
    public String generateSystemReferenceId() {
        return generateReferenceId("SYS");
    }
    
    /**
     * Generate a short reference ID (without timestamp)
     * Format: PREFIX-RANDOM-SEQUENCE
     */
    public String generateShortReferenceId(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "CLD";
        }
        
        String random = generateRandomString(8);
        String sequence = String.format("%03d", sequenceCounter.getAndIncrement() % 1000);
        
        String referenceId = String.format("%s-%s-%s", prefix.toUpperCase(), random, sequence);
        
        logger.debug("Generated short reference ID: {}", referenceId);
        return referenceId;
    }
    
    /**
     * Generate a UUID-based reference ID
     */
    public String generateUuidReferenceId(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "CLD";
        }
        
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String referenceId = String.format("%s-%s", prefix.toUpperCase(), uuid);
        
        logger.debug("Generated UUID reference ID: {}", referenceId);
        return referenceId;
    }
    
    /**
     * Generate a reference ID with custom format
     * @param prefix The prefix for the ID
     * @param includeTimestamp Whether to include timestamp
     * @param randomLength Length of random string
     * @param includeSequence Whether to include sequence number
     */
    public String generateCustomReferenceId(String prefix, boolean includeTimestamp, int randomLength, boolean includeSequence) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "CLD";
        }
        
        StringBuilder referenceId = new StringBuilder(prefix.toUpperCase());
        
        if (includeTimestamp) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            referenceId.append("-").append(timestamp);
        }
        
        if (randomLength > 0) {
            String random = generateRandomString(randomLength);
            referenceId.append("-").append(random);
        }
        
        if (includeSequence) {
            String sequence = String.format("%04d", sequenceCounter.getAndIncrement() % 10000);
            referenceId.append("-").append(sequence);
        }
        
        String result = referenceId.toString();
        logger.debug("Generated custom reference ID: {}", result);
        return result;
    }
    
    /**
     * Validate if a reference ID follows the expected format
     */
    public boolean isValidReferenceId(String referenceId) {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            return false;
        }
        
        // Basic format validation: PREFIX-COMPONENT-COMPONENT-COMPONENT
        String[] parts = referenceId.split("-");
        if (parts.length < 2) {
            return false;
        }
        
        // Check if first part is a valid prefix
        String prefix = parts[0];
        boolean validPrefix = false;
        for (String validPrefixValue : PREFIXES) {
            if (validPrefixValue.equals(prefix)) {
                validPrefix = true;
                break;
            }
        }
        
        return validPrefix;
    }
    
    /**
     * Extract prefix from reference ID
     */
    public String extractPrefix(String referenceId) {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = referenceId.split("-");
        return parts.length > 0 ? parts[0] : null;
    }
    
    /**
     * Extract timestamp from reference ID (if present)
     */
    public LocalDateTime extractTimestamp(String referenceId) {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = referenceId.split("-");
        if (parts.length >= 2) {
            try {
                // Try to parse the second part as timestamp (yyyyMMddHHmmss)
                String timestampStr = parts[1];
                if (timestampStr.length() == 14) {
                    return LocalDateTime.parse(timestampStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                }
            } catch (Exception e) {
                logger.debug("Could not extract timestamp from reference ID: {}", referenceId);
            }
        }
        
        return null;
    }
    
    /**
     * Generate a random string of specified length
     */
    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        
        return randomString.toString();
    }
    
    /**
     * Get statistics about generated reference IDs
     */
    public ReferenceIdStats getStats() {
        return new ReferenceIdStats(
            sequenceCounter.get(),
            PREFIXES.length,
            LocalDateTime.now()
        );
    }
    
    /**
     * Stats class for reference ID generation
     */
    public static class ReferenceIdStats {
        private final long totalGenerated;
        private final int availablePrefixes;
        private final LocalDateTime lastGenerated;
        
        public ReferenceIdStats(long totalGenerated, int availablePrefixes, LocalDateTime lastGenerated) {
            this.totalGenerated = totalGenerated;
            this.availablePrefixes = availablePrefixes;
            this.lastGenerated = lastGenerated;
        }
        
        public long getTotalGenerated() {
            return totalGenerated;
        }
        
        public int getAvailablePrefixes() {
            return availablePrefixes;
        }
        
        public LocalDateTime getLastGenerated() {
            return lastGenerated;
        }
        
        @Override
        public String toString() {
            return "ReferenceIdStats{" +
                    "totalGenerated=" + totalGenerated +
                    ", availablePrefixes=" + availablePrefixes +
                    ", lastGenerated=" + lastGenerated +
                    '}';
        }
    }
}
