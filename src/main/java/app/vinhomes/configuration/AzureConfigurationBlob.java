package app.vinhomes.configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class AzureConfigurationBlob {

    @Value("${blob.connection-string}")
    private String connectionString;

    @Value("${blob.container-name}")
    private String containerName;

    @Bean
    public BlobServiceClient clobServiceClient() {

        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder()
                        .connectionString(connectionString)
                        .buildClient();

        return blobServiceClient;

    }

    @Bean
    public BlobContainerClient blobContainerClient() {

        BlobContainerClient blobContainerClient =
                clobServiceClient()
                        .getBlobContainerClient(containerName);

        return blobContainerClient;

    }
}