package com.gestionexpedientes.security.controller;

import com.gestionexpedientes.security.service.UserEntityService;
import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.security.dto.CreateUserDto;
import com.gestionexpedientes.security.dto.JwtTokenDto;
import com.gestionexpedientes.security.dto.LoginUserDto;
import com.gestionexpedientes.security.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    UserEntityService userEntityService;


    @PostMapping("/create")
    public ResponseEntity<MessageDto> create(@Valid @RequestBody CreateUserDto dto) throws AttributeException {
        UserEntity userEntity = userEntityService.create(dto);
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, "user " + userEntity.getUsername() + " have been created"));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<MessageDto> createAdmin(@Valid @RequestBody CreateUserDto dto) throws AttributeException {
        UserEntity userEntity = userEntityService.createAdmin(dto);
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, "admin " + userEntity.getUsername() + " have been created"));
    }

    @PostMapping("/create-user")
    public ResponseEntity<MessageDto> createUser(@Valid @RequestBody CreateUserDto dto) throws AttributeException {
        UserEntity userEntity = userEntityService.createUser(dto);
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, "Usuario " + userEntity.getUsername() + " ha sido creado con éxito."));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenDto> login(@Valid @RequestBody LoginUserDto dto) throws AttributeException {
        JwtTokenDto jwtTokenDto = userEntityService.login(dto);
        return ResponseEntity.ok(jwtTokenDto);
    }
}
