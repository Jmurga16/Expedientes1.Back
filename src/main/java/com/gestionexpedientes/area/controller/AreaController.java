package com.gestionexpedientes.area.controller;

import com.gestionexpedientes.area.dto.AreaDto;
import com.gestionexpedientes.area.entity.AreaEntity;
import com.gestionexpedientes.area.service.AreaService;
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
@RequestMapping("/area")
@CrossOrigin
public class AreaController {
    @Autowired
    AreaService areaService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AreaEntity>> getAll() {
        return ResponseEntity.ok(areaService.getAll());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<AreaEntity>> getActives() {
        return ResponseEntity.ok(areaService.getActives());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<AreaEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(areaService.getOne(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody AreaDto dto) throws AttributeException {
        AreaEntity area = areaService.save(dto);
        String message = area.getNombre() + " ha sido guardado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody AreaDto dto) throws ResourceNotFoundException, AttributeException {
        AreaEntity area = areaService.update(id, dto);
        String message = area.getNombre() + " ha sido actualizado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        AreaEntity area = areaService.delete(id);
        String message = area.getNombre() + " ha sido eliminado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
