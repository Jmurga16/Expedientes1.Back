package com.gestionexpedientes.tipodemanda.controller;

import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import com.gestionexpedientes.tipodemanda.service.TipoDemandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipo-demanda")
@CrossOrigin
public class TipoDemandaController {

    @Autowired
    TipoDemandaService tipoDemandaService;

    @GetMapping()
    public List<TipoDemandaData.TipoDemanda> obtenerTiposDemanda() {

        return tipoDemandaService.listarTiposDeDemanda();
    }
}