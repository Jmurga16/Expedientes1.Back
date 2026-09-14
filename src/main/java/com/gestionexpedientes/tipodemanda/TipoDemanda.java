package com.gestionexpedientes.tipodemanda;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.Optional;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoDemanda {

    PETICION(1, "PT", "Petición"),
    QUEJA(2, "QJ", "Queja"),
    SUGERENCIA(3, "SG", "Sugerencia"),
    RECLAMO(4, "RC", "Reclamo");

    private final int id;
    private final String codigo;
    private final String nombre;

    TipoDemanda(int id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public static Optional<TipoDemanda> fromId(int id) {
        return Arrays.stream(values()).filter(t -> t.id == id).findFirst();
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }
}
