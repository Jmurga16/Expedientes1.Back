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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demanda")
@CrossOrigin
public class DemandaController {
    @Autowired
    DemandaService demandaService;


    @GetMapping

    public ResponseEntity<List<DemandaListDto>> getAll(@RequestParam(required = false) String search) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        boolean isAdmin = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        //boolean isUser = authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));

        String username = authentication.getName();

        List<DemandaListDto> data;

        if (isAdmin) {
            data = demandaService.getDatatable(search);
        } else {
            data = demandaService.getDatatableForUser(search, username);
        }

        return ResponseEntity.ok(data);
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
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody DemandaRequestDto dto) throws Exception {
        DemandaEntity demanda = demandaService.save(dto);
        String message = demanda.getCaratula() + " ha sido guardado";

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK);
        response.put("message", message);
        response.put("id", demanda.getId());

        return ResponseEntity.ok(response);
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
