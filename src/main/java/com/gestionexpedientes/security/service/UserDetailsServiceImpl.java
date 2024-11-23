package com.gestionexpedientes.security.service;

import com.gestionexpedientes.security.entity.UserEntity;
import com.gestionexpedientes.security.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userEntityRepository.findByUsernameOrEmail(username, username);
        if(!userEntity.isPresent())
            throw new UsernameNotFoundException("not exists");
        return UserPrincipal.build(userEntity.get());
    }
}
