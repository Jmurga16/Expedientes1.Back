package com.gestionexpedientes.historial_demanda.entity;

import com.gestionexpedientes.global.entity.EntityId;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "historial_demanda")
public class HistorialDemandaEntity extends EntityId {
    private int idUsuario;
    private int idDemanda;
    private String paso;
    private int estado;
    private String observaciones;
    private Date fecha;

    public HistorialDemandaEntity(int id, int idUsuario, int idDemanda, String paso, int estado, String observaciones, Date fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idDemanda = idDemanda;
        this.paso = paso;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fecha = fecha;
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdDemanda() {
        return idDemanda;
    }

    public void setIdDemanda(int idDemanda) {
        this.idDemanda = idDemanda;
    }

    public String getPaso() {
        return paso;
    }

    public void setPaso(String paso) {
        this.paso = paso;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
