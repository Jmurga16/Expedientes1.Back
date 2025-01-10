package com.gestionexpedientes.file.controller;

import com.azure.storage.blob.BlobClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileUploadController {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @PostMapping("{container}")
    public ResponseEntity<?> uploadFile(@PathVariable("container") String containerName, @RequestParam("file") MultipartFile file) {

        try {
            String endpoint = String.format("https://%s.blob.core.windows.net/%s", accountName, containerName);
            String blobUrl = endpoint + "/" + file.getOriginalFilename();

            new BlobClientBuilder()
                    .endpoint(endpoint)
                    .credential(new com.azure.storage.common.StorageSharedKeyCredential(accountName, accountKey))
                    .blobName(file.getOriginalFilename())
                    .buildClient()
                    .upload(file.getInputStream(), file.getSize(), true);

            return ResponseEntity.ok().body("{\"fileUrl\": \"" + blobUrl + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
}