package com.gestionexpedientes.security.controller;

import com.gestionexpedientes.security.service.UserEntityService;
import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.security.dto.CreateUserDto;
import com.gestionexpedientes.security.dto.JwtTokenDto;
import com.gestionexpedientes.security.dto.LoginUserDto;
import com.gestionexpedientes.user.entity.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserEntityService userEntityService;

    public AuthController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    // Registro publico: siempre ROLE_USER. Los admins se siembran (perfil seed) o los crea otro admin en /user.
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
