package com.gestionexpedientes.file.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    public String uploadFile(String containerName, MultipartFile file) throws Exception {
        String endpoint = String.format("https://%s.blob.core.windows.net/%s", accountName, containerName);
        String blobUrl = endpoint + "/" + file.getOriginalFilename();

        new BlobClientBuilder()
                .endpoint(endpoint)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .blobName(file.getOriginalFilename())
                .buildClient()
                .upload(file.getInputStream(), file.getSize(), true);

        return blobUrl;
    }

    public String copyFileWithNewName(String sourceBlobUrl, String destinationContainer, String blobName) throws Exception {

        String destinationBlobUrl = String.format("https://%s.blob.core.windows.net/%s", accountName, destinationContainer);

        BlobClient destinationBlobClient = new BlobClientBuilder()
                .endpoint(destinationBlobUrl)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .blobName(blobName)
                .buildClient();

        destinationBlobClient.beginCopy(sourceBlobUrl, Duration.ofSeconds(1));

        return destinationBlobClient.getBlobUrl();
    }

    public String copyFile(String sourceContainer, String destinationContainer, String sourceBlobUrl) throws Exception {

        //String sourceBlobUrl = String.format("https://%s.blob.core.windows.net/%s/%s", accountName, sourceContainer, blobName);
        String destinationBlobUrl = String.format("https://%s.blob.core.windows.net/%s", accountName, destinationContainer);

        String blobName = "";

        BlobClient destinationBlobClient = new BlobClientBuilder()
                .endpoint(destinationBlobUrl)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .blobName(blobName)
                .buildClient();

        destinationBlobClient.beginCopy(sourceBlobUrl, Duration.ofSeconds(1));

        return destinationBlobClient.getBlobUrl();
    }

    public String readFile(String containerName, String blobName) throws Exception {
        String endpoint = String.format("https://%s.blob.core.windows.net/%s", accountName, containerName);

        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(endpoint)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .blobName(blobName)
                .buildClient();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.download(outputStream);

        return outputStream.toString(StandardCharsets.UTF_8.name());
    }

    public List<String> listFiles(String containerName) throws Exception {
        String endpoint = String.format("https://%s.blob.core.windows.net/%s", accountName, containerName);

        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .endpoint(endpoint)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .buildClient();

        List<String> blobNames = new ArrayList<>();
        for (BlobItem blobItem : containerClient.listBlobs()) {
            blobNames.add(blobItem.getName());
        }

        return blobNames;
    }

}