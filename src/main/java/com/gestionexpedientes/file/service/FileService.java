package com.gestionexpedientes.file.service;

import com.azure.core.util.BinaryData;
import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.BlobRequestConditions;
import com.azure.storage.blob.options.BlobParallelUploadOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;
import com.gestionexpedientes.file.FileContainer;
import com.gestionexpedientes.global.exceptions.AttributeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    private static final Duration COPY_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration SAS_TTL = Duration.ofMinutes(10);
    private static final Set<String> READABLE_CONTAINERS = Set.of("demanda-imagen", "demanda-bpmn", "workflow-bpmn");

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;


    public String uploadFile(FileContainer container, MultipartFile file) throws Exception {
        if (file.isEmpty())
            throw new AttributeException("El archivo esta vacio.");
        if (file.getSize() > MAX_FILE_SIZE)
            throw new AttributeException("El archivo supera el tamaño máximo de 2 MB.");

        String extension = StringUtils.getFilenameExtension(StringUtils.cleanPath(String.valueOf(file.getOriginalFilename())));
        extension = extension == null ? "" : extension.toLowerCase(Locale.ROOT);
        String contentType = container.contentTypeFor(extension)
                .orElseThrow(() -> new AttributeException("Tipo de archivo no permitido."));

        byte[] content = file.getBytes();
        if (!matchesContent(extension, content))
            throw new AttributeException("El contenido del archivo no corresponde a su extension.");

        BlobClient blobClient = blobClient(container.getContainerName(), UUID.randomUUID() + "." + extension);
        // If-None-Match: * -> falla si el blob ya existe, nunca sobrescribe.
        BlobParallelUploadOptions options = new BlobParallelUploadOptions(BinaryData.fromBytes(content))
                .setHeaders(new BlobHttpHeaders().setContentType(contentType))
                .setRequestConditions(new BlobRequestConditions().setIfNoneMatch("*"));
        blobClient.uploadWithResponse(options, null, null);

        return blobClient.getBlobUrl();
    }

    public String copyFileWithNewName(String sourceBlobUrl, String destinationContainer, String blobName) throws Exception {
        BlobClient destinationBlobClient = blobClient(destinationContainer, blobName);
        String sourceWithSas = sasUrl(sourceBlobUrl);
        LongRunningOperationStatus status;
        try {
            status = destinationBlobClient.beginCopy(sourceWithSas, Duration.ofSeconds(1))
                    .waitForCompletion(COPY_TIMEOUT)
                    .getStatus();
        } catch (RuntimeException e) {
            throw new AttributeException("No se pudo copiar el diagrama BPMN del workflow.");
        }
        if (status != LongRunningOperationStatus.SUCCESSFULLY_COMPLETED)
            throw new AttributeException("No se pudo copiar el diagrama BPMN del workflow.");
        return destinationBlobClient.getBlobUrl();
    }

    /** Contenedor al que pertenece la URL, validando que sea de la cuenta configurada. */
    public String containerOf(String blobUrl) throws AttributeException {
        return parse(blobUrl)[0];
    }

    /** URL de lectura firmada y de corta duracion para un blob de la cuenta configurada. */
    public String sasUrl(String blobUrl) throws AttributeException {
        String[] parts = parse(blobUrl);
        BlobClient blobClient = blobClient(parts[0], parts[1]);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plus(SAS_TTL),
                new BlobSasPermission().setReadPermission(true));
        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(values);
    }

    private String[] parse(String blobUrl) throws AttributeException {
        URI uri;
        try {
            uri = URI.create(blobUrl);
        } catch (IllegalArgumentException e) {
            throw new AttributeException("URL de archivo invalida.");
        }

        if (!String.format("%s.blob.core.windows.net", accountName).equals(uri.getHost()))
            throw new AttributeException("URL de archivo no permitida.");

        String path = uri.getRawPath() == null ? "" : uri.getRawPath();
        String[] segments = path.split("/", 3);
        if (segments.length < 3 || segments[2].isEmpty() || !READABLE_CONTAINERS.contains(segments[1]))
            throw new AttributeException("URL de archivo no permitida.");

        return new String[]{segments[1], URLDecoder.decode(segments[2], StandardCharsets.UTF_8)};
    }

    /** Lee un blob de la cuenta configurada a partir de su URL completa. */
    public String readBlobUrl(String blobUrl) {
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .buildClient();
        return download(blobClient);
    }

    private String download(BlobClient blobClient) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.downloadStream(outputStream);
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }

    private BlobClient blobClient(String containerName, String blobName) {
        return new BlobClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/%s", accountName, containerName))
                .credential(new StorageSharedKeyCredential(accountName, accountKey))
                .blobName(blobName)
                .buildClient();
    }

    /** Verifica la firma real del archivo, no el content-type que declara el cliente. */
    private boolean matchesContent(String extension, byte[] content) {
        switch (extension) {
            case "png":
                return startsWith(content, 0x89, 'P', 'N', 'G');
            case "jpg":
            case "jpeg":
                return startsWith(content, 0xFF, 0xD8, 0xFF);
            case "webp":
                return startsWith(content, 'R', 'I', 'F', 'F')
                        && content.length > 12 && new String(content, 8, 4, StandardCharsets.US_ASCII).equals("WEBP");
            case "bpmn":
                String xml = new String(content, StandardCharsets.UTF_8).replace("﻿", "").trim();
                return xml.startsWith("<") && xml.contains("definitions")
                        && !xml.toLowerCase(Locale.ROOT).contains("<script");
            default:
                return false;
        }
    }

    private boolean startsWith(byte[] content, int... signature) {
        if (content.length < signature.length)
            return false;
        for (int i = 0; i < signature.length; i++) {
            if ((content[i] & 0xFF) != signature[i])
                return false;
        }
        return true;
    }
}
