package com.gestionexpedientes.tipologia.dto;

import com.gestionexpedientes.global.dto.CatalogDto;

import jakarta.validation.constraints.NotBlank;

public class TipologiaDto implements CatalogDto {

    @NotBlank(message = "Nombre es Obligatorio")
    private String nombre;
    @NotBlank(message = "Descripcion es Obligatorio")
    private String descripcion;
    private int estado;

    public TipologiaDto(String nombre, String descripcion, int estado) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
