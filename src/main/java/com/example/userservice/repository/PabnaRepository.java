package com.example.userservice.repository;


import com.example.userservice.model.Developer;
import com.example.userservice.model.Pabna;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PabnaRepository extends MongoRepository<Pabna, String> {

}
