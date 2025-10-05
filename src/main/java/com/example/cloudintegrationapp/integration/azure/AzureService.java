package com.example.cloudintegrationapp.integration.azure;

import com.azure.core.util.BinaryData;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Service
@ConditionalOnProperty(name = "azure.enabled", havingValue = "true", matchIfMissing = false)
public class AzureService {

    private static final Logger logger = LoggerFactory.getLogger(AzureService.class);

    @Autowired
    private SecretClient secretClient;

    @Autowired
    private BlobServiceClient blobServiceClient;

    @Autowired
    private QueueServiceClient queueServiceClient;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Value("${azure.queue.name}")
    private String queueName;

    public String getSecret(String secretName) {
        try {
            return secretClient.getSecret(secretName).getValue();
        } catch (Exception e) {
            logger.error("Failed to retrieve secret: {}", secretName, e);
            throw new RuntimeException("Failed to retrieve secret", e);
        }
    }

    public void uploadBlob(String blobName, byte[] data) {
        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            InputStream inputStream = new ByteArrayInputStream(data);
            blobClient.upload(inputStream, data.length);
            
            logger.info("Successfully uploaded blob: {}", blobName);
        } catch (Exception e) {
            logger.error("Failed to upload blob: {}", blobName, e);
            throw new RuntimeException("Failed to upload blob", e);
        }
    }

    public byte[] downloadBlob(String blobName) {
        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            
            BinaryData binaryData = blobClient.downloadContent();
            return binaryData.toBytes();
        } catch (Exception e) {
            logger.error("Failed to download blob: {}", blobName, e);
            throw new RuntimeException("Failed to download blob", e);
        }
    }

    public void sendQueueMessage(String message) {
        try {
            QueueClient queueClient = queueServiceClient.getQueueClient(queueName);
            queueClient.sendMessage(Base64.getEncoder().encodeToString(message.getBytes()));
            
            logger.info("Successfully sent message to queue: {}", queueName);
        } catch (Exception e) {
            logger.error("Failed to send message to queue", e);
            throw new RuntimeException("Failed to send message to queue", e);
        }
    }

    public String receiveQueueMessage() {
        try {
            QueueClient queueClient = queueServiceClient.getQueueClient(queueName);
            var messages = queueClient.receiveMessages(1);
            
//            if (messages != null && !messages.isEmpty()) {
//                var message = messages.get(0);
//                String decodedMessage = new String(Base64.getDecoder().decode(message.getMessageText()));
//                queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt());
//                return decodedMessage;
//            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to receive message from queue", e);
            throw new RuntimeException("Failed to receive message from queue", e);
        }
    }
}
