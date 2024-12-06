package com.gestionexpedientes.user.repository;

import com.gestionexpedientes.security.enums.RoleEnum;
import com.gestionexpedientes.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface IUserRepository extends MongoRepository<UserEntity, Integer> {

    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByDni(String dni);
    Optional<UserEntity> findByDni(String dni);

    boolean existsByIdAreaAndRoles(Integer idArea, RoleEnum role);

}
