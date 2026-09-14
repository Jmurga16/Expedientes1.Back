package com.gestionexpedientes.subtipologia.entity;

import com.gestionexpedientes.global.entity.CatalogEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subtipologia")
public class SubTipologiaEntity extends CatalogEntity {

    private int idTipologia;

    public SubTipologiaEntity(int id, String nombre, int idTipologia, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.idTipologia = idTipologia;
        this.estado = estado;
    }

    public int getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(int idTipologia) {
        this.idTipologia = idTipologia;
    }
}
