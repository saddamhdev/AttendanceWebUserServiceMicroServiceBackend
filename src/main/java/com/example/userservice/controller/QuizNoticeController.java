package com.example.userservice.controller;

import com.example.userservice.model.QuizNotice;
import com.example.userservice.repository.QuizNoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizNotice")
public class QuizNoticeController {

    @Autowired
    private QuizNoticeRepository noticeRepo;

    @PostMapping("/insert")
    public ResponseEntity<?> save(@RequestBody QuizNotice notice) {
       // notice.setDatetime(new Date());
        QuizNotice saved = noticeRepo.save(notice);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(noticeRepo.findAll());
    }

    @PostMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, String> body) {
        try {
            String id = body.get("id");
            String status = body.get("status");

            Optional<QuizNotice> notice = noticeRepo.findById(id);
            if (notice.isPresent()) {
                QuizNotice data = notice.get();
                data.setStatus(status);
                QuizNotice updated = noticeRepo.save(data);
                return ResponseEntity.ok(updated);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notice not found with ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating status: " + e.getMessage());
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteQuizNotice(@RequestBody Map<String, String> body) {
        try {
            String id = body.get("id");
            if (id == null) {
                return ResponseEntity.badRequest().body("❌ Missing ID in request body.");
            }

            noticeRepo.deleteById(id);
            return ResponseEntity.ok("✅ Quiz notice deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to delete quiz notice.");
        }
    }
}
