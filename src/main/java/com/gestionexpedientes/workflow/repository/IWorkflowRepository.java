package com.gestionexpedientes.workflow.repository;

import com.gestionexpedientes.global.dto.BpmnDto;
import com.gestionexpedientes.global.repository.ICatalogRepository;
import com.gestionexpedientes.workflow.entity.WorkflowEntity;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IWorkflowRepository extends ICatalogRepository<WorkflowEntity> {

    boolean existsByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer idTipologia, Integer idSubtipologia);

    @Query(value = "{ 'idTipoDemanda': ?0, 'idTipologia': ?1, 'idSubtipologia': ?2 }", fields = "{ 'bpmn': 1 }")
    Optional<BpmnDto> findBpmnByIdTipoDemandaAndIdTipologiaAndIdSubtipologia(Integer idTipoDemanda, Integer idTipologia, Integer idSubtipologia);
}
