package com.gestionexpedientes.global.exceptions;

public class WorkflowNotConfiguredException extends Exception {

    public static final String CODE = "WORKFLOW_NOT_CONFIGURED";

    public WorkflowNotConfiguredException(String message) {
        super(message);
    }
}
