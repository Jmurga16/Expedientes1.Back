package com.gestionexpedientes.historial_demanda.repository;

import com.gestionexpedientes.demanda.entity.DemandaEntity;
import com.gestionexpedientes.historial_demanda.entity.HistorialDemandaEntity;
import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IHistorialDemandaRepository extends MongoRepository<HistorialDemandaEntity, Integer> {

    boolean existsByIdUsuarioAndIdDemandaAndPasoAndEstado(Integer idUsuario, Integer idDemanda, String Paso, Integer Estado);
    List<HistorialDemandaEntity> findByEstado(int estado);
    List<HistorialDemandaEntity> findByIdUsuario(int idUsuario);
    List<HistorialDemandaEntity> findByIdDemanda(int idDemanda);
}
