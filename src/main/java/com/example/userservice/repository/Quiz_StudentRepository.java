package com.example.userservice.repository;

import com.example.userservice.model.Developer;
import com.example.userservice.model.Quiz_Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface Quiz_StudentRepository extends MongoRepository<Quiz_Student, String> {

    Optional<Quiz_Student> findByIdNumber(String idNumber);
}
