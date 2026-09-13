package com.gestionexpedientes.demanda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.dto.DemandaListDto;
import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.dto.BpmnDto;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.WorkflowNotConfiguredException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.historial_demanda.service.HistorialDemandaService;
import com.gestionexpedientes.security.service.UserPrincipal;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.workflow.repository.IWorkflowRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandaService {

    private static final String PASO_INICIAL = "Inicio";
    private static final int ESTADO_RECEPTADA = 1;
    private static final String WORKFLOW_NO_CONFIGURADO =
            "No hay un flujo de trabajo definido para esa combinación de Tipo de Demanda, Tipología y Subtipología.";

    private final IDemandaRepository demandaRepository;
    private final ITipologiaRepository tipologiaRepository;
    private final ISubTipologiaRepository subtipologiaRepository;
    private final IUserRepository userRepository;
    private final IWorkflowRepository workflowRepository;
    private final FileService fileService;
    private final DemandaAccessService demandaAccessService;
    private final HistorialDemandaService historialDemandaService;
    private final CounterService counterService;
    private final ObjectMapper objectMapper;

    public DemandaService(IDemandaRepository demandaRepository,
                          ITipologiaRepository tipologiaRepository,
                          ISubTipologiaRepository subtipologiaRepository,
                          IUserRepository userRepository,
                          IWorkflowRepository workflowRepository,
                          FileService fileService,
                          DemandaAccessService demandaAccessService,
                          HistorialDemandaService historialDemandaService,
                          CounterService counterService,
                          ObjectMapper objectMapper) {
        this.demandaRepository = demandaRepository;
        this.tipologiaRepository = tipologiaRepository;
        this.subtipologiaRepository = subtipologiaRepository;
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.fileService = fileService;
        this.demandaAccessService = demandaAccessService;
        this.historialDemandaService = historialDemandaService;
        this.counterService = counterService;
        this.objectMapper = objectMapper;
    }

    public List<DemandaEntity> getAll() {
        return demandaRepository.findAll();
    }

    public List<DemandaListDto> getDatatable(String search) {
        List<DemandaEntity> demandas = demandaRepository.findAll();

        return demandas.stream()
                .filter(demanda -> matchesSearch(demanda, search))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<DemandaListDto> getDatatableForUser(String search, UserPrincipal user) {

        List<DemandaEntity> demandas = user.isAreaStaff()
                ? demandaRepository.findAll().stream().filter(demanda -> canAccessQuietly(demanda, user)).collect(Collectors.toList())
                : demandaRepository.findByIdUsuario(user.getId());

        return demandas.stream()
                .filter(demanda -> matchesSearch(demanda, search))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private boolean matchesSearch(DemandaEntity demanda, String search) {
        if (search == null || search.isEmpty()) {
            return true;
        }

        String lowerSearch = search.toLowerCase();

        // Filtrar por los campos de texto de la demanda
        boolean matchesStringFields = (demanda.getCaratula() != null && demanda.getCaratula().toLowerCase().contains(lowerSearch)) ||
                (demanda.getDomicilio() != null && demanda.getDomicilio().toLowerCase().contains(lowerSearch)) ||
                (demanda.getRutaImagen() != null && demanda.getRutaImagen().toLowerCase().contains(lowerSearch)) ||
                (demanda.getInformacionAdicional() != null && demanda.getInformacionAdicional().toLowerCase().contains(lowerSearch)) ||
                (demanda.getPaso() != null && demanda.getPaso().toLowerCase().contains(lowerSearch));

        // Filtrar por el estado de la demanda
        boolean matchesEstado = Integer.toString(demanda.getEstado()).contains(lowerSearch);

        // Filtrar por el DNI del usuario asociado a la demanda
        boolean matchesDni = userRepository.findById(demanda.getIdUsuario())
                .map(user -> user.getDni().toLowerCase().contains(lowerSearch))
                .orElse(false);

        // Filtrar por el nombre o apellido del usuario asociado a la demanda
        boolean matchesUsuario = userRepository.findById(demanda.getIdUsuario())
                .map(user -> (user.getName().toLowerCase().contains(lowerSearch) || user.getLastname().toLowerCase().contains(lowerSearch)))
                .orElse(false);

        return matchesStringFields || matchesEstado || matchesDni || matchesUsuario;
    }

    private String extractNombre(String jsonString) {
        try {
            JsonNode node = objectMapper.readTree(jsonString);
            return node.get("nombre").asText();
        } catch (Exception e) {
            return null;
        }
    }

    private DemandaListDto mapToResponseDto(DemandaEntity demanda) {
        DemandaListDto dto = new DemandaListDto();

        dto.setId(demanda.getId());
        dto.setCaratula(demanda.getCaratula());
        dto.setInformacionAdicional(demanda.getInformacionAdicional());
        dto.setPaso(demanda.getPaso());

        Optional<UserEntity> optionalUser = userRepository.findById(demanda.getIdUsuario());
        optionalUser.ifPresent(user -> dto.setDemandante(user.getName() + " " +user.getLastname()));
        optionalUser.ifPresent(user -> dto.setDni(user.getDni()));

        Optional<TipologiaEntity> optionalTipologia = tipologiaRepository.findById(demanda.getIdTipologia());
        optionalTipologia.ifPresent(element -> dto.setDescripcion(element.getDescripcion()));

        dto.setTipoDemanda(
                TipoDemandaData.getTipoDemandaList().stream()
                        .filter(td -> td.getId() == demanda.getIdTipoDemanda())
                        .map(TipoDemandaData.TipoDemanda::getCodigo)
                        .findFirst()
                        .orElse(null)
        );

        dto.setTipologia(tipologiaRepository.findNombreById(demanda.getIdTipologia())
                .map(this::extractNombre)
                .orElse(null));
        dto.setSubtipologia(subtipologiaRepository.findNombreById(demanda.getIdSubtipologia())
                .map(this::extractNombre)
                .orElse(null));
        dto.setEstado(demanda.getEstado());
        return dto;
    }

    public DemandaEntity getOne(int id, UserPrincipal user) throws ResourceNotFoundException {

        DemandaEntity demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        demandaAccessService.checkAccess(demanda, user);
        return demanda;
    }

    public List<DemandaEntity> getActives() {

        List<DemandaEntity> actives = demandaRepository.findByEstado(1);

        return actives;
    }

    public DemandaEntity save(DemandaRequestDto dto, UserPrincipal user) throws Exception {
        DemandaEntity demanda = demandaRepository.save(mapTipologiaFromDto(dto, user));

        historialDemandaService.registrar(demanda, user.getId(), null);
        return demanda;
    }

    public DemandaEntity update(int id, DemandaRequestDto dto, UserPrincipal user) throws ResourceNotFoundException {
        DemandaEntity demanda = getOne(id, user);

        demanda.setIdTipoDemanda(dto.getIdTipoDemanda());
        demanda.setIdTipologia(dto.getIdTipologia());
        demanda.setIdSubtipologia(dto.getIdSubtipologia());
        demanda.setDomicilio(dto.getDomicilio());
        demanda.setRutaImagen(dto.getRutaImagen());
        demanda.setInformacionAdicional(dto.getInformacionAdicional());

        if (demandaAccessService.canAdvance(demanda, user)) {
            demanda.setPaso(dto.getPaso());
            demanda.setEstado(dto.getEstado());
        }

        DemandaEntity saved = demandaRepository.save(demanda);
        historialDemandaService.registrar(saved, user.getId(), dto.getObservaciones());
        return saved;
    }

    public DemandaEntity delete(int id, UserPrincipal user) throws ResourceNotFoundException {
        DemandaEntity demanda = getOne(id, user);

        demanda.setEstado(0);
        return demandaRepository.save(demanda);
    }

    private boolean canAccessQuietly(DemandaEntity demanda, UserPrincipal user) {
        try {
            return demandaAccessService.canAccess(demanda, user);
        } catch (RuntimeException e) {
            return false;
        }
    }

    private DemandaEntity mapTipologiaFromDto(DemandaRequestDto dto, UserPrincipal user) throws Exception {
        int id = Operations.autoIncrement(demandaRepository.findAll());
        Date fechaCreacion = new Date();

        String caratula = setCaratula(dto);

        String urlBPMN = workflowRepository.findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(
                        dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia()
                ).map(BpmnDto::getBpmn)
                .orElseThrow(() -> new WorkflowNotConfiguredException(WORKFLOW_NO_CONFIGURADO));


        String newNameBpmn = "demanda" + id + ".bpmn";
        String container = "demanda-bpmn";

        String bpmnDemanda = fileService.copyFileWithNewName(urlBPMN, container, newNameBpmn);

        return new DemandaEntity(id, user.getId(), caratula, dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia(), dto.getDomicilio(), dto.getRutaImagen(), dto.getInformacionAdicional(), PASO_INICIAL, bpmnDemanda, fechaCreacion, ESTADO_RECEPTADA);
    }

    private String setCaratula(DemandaRequestDto dto) throws AttributeException {
        String codigoTipologia = String.format("%03d", dto.getIdTipologia());

        String tipoDemanda = TipoDemandaData.getTipoDemandaList().stream()
                .filter(td -> td.getId() == dto.getIdTipoDemanda())
                .map(TipoDemandaData.TipoDemanda::getCodigo)
                .findFirst()
                .orElseThrow(() -> new AttributeException("El tipo de demanda no existe."));

        String anio = new SimpleDateFormat("yyyy").format(new Date());

        long secuencia = counterService.next(anio + "-" + codigoTipologia + "-" + tipoDemanda);

        return codigoTipologia + "-" + tipoDemanda + "-" + anio + "-" + String.format("%05d", secuencia);
    }
}
