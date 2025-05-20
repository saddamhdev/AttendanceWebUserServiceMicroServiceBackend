package com.example.userservice.repository;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizFeedbackRepository extends MongoRepository<QuizFeedback, String> {
    List<QuizFeedback> findByIdNumber(String studentIdNumber);
    Optional<QuizFeedback> findByIdNumberAndClassNameAndClassNumber(String studentIdNumber, String className, String classNumber);
    void deleteByClassName(String className);
}
