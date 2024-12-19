package com.gestionexpedientes.workflow.dto;

public class WorkflowListDto {
    private int id;
    private String nombre;
    private String descripcion;
    private String tipoDemanda;
    private String tipologia;
    private String subtipologia;
    private int estado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTipoDemanda() {
        return tipoDemanda;
    }

    public void setTipoDemanda(String tipoDemanda) {
        this.tipoDemanda = tipoDemanda;
    }

    public String getTipologia() {
        return tipologia;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public String getSubtipologia() {
        return subtipologia;
    }

    public void setSubtipologia(String subtipologia) {
        this.subtipologia = subtipologia;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
