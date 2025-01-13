package com.gestionexpedientes.subtipologia.controller;

import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.subtipologia.dto.SubTipologiaDto;
import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import com.gestionexpedientes.subtipologia.service.SubTipologiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/subtipologia")
@CrossOrigin
public class SubTipologiaController {
    @Autowired
    SubTipologiaService subtipologiaService;

    @GetMapping
    public ResponseEntity<List<SubTipologiaEntity>> getAll() {
        return ResponseEntity.ok(subtipologiaService.getAll());
    }


    @GetMapping("/activos")
    public ResponseEntity<List<SubTipologiaEntity>> getActives() {
        return ResponseEntity.ok(subtipologiaService.getActives());
    }

    @GetMapping("/tipologia/{idTipologia}")
    public ResponseEntity<List<SubTipologiaEntity>> getByIdTipologia(@PathVariable("idTipologia") int idTipologia) {
        return ResponseEntity.ok(subtipologiaService.getByIdTipologia(idTipologia));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SubTipologiaEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(subtipologiaService.getOne(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody SubTipologiaDto dto) throws AttributeException {
        SubTipologiaEntity subtipologia = subtipologiaService.save(dto);
        String message = subtipologia.getNombre() + " ha sido guardado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody SubTipologiaDto dto) throws ResourceNotFoundException, AttributeException {
        SubTipologiaEntity subtipologia = subtipologiaService.update(id, dto);
        String message = subtipologia.getNombre() + " ha sido actualizado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        SubTipologiaEntity subtipologia = subtipologiaService.delete(id);
        String message = subtipologia.getNombre() + " ha sido eliminado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
