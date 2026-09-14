package com.gestionexpedientes.area.service;

import com.gestionexpedientes.area.dto.AreaDto;
import com.gestionexpedientes.area.entity.AreaEntity;
import com.gestionexpedientes.area.repository.IAreaRepository;
import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.service.AbstractCatalogService;
import org.springframework.stereotype.Service;

@Service
public class AreaService extends AbstractCatalogService<AreaEntity, AreaDto> {

    public AreaService(IAreaRepository areaRepository, CounterService counterService) {
        super(areaRepository, counterService, "area");
    }

    @Override
    protected AreaEntity nuevo(int id, AreaDto dto) {
        return new AreaEntity(id, dto.getNombre(), dto.getEstado());
    }
}
