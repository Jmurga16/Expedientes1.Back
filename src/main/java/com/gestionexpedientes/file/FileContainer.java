package com.gestionexpedientes.file;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


public enum FileContainer {

    DEMANDA_IMAGEN("demanda-imagen", false, Map.of(
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "webp", "image/webp")),

    WORKFLOW_BPMN("workflow-bpmn", true, Map.of(
            "bpmn", "text/plain; charset=utf-8"));

    private final String containerName;
    private final boolean adminOnly;
    private final Map<String, String> contentTypes;

    FileContainer(String containerName, boolean adminOnly, Map<String, String> contentTypes) {
        this.containerName = containerName;
        this.adminOnly = adminOnly;
        this.contentTypes = contentTypes;
    }

    public static Optional<FileContainer> fromName(String name) {
        return Arrays.stream(values()).filter(c -> c.containerName.equals(name)).findFirst();
    }

    public String getContainerName() {
        return containerName;
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public Optional<String> contentTypeFor(String extension) {
        return Optional.ofNullable(contentTypes.get(extension));
    }
}
