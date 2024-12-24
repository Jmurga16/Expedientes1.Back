package com.gestionexpedientes.tipodemanda.service;

import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoDemandaService {

    public List<TipoDemandaData.TipoDemanda> listarTiposDeDemanda() {
        return TipoDemandaData.getTipoDemandaList();
    }
}
