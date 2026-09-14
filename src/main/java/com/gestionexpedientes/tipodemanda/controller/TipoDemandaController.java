package com.gestionexpedientes.tipodemanda.controller;

import com.gestionexpedientes.tipodemanda.TipoDemanda;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipo-demanda")
public class TipoDemandaController {

    @GetMapping
    public List<TipoDemanda> getAll() {
        return List.of(TipoDemanda.values());
    }
}
