package com.gestionexpedientes.demanda.dto;

import javax.validation.constraints.NotNull;

/**
 * Datos que envia el cliente. idUsuario, caratula y urlBpmn se aceptan por compatibilidad con el front
 * pero el servidor los ignora: el demandante es el usuario autenticado y los otros dos los genera el backend.
 */
public class DemandaRequestDto {

    private Integer idUsuario;
    private String caratula;
    private int idTipoDemanda;
    private int idTipologia;
    private int idSubtipologia;
    private String domicilio;
    private String rutaImagen;
    private String informacionAdicional;
    private String paso;
    private String urlBpmn;
    @NotNull(message = "Estado es obligatorio")
    private Integer estado;
    private String observaciones;

    public DemandaRequestDto() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaratula() {
        return caratula;
    }

    public void setCaratula(String caratula) {
        this.caratula = caratula;
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

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getInformacionAdicional() {
        return informacionAdicional;
    }

    public void setInformacionAdicional(String informacionAdicional) {
        this.informacionAdicional = informacionAdicional;
    }

    public String getPaso() {
        return paso;
    }

    public void setPaso(String paso) {
        this.paso = paso;
    }

    public String getUrlBpmn() {
        return urlBpmn;
    }

    public void setUrlBpmn(String urlBpmn) {
        this.urlBpmn = urlBpmn;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
