package com.gestionexpedientes.historial_demanda.controller;

import com.gestionexpedientes.demanda.service.DemandaService;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaListDto;
import com.gestionexpedientes.historial_demanda.service.HistorialDemandaService;
import com.gestionexpedientes.security.service.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial-demanda")
@CrossOrigin
public class HistorialDemandaController {
    @Autowired
    HistorialDemandaService historialDemandaService;
    @Autowired
    DemandaService demandaService;

    @GetMapping("/{idDemanda}")
    public ResponseEntity<List<HistorialDemandaListDto>> getAll(@PathVariable("idDemanda") int idDemanda) throws ResourceNotFoundException {
        demandaService.getOne(idDemanda, CurrentUser.get());
        List<HistorialDemandaListDto> data = historialDemandaService.getDatatable(idDemanda);
        return ResponseEntity.ok(data);
    }
}
