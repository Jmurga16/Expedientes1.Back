package com.gestionexpedientes.user.repository;

import com.gestionexpedientes.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface IUserRepository extends MongoRepository<UserEntity, Integer> {

    boolean existsByName(String name);
    Optional<UserEntity> findByName(String name);

}
