package com.gestionexpedientes.tipologia.dto;

import javax.validation.constraints.NotBlank;

public class TipologiaDto {

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
