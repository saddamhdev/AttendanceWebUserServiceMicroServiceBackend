package com.example.userservice.repository;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface QuizResultRepository extends MongoRepository<QuizResult, String> {
    List<QuizResult> findByIdNumber(String studentIdNumber);
    void deleteByClassName(String className);

}

