package com.gestionexpedientes.workflow.repository;

import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.workflow.entity.WorkflowEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWorkflowRepository extends MongoRepository<WorkflowEntity, Integer> {
    boolean existsByNombre(String nombre);
    Optional<WorkflowEntity> findByNombre(String nombre);
    List<WorkflowEntity> findByEstado(int estado);

    boolean existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer Tipologia, Integer Subtipologia);
}
