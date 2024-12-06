package com.gestionexpedientes.area.repository;

import com.gestionexpedientes.area.entity.AreaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAreaRepository extends MongoRepository<AreaEntity, Integer> {
    boolean existsByNombre(String nombre);
    Optional<AreaEntity> findByNombre(String nombre);
    List<AreaEntity> findByEstado(int status);
}
