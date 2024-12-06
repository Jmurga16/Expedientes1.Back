package com.gestionexpedientes.area.service;

import com.gestionexpedientes.area.dto.AreaDto;
import com.gestionexpedientes.area.entity.AreaEntity;
import com.gestionexpedientes.area.repository.IAreaRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {
    @Autowired
    IAreaRepository areaRepository;
    public List<AreaEntity> getAll() {
        return areaRepository.findAll();
    }

    public List<AreaEntity> getActives()  {

        return areaRepository.findByEstado(1);

    }

    public AreaEntity getOne(int id) throws ResourceNotFoundException {

        AreaEntity entity = areaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));

        return entity;
    }

    public AreaEntity save(AreaDto dto) throws AttributeException {
        if(areaRepository.existsByNombre(dto.getNombre()))
            throw new AttributeException("El registro ya existe.");

        AreaEntity tipologia = mapTipologiaFromDto(dto);

        return areaRepository.save(tipologia);
    }

    public AreaEntity update(int id, AreaDto dto) throws ResourceNotFoundException, AttributeException {
        AreaEntity tipologia = areaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));

        if(areaRepository.existsByNombre(dto.getNombre()) && areaRepository.findByNombre(dto.getNombre()).get().getId() != id)
            throw new AttributeException("El registro ya existe");

        tipologia.setNombre(dto.getNombre());
        tipologia.setEstado(dto.getEstado());

        return areaRepository.save(tipologia);
    }

    public AreaEntity delete(int id) throws ResourceNotFoundException {
        AreaEntity tipologia = areaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));;

        tipologia.setEstado(0);
        return areaRepository.save(tipologia);
    }

    private AreaEntity mapTipologiaFromDto(AreaDto dto) {
        int id = Operations.autoIncrement(areaRepository.findAll());

        return new AreaEntity(id, dto.getNombre(), dto.getEstado());
    }
}
