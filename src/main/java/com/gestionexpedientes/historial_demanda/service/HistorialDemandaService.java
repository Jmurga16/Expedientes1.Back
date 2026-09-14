package com.gestionexpedientes.historial_demanda.service;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.historial_demanda.dto.HistorialDemandaListDto;
import com.gestionexpedientes.historial_demanda.entity.HistorialDemandaEntity;
import com.gestionexpedientes.historial_demanda.repository.IHistorialDemandaRepository;
import com.gestionexpedientes.user.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class HistorialDemandaService {
    private final IHistorialDemandaRepository historialDemandaRepository;
    private final IUserRepository userRepository;
    private final CounterService counterService;

    public HistorialDemandaService(IHistorialDemandaRepository historialDemandaRepository, IUserRepository userRepository,
                                   CounterService counterService) {
        this.historialDemandaRepository = historialDemandaRepository;
        this.userRepository = userRepository;
        this.counterService = counterService;
    }

    public List<HistorialDemandaListDto> getDatatable(int idDemanda) {
        List<HistorialDemandaEntity> historial = historialDemandaRepository.findByIdDemanda(idDemanda);

        List<Integer> idsUsuario = historial.stream().map(HistorialDemandaEntity::getIdUsuario).distinct().collect(Collectors.toList());
        Map<Integer, String> nombres = new HashMap<>();
        userRepository.findAllById(idsUsuario).forEach(user -> nombres.put(user.getId(), user.getName() + " " + user.getLastname()));

        return historial.stream()
                .map(item -> mapToListDto(item, nombres.get(item.getIdUsuario())))
                .collect(Collectors.toList());
    }

    public HistorialDemandaEntity registrar(DemandaEntity demanda, int idUsuario, String observaciones) {
        int id = counterService.nextId("historial_demanda");

        return historialDemandaRepository.save(new HistorialDemandaEntity(
                id, idUsuario, demanda.getId(), demanda.getPaso(), demanda.getEstado(), observaciones, new Date()));
    }

    private HistorialDemandaListDto mapToListDto(HistorialDemandaEntity historialDemanda, String usuario) {
        HistorialDemandaListDto dto = new HistorialDemandaListDto();

        dto.setId(historialDemanda.getId());
        dto.setPaso(historialDemanda.getPaso());
        dto.setEstado(historialDemanda.getEstado());
        dto.setObservaciones(historialDemanda.getObservaciones());
        dto.setFecha(historialDemanda.getFecha());
        dto.setUsuario(usuario);

        return dto;
    }
}
