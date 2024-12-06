package com.gestionexpedientes.area.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AreaDto {

    @NotBlank(message = "Nombre es Obligatorio")
    private String nombre;

    private int estado;

    public AreaDto(String nombre, int estado) {
        this.nombre = nombre;
        this.estado = estado;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
