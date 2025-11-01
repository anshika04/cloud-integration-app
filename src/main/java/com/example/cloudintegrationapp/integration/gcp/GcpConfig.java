package com.example.cloudintegrationapp.integration.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretManagerServiceSettings;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
@ConditionalOnProperty(name = "gcp.enabled", havingValue = "true", matchIfMissing = false)
public class GcpConfig {

    private static final Logger logger = LoggerFactory.getLogger(GcpConfig.class);

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.credentials.service-account-key:}")
    private String serviceAccountKey;

    @Value("${gcp.credentials.key-file-path:}")
    private String keyFilePath;

    private GoogleCredentials getCredentials() throws IOException {
        // Priority: 1. Base64 encoded key, 2. File path, 3. Application Default Credentials
        if (serviceAccountKey != null && !serviceAccountKey.isEmpty()) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(serviceAccountKey);
                InputStream keyStream = new ByteArrayInputStream(keyBytes);
                logger.info("Using Base64 encoded service account key for GCP authentication");
                return GoogleCredentials.fromStream(keyStream);
            } catch (Exception e) {
                logger.warn("Failed to decode Base64 service account key, trying as JSON string", e);
                // Try as direct JSON string
                InputStream keyStream = new ByteArrayInputStream(serviceAccountKey.getBytes());
                return GoogleCredentials.fromStream(keyStream);
            }
        } else if (keyFilePath != null && !keyFilePath.isEmpty()) {
            logger.info("Using service account key file: {}", keyFilePath);
            java.io.File keyFile = new java.io.File(keyFilePath);
            if (!keyFile.exists() || !keyFile.canRead()) {
                throw new IOException("Service account key file not found or cannot be read: " + keyFilePath);
            }
            InputStream keyStream = new java.io.FileInputStream(keyFile);
            GoogleCredentials credentials = GoogleCredentials.fromStream(keyStream);
            keyStream.close();
            return credentials;
        } else {
            logger.info("Using Application Default Credentials for GCP authentication");
            return GoogleCredentials.getApplicationDefault();
        }
    }

    @Bean
    public Storage storage() throws IOException {
        GoogleCredentials credentials = getCredentials();
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
    }

    @Bean
    public SecretManagerServiceClient secretManagerClient() throws IOException {
        try {
            GoogleCredentials credentials = getCredentials();
            SecretManagerServiceSettings settings = SecretManagerServiceSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            return SecretManagerServiceClient.create(settings);
        } catch (Exception e) {
            logger.error("Failed to create SecretManagerServiceClient", e);
            throw new RuntimeException("Failed to create SecretManagerServiceClient", e);
        }
    }

    @Bean
    public TopicAdminClient topicAdminClient() throws IOException {
        try {
            GoogleCredentials credentials = getCredentials();
            TopicAdminSettings settings = TopicAdminSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            return TopicAdminClient.create(settings);
        } catch (Exception e) {
            logger.error("Failed to create TopicAdminClient", e);
            throw new RuntimeException("Failed to create TopicAdminClient", e);
        }
    }

    @Bean
    public SubscriptionAdminClient subscriptionAdminClient() throws IOException {
        try {
            GoogleCredentials credentials = getCredentials();
            SubscriptionAdminSettings settings = SubscriptionAdminSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            return SubscriptionAdminClient.create(settings);
        } catch (Exception e) {
            logger.error("Failed to create SubscriptionAdminClient", e);
            throw new RuntimeException("Failed to create SubscriptionAdminClient", e);
        }
    }
}
