package com.gestionexpedientes.workflow.dto;

import com.gestionexpedientes.global.dto.CatalogDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class WorkflowDto implements CatalogDto {

    @NotBlank(message = "Nombre es Obligatorio")
    private String nombre;
    @NotBlank(message = "Descripcion es Obligatorio")
    private String descripcion;
    @NotNull(message = "Tipo de demanda es Obligatorio")
    private Integer idTipoDemanda;
    @NotNull(message = "Tipologia es Obligatorio")
    private Integer idTipologia;
    @NotNull(message = "Subtipologia es Obligatorio")
    private Integer idSubtipologia;
    private String bpmn;
    private int estado;

    public WorkflowDto(String nombre, String descripcion, Integer idTipoDemanda, Integer idTipologia, Integer idSubtipologia, String bpmn, int estado) {
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

    public Integer getIdTipoDemanda() {
        return idTipoDemanda;
    }

    public void setIdTipoDemanda(Integer idTipoDemanda) {
        this.idTipoDemanda = idTipoDemanda;
    }

    public Integer getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(Integer idTipologia) {
        this.idTipologia = idTipologia;
    }

    public Integer getIdSubtipologia() {
        return idSubtipologia;
    }

    public void setIdSubtipologia(Integer idSubtipologia) {
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
