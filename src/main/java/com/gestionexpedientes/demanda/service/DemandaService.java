package com.gestionexpedientes.demanda.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionexpedientes.demanda.dto.DemandaRequestDto;
import com.gestionexpedientes.demanda.dto.DemandaListDto;
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
    private ObjectMapper objectMapper;

    public List<DemandaEntity> getAll() {
        return demandaRepository.findAll();
    }

    public List<DemandaListDto> getDatatable() {
        List<DemandaEntity> demandas = demandaRepository.findAll();
        return demandas.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    }


    private String extractNombre(String jsonString) {
        try {
            JsonNode node = objectMapper.readTree(jsonString);
            return node.get("nombre").asText();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractValueFromJson(String jsonString,String value) {
        try {
            JsonNode node = objectMapper.readTree(jsonString);
            return node.get(value).asText();
        } catch (Exception e) {
            return null;
        }
    }


    private DemandaListDto mapToResponseDto(DemandaEntity demanda) {
        DemandaListDto dto = new DemandaListDto();

        dto.setId(demanda.getId());
        dto.setCaratula(demanda.getCaratula());

        Optional<UserEntity> optionalUser = userRepository.findById(demanda.getIdUsuario());
        optionalUser.ifPresent(user -> dto.setDemandante(user.getName()));
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

    public DemandaEntity save(DemandaRequestDto dto) throws AttributeException {
        if (demandaRepository.existsByCaratula(dto.getCaratula()))
            throw new AttributeException("El registro ya existe.");

        //if (demandaRepository.existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia())) {
        //    throw new AttributeException("Ya existe un flujo con la misma combinación de Tipo de Demanda, Tipologia y Subtipologia.");
        //}

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

    private DemandaEntity mapTipologiaFromDto(DemandaRequestDto dto) {
        int id = Operations.autoIncrement(demandaRepository.findAll());
        Date fechaCreacion = new Date();

        String caratula = setCaratula(dto);

        return new DemandaEntity(id, dto.getIdUsuario(), caratula, dto.getIdTipoDemanda(), dto.getIdTipologia(), dto.getIdSubtipologia(), dto.getDomicilio(), fechaCreacion, dto.getEstado());
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
