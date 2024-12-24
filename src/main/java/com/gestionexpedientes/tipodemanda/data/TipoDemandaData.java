package com.gestionexpedientes.tipodemanda.data;

import java.util.ArrayList;
import java.util.List;

public class TipoDemandaData {

    public static class TipoDemanda {
        private int id;
        private String codigo;
        private String nombre;

        public TipoDemanda(int id, String codigo, String nombre) {
            this.id = id;
            this.codigo = codigo;
            this.nombre = nombre;
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

        @Override
        public String toString() {
            return "TipoDemanda{" +
                    "id=" + id +
                    ", codigo='" + codigo + '\'' +
                    ", nombre='" + nombre + '\'' +
                    '}';
        }
    }

    public static List<TipoDemanda> getTipoDemandaList() {
        List<TipoDemanda> list = new ArrayList<>();
        list.add(new TipoDemanda(1, "PT", "Petición"));
        list.add(new TipoDemanda(2, "QJ", "Queja"));
        list.add(new TipoDemanda(3, "SG", "Sugerencia"));
        list.add(new TipoDemanda(4, "RC", "Reclamo"));
        return list;
    }
}