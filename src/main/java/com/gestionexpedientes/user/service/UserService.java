package com.gestionexpedientes.user.service;

import com.gestionexpedientes.user.dto.UserDto;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.global.utils.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    IUserRepository userRepository;

    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }

    public UserEntity getOne(int id) throws ResourceNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("not found"));
    }

//    public UserEntity save(UserDto dto) throws AttributeException {
//        if(userRepository.existsByName(dto.getName()))
//            throw new AttributeException("name already in use");
//        int id = Operations.autoIncrement(userRepository.findAll());
//        UserEntity user = new UserEntity(id, dto.getName(), dto.getLastname(), dto.getStatus(),dto.getEmail(),dto.getPassword());
//        return userRepository.save(user);
//    }
//
//    public UserEntity update(int id, UserDto dto) throws ResourceNotFoundException, AttributeException {
//        UserEntity user = userRepository.findById(id)
//                .orElseThrow(()-> new ResourceNotFoundException("not found"));
//        if(userRepository.existsByName(dto.getName()) && userRepository.findByName(dto.getName()).get().getId() != id)
//            throw new AttributeException("name already in use");
//        user.setName(dto.getName());
//        user.setPrice(dto.getPrice());
//        return userRepository.save(user);
//    }
//
//    public UserEntity delete(int id) throws ResourceNotFoundException {
//        UserEntity user = userRepository.findById(id)
//                .orElseThrow(()-> new ResourceNotFoundException("not found"));;
//        userRepository.delete(user);
//        return user;
//    }
}
