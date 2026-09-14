package com.gestionexpedientes.user.service;


import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.user.dto.UserDto;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CounterService counterService;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder, CounterService counterService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.counterService = counterService;
    }

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public UserEntity getOne(int id) throws ResourceNotFoundException {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));

        return user;
    }

    public UserEntity save(UserDto dto) throws AttributeException {
        if(dto.getPassword() == null || dto.getPassword().isBlank())
            throw new AttributeException("Contraseña es Obligatorio");
        if(userRepository.existsByEmail(dto.getEmail()))
            throw new AttributeException("El correo ya existe.");
        if(userRepository.existsByDni(dto.getDni()))
            throw new AttributeException("El DNI está en uso.");

        //Validar la existencia de cuando sea Rol "ROLE_AREA", exista un usuario con esa area y ese rol
        if (dto.getRoles() != null && dto.getRoles().contains(RoleEnum.ROLE_AREA)) {
            Integer idArea = dto.getIdArea();
            if (idArea == null) {
                throw new AttributeException("El área no puede ser nula para un rol 'Referente de Area'.");
            }
            if (userRepository.existsByIdAreaAndRoles(idArea, RoleEnum.ROLE_AREA)) {
                throw new AttributeException("Ya existe un usuario con el rol 'Referente de Área' y el área especificada.");
            }
        }

        UserEntity user = mapUserFromDto(dto);

        return userRepository.save(user);
    }

    public UserEntity update(int id, UserDto dto) throws ResourceNotFoundException, AttributeException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));

        verificarLibre(userRepository.findByEmail(dto.getEmail()), id, "El correo está en Uso");
        verificarLibre(userRepository.findByDni(dto.getDni()), id, "El DNI está en Uso");

        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setDni(dto.getDni());
        user.setAddress(dto.getAddress());

        user.setEmail(dto.getEmail());
        user.setUsername(dto.getEmail());

        if(dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(dto.getRoles());
        user.setIdArea(dto.getIdArea());

        if(dto.getStatus() != null)
            user.setStatus(dto.getStatus());

        return userRepository.save(user);
    }

    public UserEntity delete(int id) throws ResourceNotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));;
        userRepository.delete(user);
        return user;
    }

    private void verificarLibre(Optional<UserEntity> encontrado, int id, String mensaje) throws AttributeException {
        if (encontrado.filter(otro -> otro.getId() != id).isPresent())
            throw new AttributeException(mensaje);
    }

    private UserEntity mapUserFromDto(UserDto dto) {
        int id = counterService.nextId("users");
        String password = passwordEncoder.encode(dto.getPassword());
        return new UserEntity(id, dto.getName(), dto.getLastname(), dto.getDni(), dto.getAddress(), dto.getEmail(), dto.getEmail(), password, dto.getRoles(), dto.getIdArea(),dto.getStatus());
    }

}
