package com.gestionexpedientes.tipologia.entity;

import com.gestionexpedientes.global.entity.CatalogEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tipologia")
public class TipologiaEntity extends CatalogEntity {

    private String descripcion;

    public TipologiaEntity(int id, String nombre, String descripcion, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
