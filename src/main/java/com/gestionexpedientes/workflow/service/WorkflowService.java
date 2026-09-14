package com.gestionexpedientes.workflow.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import com.gestionexpedientes.workflow.dto.WorkflowDto;
import com.gestionexpedientes.workflow.dto.WorkflowListDto;
import com.gestionexpedientes.workflow.entity.WorkflowEntity;
import com.gestionexpedientes.workflow.repository.IWorkflowRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkflowService {
    private final IWorkflowRepository workflowRepository;
    private final ITipologiaRepository tipologiaRepository;
    private final ISubTipologiaRepository subtipologiaRepository;
    private final ObjectMapper objectMapper;
    private final CounterService counterService;

    public WorkflowService(IWorkflowRepository workflowRepository,
                           ITipologiaRepository tipologiaRepository,
                           ISubTipologiaRepository subtipologiaRepository,
                           ObjectMapper objectMapper,
                           CounterService counterService) {
        this.workflowRepository = workflowRepository;
        this.tipologiaRepository = tipologiaRepository;
        this.subtipologiaRepository = subtipologiaRepository;
        this.objectMapper = objectMapper;
        this.counterService = counterService;
    }

    private String extractNombre(String jsonString) {
        try {
            JsonNode node = objectMapper.readTree(jsonString);
            return node.get("nombre").asText();
        } catch (Exception e) {
            return null;
        }
    }

    public List<WorkflowListDto> getAllWithNames() {
        List<WorkflowEntity> workflows = workflowRepository.findAll();
        return workflows.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }

    private WorkflowListDto mapToResponseDto(WorkflowEntity workflow) {
        WorkflowListDto dto = new WorkflowListDto();
        dto.setId(workflow.getId());
        dto.setNombre(workflow.getNombre());
        dto.setDescripcion(workflow.getDescripcion());

        dto.setTipoDemanda(
                TipoDemandaData.getTipoDemandaList().stream()
                        .filter(td -> td.getId() == workflow.getIdTipoDemanda())
                        .map(TipoDemandaData.TipoDemanda::getNombre)
                        .findFirst()
                        .orElse(null)
        );

        dto.setTipologia(tipologiaRepository.findNombreById(workflow.getIdTipologia())
                .map(this::extractNombre)
                .orElse(null));
        dto.setSubtipologia(subtipologiaRepository.findNombreById(workflow.getIdSubtipologia())
                .map(this::extractNombre)
                .orElse(null));
        dto.setEstado(workflow.getEstado());
        return dto;
    }

    private WorkflowEntity mapWorkflowFromDto(WorkflowDto dto) {
        int id = counterService.nextId("workflow");

        return new WorkflowEntity(id, dto.getNombre(), dto.getDescripcion(), dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia(), dto.getBpmn(), dto.getEstado());
    }

    public List<WorkflowEntity> getAll() {
        return workflowRepository.findAll();
    }

    public WorkflowEntity getOne(int id) throws ResourceNotFoundException {

        WorkflowEntity workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        return workflow;
    }

    public List<WorkflowEntity> getActives() {

        List<WorkflowEntity> actives = workflowRepository.findByEstado(1);

        return actives;
    }

    public boolean exists(int idTipoDemanda, int idTipologia, int idSubtipologia) {
        return workflowRepository.existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(
                idTipoDemanda, idTipologia, idSubtipologia);
    }

    public WorkflowEntity save(WorkflowDto dto) throws AttributeException {
        if (workflowRepository.existsByNombre(dto.getNombre()))
            throw new AttributeException("El registro ya existe.");

        if (workflowRepository.existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia())) {
            throw new AttributeException("Ya existe un flujo con la misma combinación de Tipo de Demanda, Tipologia y Subtipologia.");
        }

        WorkflowEntity workflow = mapWorkflowFromDto(dto);

        return workflowRepository.save(workflow);
    }

    public WorkflowEntity update(int id, WorkflowDto dto) throws ResourceNotFoundException, AttributeException {
        WorkflowEntity workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        if (workflowRepository.existsByNombre(dto.getNombre()) && workflowRepository.findByNombre(dto.getNombre()).get().getId() != id)
            throw new AttributeException("El registro ya existe");

        workflow.setNombre(dto.getNombre());
        workflow.setDescripcion(dto.getDescripcion());
        workflow.setIdTipoDemanda(dto.getIdTipoDemanda());
        workflow.setIdTipologia(dto.getIdTipologia());
        workflow.setIdSubtipologia(dto.getIdSubtipologia());
        workflow.setBpmn(dto.getBpmn());
        workflow.setEstado(dto.getEstado());

        return workflowRepository.save(workflow);
    }

    public WorkflowEntity delete(int id) throws ResourceNotFoundException {
        WorkflowEntity workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        workflow.setEstado(0);
        return workflowRepository.save(workflow);
    }


}
