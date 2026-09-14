package com.gestionexpedientes.demanda.controller;

import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.dto.DemandaListDto;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.service.DemandaService;
import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.dto.PageDto;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.security.service.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demanda")
public class DemandaController {
    private final DemandaService demandaService;

    public DemandaController(DemandaService demandaService) {
        this.demandaService = demandaService;
    }

    @GetMapping
    public ResponseEntity<PageDto<DemandaListDto>> getAll(@RequestParam(required = false) String search,
                                                          @RequestParam(defaultValue = "1") int pageIndex,
                                                          @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(demandaService.getDatatable(search, pageIndex, pageSize, CurrentUser.get()));
    }

    @GetMapping("/resumen")
    public ResponseEntity<Map<Integer, Long>> getResumen() {
        return ResponseEntity.ok(demandaService.getResumen(CurrentUser.get()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activos")
    public ResponseEntity<List<DemandaEntity>> getActives() {
        return ResponseEntity.ok(demandaService.getActives());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandaEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(demandaService.getOne(id, CurrentUser.get()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody DemandaRequestDto dto) throws Exception {
        DemandaEntity demanda = demandaService.save(dto, CurrentUser.get());
        String message = demanda.getCaratula() + " ha sido guardado";

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK);
        response.put("message", message);
        response.put("id", demanda.getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody DemandaRequestDto dto) throws ResourceNotFoundException {
        DemandaEntity demanda = demandaService.update(id, dto, CurrentUser.get());
        String message = demanda.getCaratula() + " ha sido actualizado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        DemandaEntity demanda = demandaService.delete(id, CurrentUser.get());
        String message = demanda.getCaratula() + " ha sido eliminado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
