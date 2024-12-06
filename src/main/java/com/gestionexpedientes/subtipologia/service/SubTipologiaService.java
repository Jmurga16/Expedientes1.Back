package com.gestionexpedientes.subtipologia.service;

import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import com.gestionexpedientes.subtipologia.dto.SubTipologiaDto;
import com.gestionexpedientes.subtipologia.entity.SubTipologiaEntity;
import com.gestionexpedientes.subtipologia.repository.ISubTipologiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubTipologiaService {
    @Autowired
    ISubTipologiaRepository subtipologiaRepository;
    public List<SubTipologiaEntity> getAll() {
        return subtipologiaRepository.findAll();
    }

    public List<SubTipologiaEntity> getActives()  {

        return subtipologiaRepository.findByEstado(1);

    }

    public List<SubTipologiaEntity> getByIdTipologia(int idTipologia)  {

        return subtipologiaRepository.findByIdTipologia(idTipologia);
    }

    public SubTipologiaEntity getOne(int id) throws ResourceNotFoundException {

        SubTipologiaEntity tipologia = subtipologiaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));

        return tipologia;
    }

    public SubTipologiaEntity save(SubTipologiaDto dto) throws AttributeException {
        if(subtipologiaRepository.existsByNombre(dto.getNombre()))
            throw new AttributeException("El registro ya existe.");

        SubTipologiaEntity tipologia = mapTipologiaFromDto(dto);

        return subtipologiaRepository.save(tipologia);
    }

    public SubTipologiaEntity update(int id, SubTipologiaDto dto) throws ResourceNotFoundException, AttributeException {
        SubTipologiaEntity tipologia = subtipologiaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));

        if(subtipologiaRepository.existsByNombre(dto.getNombre()) && subtipologiaRepository.findByNombre(dto.getNombre()).get().getId() != id)
            throw new AttributeException("El registro ya existe");

        tipologia.setNombre(dto.getNombre());
        tipologia.setIdTipologia(dto.getIdTipologia());
        tipologia.setEstado(dto.getEstado());

        return subtipologiaRepository.save(tipologia);
    }

    public SubTipologiaEntity delete(int id) throws ResourceNotFoundException {
        SubTipologiaEntity tipologia = subtipologiaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Registro no encontrado."));;

        tipologia.setEstado(0);
        return subtipologiaRepository.save(tipologia);
    }

    private SubTipologiaEntity mapTipologiaFromDto(SubTipologiaDto dto) {
        int id = Operations.autoIncrement(subtipologiaRepository.findAll());

        return new SubTipologiaEntity(id, dto.getNombre(), dto.getIdTipologia(), dto.getEstado());
    }
}
