package com.gestionexpedientes.global.entity;

public abstract class CatalogEntity extends EntityId {

    protected String nombre;
    protected int estado;

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
