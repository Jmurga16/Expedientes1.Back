package com.gestionexpedientes.historial_demanda.dto;

import javax.validation.constraints.NotBlank;

public class HistorialDemandaFormDto {

    private int idUsuario;
    private int idDemanda;
    @NotBlank
    private String paso;
    private int estado;
    private String observaciones;

    public HistorialDemandaFormDto(int idUsuario, int idDemanda, String paso, int estado, String observaciones) {
        this.idUsuario = idUsuario;
        this.idDemanda = idDemanda;
        this.paso = paso;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdDemanda() {
        return idDemanda;
    }

    public void setIdDemanda(int idDemanda) {
        this.idDemanda = idDemanda;
    }

    public String getPaso() {
        return paso;
    }

    public void setPaso(String paso) {
        this.paso = paso;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}