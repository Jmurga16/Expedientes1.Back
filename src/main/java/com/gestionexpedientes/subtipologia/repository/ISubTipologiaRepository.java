package com.gestionexpedientes.subtipologia.repository;

import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ISubTipologiaRepository extends MongoRepository<SubTipologiaEntity, Integer> {
    boolean existsByNombre(String nombre);
    Optional<SubTipologiaEntity> findByNombre(String nombre);
    List<SubTipologiaEntity> findByEstado(int status);
    List<SubTipologiaEntity> findByIdTipologia(int idTipologia);
    @Query(value = "{ '_id': ?0 }", fields = "{ 'nombre': 1, '_id': 0 }")
    Optional<String> findNombreById(int id);

}
