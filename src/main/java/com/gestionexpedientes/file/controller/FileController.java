package com.gestionexpedientes.file.controller;

import com.azure.storage.blob.BlobClientBuilder;
import com.gestionexpedientes.file.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("{container}")
    public ResponseEntity<?> uploadFile(@PathVariable("container") String containerName, @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileService.uploadFile(containerName, file);
            return ResponseEntity.ok().body("{\"fileUrl\": \"" + fileUrl + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
}