package com.gestionexpedientes.file.controller;

import com.gestionexpedientes.file.FileContainer;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.security.service.CurrentUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("{container}")
    public ResponseEntity<Map<String, String>> uploadFile(@PathVariable("container") String containerName,
                                                          @RequestParam("file") MultipartFile file) throws Exception {
        FileContainer container = FileContainer.fromName(containerName)
                .orElseThrow(() -> new AttributeException("Contenedor no permitido."));

        if (container.isAdminOnly() && !CurrentUser.get().isAdmin())
            throw new AccessDeniedException("Solo un administrador puede subir a " + containerName);

        String fileUrl = fileService.uploadFile(container, file);
        return ResponseEntity.ok(Collections.singletonMap("fileUrl", fileUrl));
    }
}
