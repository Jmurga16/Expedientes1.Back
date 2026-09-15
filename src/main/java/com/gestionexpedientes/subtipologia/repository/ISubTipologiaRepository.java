package com.gestionexpedientes.subtipologia.repository;

import com.gestionexpedientes.global.repository.ICatalogRepository;
import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ISubTipologiaRepository extends ICatalogRepository<SubTipologiaEntity> {

    List<SubTipologiaEntity> findByIdTipologia(int idTipologia);

    @Query(value = "{ '_id': ?0 }", fields = "{ 'nombre': 1, '_id': 0 }")
    Optional<String> findNombreById(int id);
}
