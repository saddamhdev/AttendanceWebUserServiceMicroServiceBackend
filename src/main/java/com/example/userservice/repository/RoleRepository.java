package com.example.userservice.repository;


import com.example.userservice.model.Developer;
import com.example.userservice.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
Optional<Role> findByRoleName(String name);

}
