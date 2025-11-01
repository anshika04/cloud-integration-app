package com.example.cloudintegrationapp.integration.gcp;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "gcp.enabled", havingValue = "true", matchIfMissing = false)
public class GcpService {

    private static final Logger logger = LoggerFactory.getLogger(GcpService.class);

    @Autowired
    private Storage storage;

    @Autowired
    private SecretManagerServiceClient secretManagerClient;

    @Autowired
    private TopicAdminClient topicAdminClient;

    @Autowired
    private SubscriptionAdminClient subscriptionAdminClient;

    @Autowired
    private Environment environment;

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${gcp.pubsub.topic-name}")
    private String topicName;

    @Value("${gcp.pubsub.subscription-name}")
    private String subscriptionName;

    /**
     * Get environment-specific storage path prefix
     */
    private String getStoragePathPrefix() {
        String[] activeProfiles = environment.getActiveProfiles();
        String profile = activeProfiles.length > 0 ? activeProfiles[0] : "dev";
        
        // Map profile to storage path
        switch (profile.toLowerCase()) {
            case "dev":
            case "docker":
                return "dev/reports";
            case "qa":
                return "qa/reports";
            case "prod":
            case "production":
                return "prod/reports";
            default:
                logger.warn("Unknown profile '{}', defaulting to dev/reports", profile);
                return "dev/reports";
        }
    }

    /**
     * Build full object path with environment prefix
     */
    private String buildObjectPath(String objectName) {
        String prefix = getStoragePathPrefix();
        if (objectName == null || objectName.isEmpty()) {
            return prefix;
        }
        // Remove leading slash if present to avoid double slashes
        String cleanObjectName = objectName.startsWith("/") ? objectName.substring(1) : objectName;
        return prefix + "/" + cleanObjectName;
    }

    public String getSecret(String secretName, String version) {
        try {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, version);
            AccessSecretVersionResponse response = secretManagerClient.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            logger.error("Failed to retrieve secret: {}", secretName, e);
            throw new RuntimeException("Failed to retrieve secret", e);
        }
    }

    public void uploadObject(String objectName, byte[] data) {
        try {
            String objectPath = buildObjectPath(objectName);
            BlobId blobId = BlobId.of(bucketName, objectPath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, data);

            logger.info("Successfully uploaded object: {} -> gs://{}/{}", objectName, bucketName, objectPath);
        } catch (Exception e) {
            logger.error("Failed to upload object: {}", objectName, e);
            throw new RuntimeException("Failed to upload object", e);
        }
    }

    public byte[] downloadObject(String objectName) {
        try {
            String objectPath = buildObjectPath(objectName);
            BlobId blobId = BlobId.of(bucketName, objectPath);
            Blob blob = storage.get(blobId);

            if (blob == null) {
                throw new RuntimeException("Object not found: " + objectPath);
            }

            logger.info("Successfully downloaded object: gs://{}/{}", bucketName, objectPath);
            return blob.getContent();
        } catch (Exception e) {
            logger.error("Failed to download object: {}", objectName, e);
            throw new RuntimeException("Failed to download object", e);
        }
    }

    public void publishMessage(String message) {
        try {
            ProjectTopicName topicName = ProjectTopicName.of(projectId, this.topicName);
            Publisher publisher = Publisher.newBuilder(topicName).build();

            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFromUtf8(message))
                    .build();

            publisher.publish(pubsubMessage);
            publisher.shutdown();

            logger.info("Successfully published message to topic: {}", this.topicName);
        } catch (Exception e) {
            logger.error("Failed to publish message", e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }

    public void subscribeToMessages() {
        try {
            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, this.subscriptionName);

            MessageReceiver receiver = (message, consumer) -> {
                logger.info("Received message: {}", message.getData().toStringUtf8());
                consumer.ack();
            };
            Subscriber subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();

            subscriber.startAsync().awaitRunning();
            logger.info("Started subscription to: {}", this.subscriptionName);
        } catch (Exception e) {
            logger.error("Failed to subscribe to messages", e);
            throw new RuntimeException("Failed to subscribe to messages", e);
        }
    }

    public java.util.List<java.util.Map<String, Object>> listObjects() {
        try {
            String prefix = getStoragePathPrefix();
            java.util.List<java.util.Map<String, Object>> files = new java.util.ArrayList<>();
            
            for (Blob blob : storage.list(bucketName, 
                    Storage.BlobListOption.prefix(prefix)).iterateAll()) {
                // Skip directory markers (empty blobs with size 0)
                if (blob.getSize() == 0) {
                    continue;
                }
                
                java.util.Map<String, Object> fileInfo = new java.util.HashMap<>();
                fileInfo.put("name", blob.getName());
                fileInfo.put("path", blob.getName());
                fileInfo.put("size", blob.getSize());
                fileInfo.put("contentType", blob.getContentType());
                fileInfo.put("updated", blob.getUpdateTime());
                fileInfo.put("created", blob.getCreateTime());
                
                // Extract just filename from path
                String fullPath = blob.getName();
                String filename = fullPath.substring(fullPath.lastIndexOf('/') + 1);
                fileInfo.put("filename", filename);
                
                files.add(fileInfo);
            }
            
            logger.info("Retrieved {} files from environment path: {}", files.size(), prefix);
            return files;
        } catch (Exception e) {
            logger.error("Failed to list objects", e);
            throw new RuntimeException("Failed to list objects", e);
        }
    }

    public void deleteObject(String objectName) {
        try {
            String objectPath = buildObjectPath(objectName);
            BlobId blobId = BlobId.of(bucketName, objectPath);
            boolean deleted = storage.delete(blobId);
            
            if (deleted) {
                logger.info("Successfully deleted object: gs://{}/{}", bucketName, objectPath);
            } else {
                throw new RuntimeException("Object not found or could not be deleted: " + objectPath);
            }
        } catch (Exception e) {
            logger.error("Failed to delete object: {}", objectName, e);
            throw new RuntimeException("Failed to delete object", e);
        }
    }
}
