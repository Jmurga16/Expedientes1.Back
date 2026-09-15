package com.gestionexpedientes.tipologia.repository;

import com.gestionexpedientes.global.repository.ICatalogRepository;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ITipologiaRepository extends ICatalogRepository<TipologiaEntity> {

    @Query(value = "{ '_id': ?0 }", fields = "{ 'nombre': 1, '_id': 0 }")
    Optional<String> findNombreById(int id);
}
