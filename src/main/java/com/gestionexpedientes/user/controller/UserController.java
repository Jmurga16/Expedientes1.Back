package com.gestionexpedientes.user.controller;

import com.gestionexpedientes.user.dto.UserDto;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.service.UserService;

import com.gestionexpedientes.global.dto.MessageDto;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.global.exceptions.ResourceNotFoundException;
import com.gestionexpedientes.user.dto.UserDto;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserEntity>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        return ResponseEntity.ok(userService.getOne(id));
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping
//    public ResponseEntity<MessageDto> save(@Valid @RequestBody UserDto dto) throws AttributeException {
//        User user = userService.save(dto);
//        String message = "user " + user.getName() + " have been saved";
//        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
//    }
//
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @PutMapping("/{id}")
//    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody UserDto dto) throws ResourceNotFoundException, AttributeException {
//        User user = userService.update(id, dto);
//        String message = "user " + user.getName() + " have been updated";
//        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
//    }
//
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
//        User user = userService.delete(id);
//        String message = "user " + user.getName() + " have been deleted";
//        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
//    }
}

