package com.gestionexpedientes.demanda.repository;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IDemandaRepository extends MongoRepository<DemandaEntity, Integer> {
    boolean existsByCaratula(String caratula);
    Optional<DemandaEntity> findByCaratula(String caratula);
    List<DemandaEntity> findByEstado(int estado);
    List<DemandaEntity> findByIdUsuario(int idUsuario);
    boolean existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer Tipologia, Integer Subtipologia);
}
