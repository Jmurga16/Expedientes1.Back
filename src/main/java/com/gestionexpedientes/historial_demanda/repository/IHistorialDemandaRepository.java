package com.gestionexpedientes.historial_demanda.repository;

import com.gestionexpedientes.historial_demanda.entity.HistorialDemandaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistorialDemandaRepository extends MongoRepository<HistorialDemandaEntity, Integer> {
    List<HistorialDemandaEntity> findByIdDemanda(int idDemanda);
}
