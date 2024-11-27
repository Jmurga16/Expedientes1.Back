package com.gestionexpedientes.tipologia.entity;

import com.gestionexpedientes.global.entity.EntityId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tipologia")
public class TipologiaEntity extends EntityId {
        private String nombre;
        private String descripcion;
        private int estado;

    public TipologiaEntity(int id, String nombre, String descripcion, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
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

