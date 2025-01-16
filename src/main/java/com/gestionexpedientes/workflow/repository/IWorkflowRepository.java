package com.gestionexpedientes.workflow.repository;

import com.gestionexpedientes.global.dto.BpmnDto;
import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.workflow.entity.WorkflowEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface IWorkflowRepository extends MongoRepository<WorkflowEntity, Integer> {
    boolean existsByNombre(String nombre);
    Optional<WorkflowEntity> findByNombre(String nombre);
    List<WorkflowEntity> findByEstado(int estado);
    boolean existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer Tipologia, Integer Subtipologia);
    @Query(value = "{ 'idTipoDemanda': ?0, 'idTipologia': ?1, 'idSubtipologia': ?2 }", fields = "{ '_id': 1 }")
    Optional<Integer> findIdByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer idTipologia, Integer idSubtipologia);

    @Query(value = "{ 'idTipoDemanda': ?0, 'idTipologia': ?1, 'idSubtipologia': ?2 }", fields = "{ '_id': 1, 'bpmn': 1 }")
    Optional<WorkflowEntity> findIdAndBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer idTipologia, Integer idSubtipologia);

    @Query(value = "{ 'idTipoDemanda': ?0, 'idTipologia': ?1, 'idSubtipologia': ?2 }", fields = "{ 'bpmn': 1 }")
    Optional<BpmnDto> findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer idTipologia, Integer idSubtipologia);
}
