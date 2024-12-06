package com.gestionexpedientes.area.entity;

import com.gestionexpedientes.global.entity.EntityId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "area")
public class AreaEntity extends EntityId {
        private String nombre;
        private int estado;

    public AreaEntity(int id, String nombre, int estado) {
        this.id = id;
        this.nombre = nombre;
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}

