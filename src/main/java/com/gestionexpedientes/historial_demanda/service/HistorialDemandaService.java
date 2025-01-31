package com.gestionexpedientes.historial_demanda.service;

import com.gestionexpedientes.demanda.repository.IDemandaRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaFormDto;
import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaListDto;
import com.gestionexpedientes.historial_demanda.entity.HistorialDemandaEntity;
import com.gestionexpedientes.historial_demanda.repository.IHistorialDemandaRepository;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HistorialDemandaService {
    @Autowired
    IHistorialDemandaRepository historialDemandaRepository;
    @Autowired
    IDemandaRepository demandaRepository;
    @Autowired
    IUserRepository userRepository;

    public List<HistorialDemandaEntity> getAll() {
        return historialDemandaRepository.findAll();
    }

    public List<HistorialDemandaListDto> getDatatable(int idDemanda) {
        List<HistorialDemandaEntity> entity = historialDemandaRepository.findByIdDemanda(idDemanda);

        return entity.stream()
                .map(this::mapToListDto)
                .collect(Collectors.toList());
    }

    public HistorialDemandaEntity save(HistorialDemandaFormDto dto) throws Exception {
        //if (historialDemandaRepository.existsByIdUsuarioAndIdDemandaAndPasoAndEstado(dto.getIdUsuario(), dto.getIdDemanda(),dto.getPaso(),dto.getEstado()))
        //    throw new AttributeException("El registro ya existe.");

        HistorialDemandaEntity entity = mapToFormDto(dto);

        return historialDemandaRepository.save(entity);
    }

    public HistorialDemandaEntity update(int id, HistorialDemandaFormDto dto) throws ResourceNotFoundException, AttributeException {
        HistorialDemandaEntity entity = historialDemandaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));

        entity.setIdUsuario(dto.getIdUsuario());
        entity.setIdDemanda(dto.getIdDemanda());
        entity.setPaso(dto.getPaso());
        entity.setEstado(dto.getEstado());
        entity.setObservaciones(dto.getObservaciones());

        return historialDemandaRepository.save(entity);
    }

    private HistorialDemandaListDto mapToListDto(HistorialDemandaEntity historialDemanda) {
        HistorialDemandaListDto dto = new HistorialDemandaListDto();

        dto.setId(historialDemanda.getId());
        dto.setPaso(historialDemanda.getPaso());
        dto.setEstado(historialDemanda.getEstado());
        dto.setObservaciones(historialDemanda.getObservaciones());
        dto.setFecha(historialDemanda.getFecha());

        Optional<UserEntity> optionalUser = userRepository.findById(historialDemanda.getIdUsuario());
        optionalUser.ifPresent(user -> dto.setUsuario(user.getName() + " " +user.getLastname()));

        return dto;
    }
    private HistorialDemandaEntity mapToFormDto(HistorialDemandaFormDto dto) throws Exception {
        int id = Operations.autoIncrement(historialDemandaRepository.findAll());
        Date fecha = new Date();

        return new HistorialDemandaEntity(id, dto.getIdUsuario(), dto.getIdDemanda(), dto.getPaso(), dto.getEstado(), dto.getObservaciones(), fecha);
    }
}
