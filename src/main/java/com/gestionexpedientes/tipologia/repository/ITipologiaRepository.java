package com.gestionexpedientes.tipologia.repository;

import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITipologiaRepository extends MongoRepository<TipologiaEntity, Integer> {
    boolean existsByNombre(String nombre);
    Optional<TipologiaEntity> findByNombre(String nombre);
    List<TipologiaEntity> findByEstado(int estado);

    @Query(value = "{ '_id': ?0 }", fields = "{ 'nombre': 1, '_id': 0 }")
    Optional<String> findNombreById(int id);

    @Query(value = "{ '_id': ?0 }", fields = "{ 'descripcion': 1, '_id': 0 }")
    Optional<String> findDescripcionById(int id);
}
