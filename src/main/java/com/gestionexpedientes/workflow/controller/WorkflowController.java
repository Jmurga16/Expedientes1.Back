package com.gestionexpedientes.workflow.controller;

import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.workflow.dto.WorkflowDto;
import com.gestionexpedientes.workflow.dto.WorkflowListDto;
import com.gestionexpedientes.workflow.entity.WorkflowEntity;
import com.gestionexpedientes.workflow.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/workflow")
@CrossOrigin
public class WorkflowController {
    @Autowired
    WorkflowService workflowService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<WorkflowListDto>> getAll() {
        return ResponseEntity.ok(workflowService.getAllWithNames());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/activos")
    public ResponseEntity<List<WorkflowEntity>> getActives(){
        return ResponseEntity.ok(workflowService.getActives());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<WorkflowEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(workflowService.getOne(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody WorkflowDto dto) throws AttributeException {
        WorkflowEntity workflow = workflowService.save(dto);
        String message = workflow.getNombre() + " ha sido guardado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody WorkflowDto dto) throws ResourceNotFoundException, AttributeException {
        WorkflowEntity workflow = workflowService.update(id, dto);
        String message = workflow.getNombre() + " ha sido actualizado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        WorkflowEntity workflow = workflowService.delete(id);
        String message = workflow.getNombre() + " ha sido eliminado";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }
}
