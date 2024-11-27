package com.gestionexpedientes.tipologia.repository;

import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ITipologiaRepository extends MongoRepository<TipologiaEntity, Integer> {
    boolean existsByNombre(String nombre);
    Optional<TipologiaEntity> findByNombre(String nombre);
}
