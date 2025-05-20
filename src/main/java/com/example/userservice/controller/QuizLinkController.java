package com.example.userservice.controller;

import com.example.userservice.model.QuizLink;
import com.example.userservice.repository.QuizLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizlink")
public class QuizLinkController {

    @Autowired
    private QuizLinkRepository quizlinkRepository;

    @PostMapping("/insert")
    public QuizLink insertQuizlink(@RequestBody QuizLink quizlink) {
        return quizlinkRepository.save(quizlink);
    }

    @GetMapping("/getAll")
    public List<QuizLink> getAllQuizlinks() {
        return quizlinkRepository.findAll();
    }

    @PostMapping("/indel")
    public ResponseEntity<String> deleteQuizlink(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (quizlinkRepository.existsById(id)) {
            quizlinkRepository.deleteById(id);
            return ResponseEntity.ok("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

    @PostMapping("/status")
    public Optional<QuizLink> updateStatus(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        String status = payload.get("status");
        // Add logic to update status if needed
        return Optional.empty();
    }
}
