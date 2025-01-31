package com.gestionexpedientes.historial_demanda.controller;

import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaFormDto;
import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaListDto;
import com.gestionexpedientes.historial_demanda.entity.HistorialDemandaEntity;
import com.gestionexpedientes.historial_demanda.service.HistorialDemandaService;
import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/historial-demanda")
@CrossOrigin
public class HistorialDemandaController {
    @Autowired
    HistorialDemandaService historialDemandaService;

    @GetMapping("/{idDemanda}")
    public ResponseEntity<List<HistorialDemandaListDto>> getAll(@PathVariable("idDemanda") int idDemanda) {
        List<HistorialDemandaListDto> data = historialDemandaService.getDatatable(idDemanda);
        return ResponseEntity.ok(data);
    }

    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody HistorialDemandaFormDto dto) throws Exception {
        HistorialDemandaEntity historial = historialDemandaService.save(dto);
        String message = "Historial de demanda guardado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody HistorialDemandaFormDto dto) throws ResourceNotFoundException, AttributeException {
        HistorialDemandaEntity historial = historialDemandaService.update(id, dto);
        String message = "Historial de demanda actualizado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

}
