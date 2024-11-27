package com.gestionexpedientes.tipologia.service;

import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.tipologia.dto.TipologiaDto;
import com.gestionexpedientes.tipologia.entity.TipologiaEntity;
import com.gestionexpedientes.tipologia.repository.ITipologiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipologiaService {
    @Autowired
    ITipologiaRepository tipologiaRepository;
    public List<TipologiaEntity> getAll() {
        return tipologiaRepository.findAll();
    }

    public TipologiaEntity getOne(int id) throws ResourceNotFoundException {

        TipologiaEntity tipologia = tipologiaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));

        return tipologia;
    }

    public List<TipologiaEntity> getActives()  {

        List<TipologiaEntity> actives = tipologiaRepository.findByEstado(1);

        return actives;
    }

    public TipologiaEntity save(TipologiaDto dto) throws AttributeException {
        if(tipologiaRepository.existsByNombre(dto.getNombre()))
            throw new AttributeException("El registro ya existe.");

        TipologiaEntity tipologia = mapTipologiaFromDto(dto);

        return tipologiaRepository.save(tipologia);
    }

    public TipologiaEntity update(int id, TipologiaDto dto) throws ResourceNotFoundException, AttributeException {
        TipologiaEntity tipologia = tipologiaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));

        if(tipologiaRepository.existsByNombre(dto.getNombre()) && tipologiaRepository.findByNombre(dto.getNombre()).get().getId() != id)
            throw new AttributeException("El registro ya existe");

        tipologia.setNombre(dto.getNombre());
        tipologia.setDescripcion(dto.getDescripcion());
        tipologia.setEstado(dto.getEstado());

        return tipologiaRepository.save(tipologia);
    }

    public TipologiaEntity delete(int id) throws ResourceNotFoundException {
        TipologiaEntity tipologia = tipologiaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));;

        tipologia.setEstado(0);
        return tipologiaRepository.save(tipologia);
    }

    private TipologiaEntity mapTipologiaFromDto(TipologiaDto dto) {
        int id = Operations.autoIncrement(tipologiaRepository.findAll());

        return new TipologiaEntity(id, dto.getNombre(), dto.getDescripcion(), dto.getEstado());
    }
}
