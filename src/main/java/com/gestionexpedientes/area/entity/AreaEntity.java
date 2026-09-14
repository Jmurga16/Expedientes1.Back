package com.gestionexpedientes.area.entity;

import com.gestionexpedientes.global.entity.CatalogEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "area")
public class AreaEntity extends CatalogEntity {

    public AreaEntity(int id, String nombre, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }
}
