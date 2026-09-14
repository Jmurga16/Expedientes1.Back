package com.gestionexpedientes.subtipologia.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.service.AbstractCatalogService;
import com.gestionexpedientes.subtipologia.dto.SubTipologiaDto;
import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubTipologiaService extends AbstractCatalogService<SubTipologiaEntity, SubTipologiaDto> {

    private final ISubTipologiaRepository subtipologiaRepository;

    public SubTipologiaService(ISubTipologiaRepository subtipologiaRepository, CounterService counterService) {
        super(subtipologiaRepository, counterService, "subtipologia");
        this.subtipologiaRepository = subtipologiaRepository;
    }

    public List<SubTipologiaEntity> getByIdTipologia(int idTipologia) {
        return subtipologiaRepository.findByIdTipologia(idTipologia);
    }

    @Override
    protected SubTipologiaEntity nuevo(int id, SubTipologiaDto dto) {
        return new SubTipologiaEntity(id, dto.getNombre(), dto.getIdTipologia(), dto.getEstado());
    }

    @Override
    protected void actualizar(SubTipologiaEntity entity, SubTipologiaDto dto) {
        entity.setIdTipologia(dto.getIdTipologia());
    }
}
