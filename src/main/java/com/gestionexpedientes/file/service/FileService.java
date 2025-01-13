package com.gestionexpedientes.file.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
}