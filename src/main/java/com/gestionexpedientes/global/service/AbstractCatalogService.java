package com.gestionexpedientes.global.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.dto.CatalogDto;
import com.gestionexpedientes.global.entity.CatalogEntity;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.repository.ICatalogRepository;

import java.util.List;

public abstract class AbstractCatalogService<E extends CatalogEntity, D extends CatalogDto> {

    protected static final int ESTADO_ACTIVO = 1;
    protected static final int ESTADO_INACTIVO = 0;

    protected final ICatalogRepository<E> repository;
    private final CounterService counterService;
    private final String coleccion;

    protected AbstractCatalogService(ICatalogRepository<E> repository, CounterService counterService, String coleccion) {
        this.repository = repository;
        this.counterService = counterService;
        this.coleccion = coleccion;
    }

    public List<E> getAll() {
        return repository.findAll();
    }

    public List<E> getActives() {
        return repository.findByEstado(ESTADO_ACTIVO);
    }

    public E getOne(int id) throws ResourceNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro no encontrado."));
    }

    public E save(D dto) throws AttributeException {
        verificarNombreLibre(dto.getNombre(), null);
        return repository.save(nuevo(counterService.nextId(coleccion), dto));
    }

    public E update(int id, D dto) throws ResourceNotFoundException, AttributeException {
        E entity = getOne(id);
        verificarNombreLibre(dto.getNombre(), id);

        entity.setNombre(dto.getNombre());
        entity.setEstado(dto.getEstado());
        actualizar(entity, dto);

        return repository.save(entity);
    }

    public E delete(int id) throws ResourceNotFoundException {
        E entity = getOne(id);
        entity.setEstado(ESTADO_INACTIVO);
        return repository.save(entity);
    }

    protected void verificarNombreLibre(String nombre, Integer idActual) throws AttributeException {
        boolean ocupado = repository.findByNombre(nombre)
                .filter(otro -> idActual == null || otro.getId() != idActual)
                .isPresent();
        if (ocupado)
            throw new AttributeException("El registro ya existe.");
    }

    protected abstract E nuevo(int id, D dto);

    protected void actualizar(E entity, D dto) {
    }
}
