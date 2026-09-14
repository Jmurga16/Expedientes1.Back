package com.gestionexpedientes.tipologia.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.service.AbstractCatalogService;
import com.gestionexpedientes.tipologia.dto.TipologiaDto;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import org.springframework.stereotype.Service;

@Service
public class TipologiaService extends AbstractCatalogService<TipologiaEntity, TipologiaDto> {

    public TipologiaService(ITipologiaRepository tipologiaRepository, CounterService counterService) {
        super(tipologiaRepository, counterService, "tipologia");
    }

    @Override
    protected TipologiaEntity nuevo(int id, TipologiaDto dto) {
        return new TipologiaEntity(id, dto.getNombre(), dto.getDescripcion(), dto.getEstado());
    }

    @Override
    protected void actualizar(TipologiaEntity entity, TipologiaDto dto) {
        entity.setDescripcion(dto.getDescripcion());
    }
}
