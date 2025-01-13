package com.gestionexpedientes.workflow.dto;

import javax.validation.constraints.NotBlank;

public class WorkflowDto {

    @NotBlank(message = "Nombre es Obligatorio")
    private String nombre;
    @NotBlank(message = "Descripcion es Obligatorio")
    private String descripcion;
    private int idTipoDemanda;
    private int idTipologia;
    private int idSubtipologia;
    private String bpmn;
    private int estado;

    public WorkflowDto(String nombre, String descripcion, int idTipoDemanda, int idTipologia, int idSubtipologia, String bpmn, int estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idTipoDemanda = idTipoDemanda;
        this.idTipologia = idTipologia;
        this.idSubtipologia = idSubtipologia;
        this.bpmn = bpmn;
        this.estado = estado;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdTipoDemanda() {
        return idTipoDemanda;
    }

    public void setIdTipoDemanda(int idTipoDemanda) {
        this.idTipoDemanda = idTipoDemanda;
    }

    public int getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(int idTipologia) {
        this.idTipologia = idTipologia;
    }

    public int getIdSubtipologia() {
        return idSubtipologia;
    }

    public void setIdSubtipologia(int idSubtipologia) {
        this.idSubtipologia = idSubtipologia;
    }

    public String getBpmn() {
        return bpmn;
    }

    public void setBpmn(String bpmn) {
        this.bpmn = bpmn;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
