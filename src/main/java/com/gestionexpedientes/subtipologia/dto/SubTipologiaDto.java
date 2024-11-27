package com.gestionexpedientes.subtipologia.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SubTipologiaDto {

    @NotBlank(message = "Nombre es Obligatorio")
    private String nombre;
    @NotNull(message = "Tipologia es Obligatorio")
    private int idTipologia;
    private int estado;

    public SubTipologiaDto(String nombre, int idTipologia, int estado) {
        this.nombre = nombre;
        this.idTipologia = idTipologia;
        this.estado = estado;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(int idTipologia) {
        this.idTipologia = idTipologia;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
