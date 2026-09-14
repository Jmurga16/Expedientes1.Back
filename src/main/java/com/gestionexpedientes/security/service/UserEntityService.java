package com.gestionexpedientes.security.service;

import com.gestionexpedientes.counter.service.CounterService;
import com.gestionexpedientes.global.exceptions.AttributeException;
import com.gestionexpedientes.security.dto.CreateUserDto;
import com.gestionexpedientes.security.dto.JwtTokenDto;
import com.gestionexpedientes.security.dto.LoginUserDto;
import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.security.jwt.JwtProvider;
import com.gestionexpedientes.user.entity.UserEntity;
import com.gestionexpedientes.user.repository.IUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEntityService {

    private static final int ESTADO_ACTIVO = 1;

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final CounterService counterService;

    public UserEntityService(IUserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             JwtProvider jwtProvider,
                             AuthenticationManager authenticationManager,
                             CounterService counterService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.counterService = counterService;
    }

    public UserEntity createUser(CreateUserDto dto) throws AttributeException {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new AttributeException("El correo está en uso.");
        if (userRepository.existsByDni(dto.getDni()))
            throw new AttributeException("El DNI está en uso.");

        return userRepository.save(mapUserFromDto(dto));
    }

    public JwtTokenDto login(LoginUserDto dto) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new JwtTokenDto(token);
    }

    private UserEntity mapUserFromDto(CreateUserDto dto) {
        int id = counterService.nextId("users");
        String password = passwordEncoder.encode(dto.getPassword());
        return new UserEntity(id, dto.getName(), dto.getLastname(), dto.getDni(), dto.getAddress(), dto.getEmail(), dto.getEmail(),
                password, List.of(RoleEnum.ROLE_USER), null, ESTADO_ACTIVO);
    }
}
