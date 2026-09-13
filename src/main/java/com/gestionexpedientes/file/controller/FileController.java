package com.gestionexpedientes.file.controller;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.demanda.service.DemandaAccessService;
import com.gestionexpedientes.file.FileContainer;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.security.service.CurrentUser;
import com.gestionexpedientes.security.service.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;
    private final IDemandaRepository demandaRepository;
    private final DemandaAccessService demandaAccessService;

    public FileController(FileService fileService,
                          IDemandaRepository demandaRepository,
                          DemandaAccessService demandaAccessService) {
        this.fileService = fileService;
        this.demandaRepository = demandaRepository;
        this.demandaAccessService = demandaAccessService;
    }

    @PostMapping("{container}")
    public ResponseEntity<Map<String, String>> uploadFile(@PathVariable("container") String containerName,
                                                          @RequestParam("file") MultipartFile file) throws Exception {
        FileContainer container = FileContainer.fromName(containerName)
                .orElseThrow(() -> new AttributeException("Contenedor no permitido."));

        if (container.isAdminOnly() && !CurrentUser.get().isAdmin())
            throw new AccessDeniedException("Solo un administrador puede subir a " + containerName);

        String fileUrl = fileService.uploadFile(container, file);
        return ResponseEntity.ok(Map.of("fileUrl", fileUrl, "viewUrl", fileService.sasUrl(fileUrl)));
    }

    @GetMapping("view")
    public ResponseEntity<Map<String, String>> viewUrl(@RequestParam("url") String url) throws Exception {
        checkReadAccess(url, CurrentUser.get());
        return ResponseEntity.ok(Collections.singletonMap("url", fileService.sasUrl(url)));
    }

    private void checkReadAccess(String url, UserPrincipal user) throws AttributeException {
        String container = fileService.containerOf(url);

        if (FileContainer.WORKFLOW_BPMN.getContainerName().equals(container)) {
            if (!user.isAdmin())
                throw new AccessDeniedException("Sin acceso al diagrama del flujo.");
            return;
        }

        Optional<DemandaEntity> demanda = FileContainer.DEMANDA_IMAGEN.getContainerName().equals(container)
                ? demandaRepository.findFirstByRutaImagen(url)
                : demandaRepository.findFirstByUrlBpmn(url);

        demandaAccessService.checkAccess(
                demanda.orElseThrow(() -> new AccessDeniedException("Sin acceso al archivo solicitado.")), user);
    }
}
