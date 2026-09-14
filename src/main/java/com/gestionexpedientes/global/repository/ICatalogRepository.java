package com.gestionexpedientes.global.repository;

import com.gestionexpedientes.global.entity.CatalogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface ICatalogRepository<E extends CatalogEntity> extends MongoRepository<E, Integer> {

    Optional<E> findByNombre(String nombre);

    List<E> findByEstado(int estado);
}
