package com.gestionexpedientes.global.dto;

public class BpmnDto {
    private String bpmn;

    public BpmnDto(String bpmn) {
        this.bpmn = bpmn;
    }

    public String getBpmn() {
        return bpmn;
    }

    public void setBpmn(String bpmn) {
        this.bpmn = bpmn;
    }
}
