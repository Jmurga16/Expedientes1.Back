package com.gestionexpedientes.demanda.repository;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IDemandaRepository extends MongoRepository<DemandaEntity, Integer> {
    List<DemandaEntity> findByEstado(int estado);
    Optional<DemandaEntity> findFirstByRutaImagen(String rutaImagen);
    Optional<DemandaEntity> findFirstByUrlBpmn(String urlBpmn);
}
