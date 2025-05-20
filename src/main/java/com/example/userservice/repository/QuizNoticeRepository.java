package com.example.userservice.repository;

import com.example.userservice.model.QuizNotice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QuizNoticeRepository extends MongoRepository<QuizNotice,String> {
Optional<QuizNotice> findById(String id);
}
