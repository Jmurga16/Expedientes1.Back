package com.gestionexpedientes.tipodemanda.controller;

import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import com.gestionexpedientes.tipodemanda.service.TipoDemandaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipo-demanda")
public class TipoDemandaController {

    private final TipoDemandaService tipoDemandaService;

    public TipoDemandaController(TipoDemandaService tipoDemandaService) {
        this.tipoDemandaService = tipoDemandaService;
    }

    @GetMapping()
    public List<TipoDemandaData.TipoDemanda> obtenerTiposDemanda() {

        return tipoDemandaService.listarTiposDeDemanda();
    }
}