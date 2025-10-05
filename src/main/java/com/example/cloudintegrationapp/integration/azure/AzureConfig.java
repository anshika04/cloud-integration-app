package com.example.cloudintegrationapp.integration.azure;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "azure.enabled", havingValue = "true", matchIfMissing = false)
public class AzureConfig {

    @Value("${azure.keyvault.url}")
    private String keyVaultUrl;

    @Value("${azure.storage.account-name}")
    private String storageAccountName;

    @Bean
    public SecretClient secretClient() {
        return new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net", storageAccountName))
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }

    @Bean
    public QueueServiceClient queueServiceClient() {
        return new QueueServiceClientBuilder()
                .endpoint(String.format("https://%s.queue.core.windows.net", storageAccountName))
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }
}
