package com.example.cloudintegrationapp.integration.gcp;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GcpConfig {

    @Value("${gcp.project-id}")
    private String projectId;

    @Bean
    public Storage storage() {
        return StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }

    @Bean
    public SecretManagerServiceClient secretManagerClient() throws IOException {
//        return SecretManagerServiceClient.create();
        return null;
    }

    @Bean
    public TopicAdminClient topicAdminClient() throws IOException {
        return null;
//        return TopicAdminClient.create();
    }

    @Bean
    public SubscriptionAdminClient subscriptionAdminClient() throws IOException {
        return null;
//        return SubscriptionAdminClient.create();
    }
}
