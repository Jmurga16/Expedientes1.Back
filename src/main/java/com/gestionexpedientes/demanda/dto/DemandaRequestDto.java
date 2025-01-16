package com.gestionexpedientes.demanda.dto;

import javax.validation.constraints.NotBlank;
import java.util.Date;

public class DemandaRequestDto {

    private int idUsuario;
    private String caratula;
    private int idTipoDemanda;
    private int idTipologia;
    private int idSubtipologia;
    private String domicilio;
    private String rutaImagen;
    private String informacionAdicional;
    private String paso;
    private String urlBpmn;
    private int estado;

    public DemandaRequestDto(int idUsuario, String caratula, int idTipoDemanda, int idTipologia, int idSubtipologia, String domicilio,
                             String rutaImagen, String informacionAdicional, String paso, String urlBpmn, int estado) {
        this.idUsuario = idUsuario;
        this.caratula = caratula;
        this.idTipoDemanda = idTipoDemanda;
        this.idTipologia = idTipologia;
        this.idSubtipologia = idSubtipologia;
        this.domicilio = domicilio;
        this.rutaImagen = rutaImagen;
        this.informacionAdicional = informacionAdicional;
        this.paso = paso;
        this.urlBpmn = urlBpmn;
        this.estado = estado;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
