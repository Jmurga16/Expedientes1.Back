package com.gestionexpedientes.subtipologia.entity;

import com.gestionexpedientes.global.entity.EntityId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subtipologia")
public class SubTipologiaEntity extends EntityId {
        private String nombre;
        private int idTipologia;
        private int estado;

    public SubTipologiaEntity(int id, String nombre, int idTipologia, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.idTipologia = idTipologia;
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

