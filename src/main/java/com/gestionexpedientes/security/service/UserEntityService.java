package com.gestionexpedientes.security.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.security.dto.CreateUserDto;
import com.gestionexpedientes.security.dto.JwtTokenDto;
import com.gestionexpedientes.security.dto.LoginUserDto;
import com.gestionexpedientes.security.entity.UserEntity;
import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.security.jwt.JwtProvider;
import com.gestionexpedientes.security.repository.UserEntityRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final CounterService counterService;

    public UserEntityService(UserEntityRepository userEntityRepository,
                             PasswordEncoder passwordEncoder,
                             JwtProvider jwtProvider,
                             AuthenticationManager authenticationManager,
                             CounterService counterService) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.counterService = counterService;
    }

    public UserEntity createUser(CreateUserDto dto) throws AttributeException {

        if(userEntityRepository.existsByUsername(dto.getUsername()))
            throw new AttributeException("Usuario en uso.");
        if(userEntityRepository.existsByEmail(dto.getEmail()))
            throw new AttributeException("El correo está en uso.");

        List<String> roles = Arrays.asList("ROLE_USER");
        dto.setRoles(roles);
        return userEntityRepository.save(mapUserFromDto(dto));
    }

    public JwtTokenDto login(LoginUserDto dto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new JwtTokenDto(token);
    }


    // private methods
    private UserEntity mapUserFromDto(CreateUserDto dto) {
        int id = counterService.nextId("users");
        String password = passwordEncoder.encode(dto.getPassword());
        List<RoleEnum> roles =
                dto.getRoles().stream().map(rol -> RoleEnum.valueOf(rol)).collect(Collectors.toList());
        return new UserEntity(id, dto.getName(), dto.getLastname(), dto.getDni(),dto.getAddress(),dto.getEmail(), dto.getEmail(), password,roles,1);
    }
}
