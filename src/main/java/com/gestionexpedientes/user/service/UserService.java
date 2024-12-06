package com.gestionexpedientes.user.service;


import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.user.dto.UserDto;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    IUserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public UserEntity getOne(int id) throws ResourceNotFoundException {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));

        return user;
    }

    public UserEntity getByEmail(String email) throws ResourceNotFoundException {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));

        return user;
    }

    public UserEntity save(UserDto dto) throws AttributeException {
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

        if(userRepository.existsByEmail(dto.getEmail()) && userRepository.findByEmail(dto.getEmail()).get().getId() != id)
            throw new AttributeException("El correo está en Uso");
        if(userRepository.existsByDni(dto.getDni()) && userRepository.findByDni(dto.getDni()).get().getId() != id)
            throw new AttributeException("El DNI está en Uso");

        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setDni(dto.getDni());
        user.setAddress(dto.getAddress());

        user.setEmail(dto.getEmail());
        user.setUsername(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(dto.getRoles());

        if(dto.getIdArea()!=null){
            user.setStatus(dto.getIdArea());
        }

        user.setStatus(dto.getStatus());

        return userRepository.save(user);
    }

    public UserEntity delete(int id) throws ResourceNotFoundException {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuario no encontrado."));;
        userRepository.delete(user);
        return user;
    }

    private UserEntity mapUserFromDto(UserDto dto) {
        int id = Operations.autoIncrement(userRepository.findAll());
        String password = passwordEncoder.encode(dto.getPassword());
        return new UserEntity(id, dto.getName(), dto.getLastname(), dto.getDni(), dto.getAddress(), dto.getEmail(), dto.getEmail(), password, dto.getRoles(), dto.getIdArea(),dto.getStatus());
    }

}
