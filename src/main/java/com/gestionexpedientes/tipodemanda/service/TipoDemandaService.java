package com.gestionexpedientes.tipodemanda.service;

import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import java.util.List;

public class TipoDemandaService {

    public List<TipoDemandaData.TipoDemanda> listarTiposDeDemanda() {
        return TipoDemandaData.getTipoDemandaList();
    }
}
