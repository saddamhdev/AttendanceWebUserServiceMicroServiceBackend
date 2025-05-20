package com.example.userservice.repository;


import com.example.userservice.model.Developer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeveloperRepository extends MongoRepository<Developer, String> {
Optional<Developer> findByMenuName(String name);

}
