package com.gestionexpedientes.demanda.controller;

import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.dto.DemandaListDto;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.service.DemandaService;
import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/demanda")
@CrossOrigin
public class DemandaController {
    @Autowired
    DemandaService demandaService;


    @GetMapping
    public ResponseEntity<List<DemandaListDto>> getAll() {
        return ResponseEntity.ok(demandaService.getDatatable());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<DemandaEntity>> getActives() {
        return ResponseEntity.ok(demandaService.getActives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandaEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(demandaService.getOne(id));
    }

    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody DemandaRequestDto dto) throws AttributeException {
        DemandaEntity demanda = demandaService.save(dto);
        String message = demanda.getCaratula() + " ha sido guardado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody DemandaRequestDto dto) throws ResourceNotFoundException, AttributeException {
        DemandaEntity demanda = demandaService.update(id, dto);
        String message = demanda.getCaratula() + " ha sido actualizado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        DemandaEntity demanda = demandaService.delete(id);
        String message = demanda.getCaratula() + " ha sido eliminado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
