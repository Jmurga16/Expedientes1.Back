package com.gestionexpedientes.subtipologia.dto;

import com.gestionexpedientes.global.dto.CatalogDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubTipologiaDto implements CatalogDto {

    @NotBlank(message = "Nombre es Obligatorio")
    private String nombre;
    @NotNull(message = "Tipologia es Obligatorio")
    private Integer idTipologia;
    private int estado;

    public SubTipologiaDto(String nombre, Integer idTipologia, int estado) {
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

    public Integer getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(Integer idTipologia) {
        this.idTipologia = idTipologia;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
