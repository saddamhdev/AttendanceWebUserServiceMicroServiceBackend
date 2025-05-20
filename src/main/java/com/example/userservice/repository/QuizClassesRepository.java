package com.example.userservice.repository;

import com.example.userservice.model.QuizClasses;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QuizClassesRepository extends MongoRepository<QuizClasses, String> {

    Optional<QuizClasses> findByClassName(String className);
    Optional<QuizClasses> findByClassNameAndClassNumber(String className,String classNumber);
    // add delete query by id
    void deleteById(String id);
}
