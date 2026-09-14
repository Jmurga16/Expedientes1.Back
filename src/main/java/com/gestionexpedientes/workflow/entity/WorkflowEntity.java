package com.gestionexpedientes.workflow.entity;

import com.gestionexpedientes.global.entity.CatalogEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "workflow")
public class WorkflowEntity extends CatalogEntity {

    private String descripcion;
    private int idTipoDemanda;
    private int idTipologia;
    private int idSubtipologia;
    private String bpmn;

    public WorkflowEntity(int id, String nombre, String descripcion, int idTipoDemanda, int idTipologia, int idSubtipologia, String bpmn, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idTipoDemanda = idTipoDemanda;
        this.idTipologia = idTipologia;
        this.idSubtipologia = idSubtipologia;
        this.bpmn = bpmn;
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdTipoDemanda() {
        return idTipoDemanda;
    }

    public void setIdTipoDemanda(int idTipoDemanda) {
        this.idTipoDemanda = idTipoDemanda;
    }

    public int getIdTipologia() {
        return idTipologia;
    }

    public void setIdTipologia(int idTipologia) {
        this.idTipologia = idTipologia;
    }

    public int getIdSubtipologia() {
        return idSubtipologia;
    }

    public void setIdSubtipologia(int idSubtipologia) {
        this.idSubtipologia = idSubtipologia;
    }

    public String getBpmn() {
        return bpmn;
    }

    public void setBpmn(String bpmn) {
        this.bpmn = bpmn;
    }
}
