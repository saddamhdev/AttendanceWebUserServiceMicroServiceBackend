package com.example.userservice.controller;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizClasses;
import com.example.userservice.model.QuizFeedback;
import com.example.userservice.model.QuizFeedbackRequest;
import com.example.userservice.repository.QuizClassesRepository;
import com.example.userservice.repository.QuizFeedbackRepository;
import com.example.userservice.security.JwtGenerator;
import com.example.userservice.service.QuizFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizFeedback")
public class QuizFeedbackController {

    @Autowired
    private QuizFeedbackService quizFeedbackService;

    @Autowired
    private QuizFeedbackRepository quizFeedbackRepository;

    @Autowired
    private QuizClassesRepository quizClassesRepository;

    @PostMapping("/insert")
    public ResponseEntity<QuizFeedbackRequest> insertAttendance(@RequestBody Map<String, Object> feedbackData,@RequestHeader(value = "Authorization", required = true) String token) {
        final String finalToken = token.substring(7);
        String username = JwtGenerator.extractUsername(finalToken);

        //String feedbackId = feedbackData.get("feedbackId").toString();
        Integer rating = feedbackData.containsKey("rating") ? Integer.valueOf(feedbackData.get("rating").toString()) : null;
        String comment = feedbackData.containsKey("comment") ? feedbackData.get("comment").toString() : null;
        String className = feedbackData.containsKey("className") ? feedbackData.get("className").toString() : null;
        String classNumber = feedbackData.containsKey("classNumber") ? feedbackData.get("classNumber").toString() : null;
        String trainerName = feedbackData.containsKey("trainerName") ? feedbackData.get("trainerName").toString() : null;


        boolean updated = quizFeedbackService.updateFeedback( rating, comment, className,classNumber,username,trainerName);
        return  null;
    }

    @GetMapping("/getAll")
    public List<QuizFeedback> getAllNotices(@RequestHeader(value = "Authorization", required = true) String token) {
        final String finalToken = token.substring(7);
        String username = JwtGenerator.extractUsername(finalToken);
        List<QuizFeedback> studentAttendance=quizFeedbackRepository.findByIdNumber(username);
        List<QuizClasses> classes=quizClassesRepository.findAll();

        return getAllAttendance(studentAttendance,classes);

    }
    @GetMapping("/getAllAdmin")
    public List<QuizFeedback> getAllNoticesAdmin(@RequestHeader(value = "Authorization", required = true) String token) {
        return quizFeedbackRepository.findAll();

    }
    public List<QuizFeedback> getAllAttendance(List<QuizFeedback> studentFeedback,List<QuizClasses> classes) {
        List<QuizFeedback> studentAttendance1=new ArrayList<>();


        for(QuizClasses classes1:classes) {
            if(studentFeedback.stream().noneMatch(attendance -> attendance.getClassNumber().equals(classes1.getClassNumber()) && attendance.getClassName().equals(classes1.getClassName()))) {
                QuizFeedback attendance=new QuizFeedback();
                attendance.setClassNumber(classes1.getClassNumber());
                attendance.setClassName(classes1.getClassName());
                attendance.setTrainerName(classes1.getTrainerName());
                attendance.setDateTime(classes1.getDatetime().toString());
                // System.out.println(attendance.getDateTime());
                studentAttendance1.add(attendance);
            }else {
                QuizFeedback attendance2=studentFeedback.stream().filter(attendance -> attendance.getClassNumber().equals(classes1.getClassNumber()) && attendance.getClassName().equals(classes1.getClassName())).findFirst().get();
                attendance2.setDateTime(classes1.getDatetime().toString());
                studentAttendance1.add(attendance2);
            }
        }

        return studentAttendance1;
    }

}

