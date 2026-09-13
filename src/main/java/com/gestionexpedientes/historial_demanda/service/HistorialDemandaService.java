package com.gestionexpedientes.historial_demanda.service;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaListDto;
import com.gestionexpedientes.historial_demanda.entity.HistorialDemandaEntity;
import com.gestionexpedientes.historial_demanda.repository.IHistorialDemandaRepository;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class HistorialDemandaService {
    private final IHistorialDemandaRepository historialDemandaRepository;
    private final IUserRepository userRepository;

    public HistorialDemandaService(IHistorialDemandaRepository historialDemandaRepository, IUserRepository userRepository) {
        this.historialDemandaRepository = historialDemandaRepository;
        this.userRepository = userRepository;
    }

    public List<HistorialDemandaListDto> getDatatable(int idDemanda) {
        List<HistorialDemandaEntity> entity = historialDemandaRepository.findByIdDemanda(idDemanda);

        return entity.stream()
                .map(this::mapToListDto)
                .collect(Collectors.toList());
    }

    public HistorialDemandaEntity registrar(DemandaEntity demanda, int idUsuario, String observaciones) {
        int id = Operations.autoIncrement(historialDemandaRepository.findAll());

        return historialDemandaRepository.save(new HistorialDemandaEntity(
                id, idUsuario, demanda.getId(), demanda.getPaso(), demanda.getEstado(), observaciones, new Date()));
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
}
