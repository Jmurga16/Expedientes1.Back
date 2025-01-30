package com.gestionexpedientes.demanda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.dto.DemandaListDto;
import com.gestionexpedientes.file.service.FileService;
import com.gestionexpedientes.global.dto.BpmnDto;
import com.gestionexpedientes.workflow.entity.WorkflowEntity;
import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import com.gestionexpedientes.tipodemanda.data.TipoDemandaData;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.workflow.repository.IWorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Random;

@Service
public class DemandaService {
    @Autowired
    IDemandaRepository demandaRepository;
    @Autowired
    ITipologiaRepository tipologiaRepository;
    @Autowired
    ISubTipologiaRepository subtipologiaRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IWorkflowRepository workflowRepository;
    @Autowired
    FileService fileService;
    @Autowired
    private ObjectMapper objectMapper;

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

    public List<DemandaListDto> getDatatableForUser(String search, String username) {

        Optional<UserEntity> optionalUser = userRepository.findByEmail(username);

        int idUsuario = optionalUser.get().getId();

        List<DemandaEntity> demandas = demandaRepository.findByIdUsuario(idUsuario);
        //return demandas.stream().map(this::mapToResponseDto).collect(Collectors.toList());
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

    public DemandaEntity getOne(int id) throws ResourceNotFoundException {

        DemandaEntity demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        return demanda;
    }

    public List<DemandaEntity> getActives() {

        List<DemandaEntity> actives = demandaRepository.findByEstado(1);

        return actives;
    }

    public DemandaEntity save(DemandaRequestDto dto) throws Exception {
        if (demandaRepository.existsByCaratula(dto.getCaratula()))
            throw new AttributeException("El registro ya existe.");

        DemandaEntity demanda = mapTipologiaFromDto(dto);

        return demandaRepository.save(demanda);
    }

    public DemandaEntity update(int id, DemandaRequestDto dto) throws ResourceNotFoundException, AttributeException {
        DemandaEntity demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        if (demandaRepository.existsByCaratula(dto.getCaratula()) && demandaRepository.findByCaratula(dto.getCaratula()).get().getId() != id)
            throw new AttributeException("El registro ya existe");

        demanda.setIdUsuario(dto.getIdUsuario());
        demanda.setCaratula(dto.getCaratula());
        demanda.setIdTipoDemanda(dto.getIdTipoDemanda());
        demanda.setIdTipologia(dto.getIdTipologia());
        demanda.setIdSubtipologia(dto.getIdSubtipologia());
        demanda.setDomicilio(dto.getDomicilio());
        demanda.setRutaImagen(dto.getRutaImagen());
        demanda.setInformacionAdicional(dto.getInformacionAdicional());
        demanda.setPaso(dto.getPaso());
        demanda.setUrlBpmn(dto.getUrlBpmn());
        demanda.setEstado(dto.getEstado());

        return demandaRepository.save(demanda);
    }

    public DemandaEntity delete(int id) throws ResourceNotFoundException {
        DemandaEntity demanda = demandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));
        ;

        demanda.setEstado(0);
        return demandaRepository.save(demanda);
    }

    private DemandaEntity mapTipologiaFromDto(DemandaRequestDto dto) throws Exception {
        int id = Operations.autoIncrement(demandaRepository.findAll());
        Date fechaCreacion = new Date();

        String caratula = setCaratula(dto);

        // Buscar idWorkflow y bpmn


        String urlBPMN = workflowRepository.findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(
                        dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia()
                ).map(BpmnDto::getBpmn)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un Workflow con los valores proporcionados"));


        String newNameBpmn = "demanda" + id + ".bpmn";
        String container = "demanda-bpmn";

        String bpmnDemanda = fileService.copyFileWithNewName(urlBPMN, container, newNameBpmn);

        return new DemandaEntity(id, dto.getIdUsuario(), caratula, dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia(), dto.getDomicilio(), dto.getRutaImagen(), dto.getInformacionAdicional(), dto.getPaso(), bpmnDemanda, fechaCreacion, dto.getEstado());
    }

    private String setCaratula(DemandaRequestDto dto) {
        String caratula = "";

        int idTipologia = dto.getIdTipologia();
        String codigoTipologia = String.format("%03d", idTipologia);

        String tipoDemanda = TipoDemandaData.getTipoDemandaList().stream()
                .filter(td -> td.getId() == dto.getIdTipoDemanda())
                .map(TipoDemandaData.TipoDemanda::getCodigo)
                .findFirst()
                .orElse(null);

        SimpleDateFormat formato = new SimpleDateFormat("yyMMdd");

        String fechaCreacion = formato.format(new Date());

        Random random = new Random();

        int aleatorio = random.nextInt(101);
        String numeral = String.format("%03d", aleatorio);

        caratula = codigoTipologia + "-" + tipoDemanda + "-" + fechaCreacion + "-" + numeral;

        return caratula;
    }
}
