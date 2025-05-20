package com.example.userservice.repository;

import com.example.userservice.model.QuizLink;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizLinkRepository extends MongoRepository<QuizLink,String> {
    void deleteByClassName(String className);
}
