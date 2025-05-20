package com.example.userservice.repository;
import com.example.userservice.model.QuizAttendance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttendanceRepository extends MongoRepository<QuizAttendance, String> {
    // You can add custom queries if needed
  List<QuizAttendance> findByIdNumber(String studentIdNumber);
  Optional<QuizAttendance> findByIdNumberAndClassNameAndClassNumber(String studentIdNumber,String className,String classNumber);
  void deleteByClassName(String className);
}
