package com.gestionexpedientes.tipologia.controller;

import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.tipologia.dto.TipologiaDto;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import com.gestionexpedientes.tipologia.service.TipologiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tipologia")
@CrossOrigin
public class TipologiaController {
    @Autowired
    TipologiaService tipologiaService;

    @GetMapping
    public ResponseEntity<List<TipologiaEntity>> getAll() {
        return ResponseEntity.ok(tipologiaService.getAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<TipologiaEntity>> getActives(){
        return ResponseEntity.ok(tipologiaService.getActives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipologiaEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(tipologiaService.getOne(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody TipologiaDto dto) throws AttributeException {
        TipologiaEntity tipologia = tipologiaService.save(dto);
        String message = tipologia.getNombre() + " ha sido guardado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody TipologiaDto dto) throws ResourceNotFoundException, AttributeException {
        TipologiaEntity tipologia = tipologiaService.update(id, dto);
        String message = tipologia.getNombre() + " ha sido actualizado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        TipologiaEntity tipologia = tipologiaService.delete(id);
        String message = tipologia.getNombre() + " ha sido eliminado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
