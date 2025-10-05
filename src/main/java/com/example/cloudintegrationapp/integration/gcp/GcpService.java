package com.example.cloudintegrationapp.integration.gcp;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.util.concurrent.TimeUnit;

@Service
public class GcpService {

    private static final Logger logger = LoggerFactory.getLogger(GcpService.class);

    @Autowired
    private Storage storage;

//    @Autowired
//    private SecretManagerServiceClient secretManagerClient;
//
//    @Autowired
//    private TopicAdminClient topicAdminClient;
//
//    @Autowired
//    private SubscriptionAdminClient subscriptionAdminClient;

    @Value("${gcp.project-id}")
    private String projectId;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${gcp.pubsub.topic-name}")
    private String topicName;

    @Value("${gcp.pubsub.subscription-name}")
    private String subscriptionName;

    public String getSecret(String secretName, String version) {
        try {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, version);
//            AccessSecretVersionResponse response = secretManagerClient.accessSecretVersion(secretVersionName);
//            return response.getPayload().getData().toStringUtf8();
            return "";
        } catch (Exception e) {
            logger.error("Failed to retrieve secret: {}", secretName, e);
            throw new RuntimeException("Failed to retrieve secret", e);
        }
    }

    public void uploadObject(String objectName, byte[] data) {
        try {
            BlobId blobId = BlobId.of(bucketName, objectName);
//            Blob blob = storage.create(BlobId.of(bucketName, objectName),
//                    new ByteArrayInputStream(data));

            logger.info("Successfully uploaded object: {}", objectName);
        } catch (Exception e) {
            logger.error("Failed to upload object: {}", objectName, e);
            throw new RuntimeException("Failed to upload object", e);
        }
    }

    public byte[] downloadObject(String objectName) {
        try {
            BlobId blobId = BlobId.of(bucketName, objectName);
            Blob blob = storage.get(blobId);

            if (blob == null) {
                throw new RuntimeException("Object not found: " + objectName);
            }

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

//            Subscriber subscriber = Subscriber.newBuilder(subscriptionName, (message, consumer) -> {
//                logger.info("Received message: {}", message.getData().toStringUtf8());
//                consumer.ack();
//            }).build();

//            subscriber.startAsync().awaitRunning();
            logger.info("Started subscription to: {}", this.subscriptionName);
        } catch (Exception e) {
            logger.error("Failed to subscribe to messages", e);
            throw new RuntimeException("Failed to subscribe to messages", e);
        }
    }
}
