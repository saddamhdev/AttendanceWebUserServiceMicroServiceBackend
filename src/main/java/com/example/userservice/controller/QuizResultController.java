package com.example.userservice.controller;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizClasses;
import com.example.userservice.model.QuizResult;
import com.example.userservice.repository.QuizClassesRepository;
import com.example.userservice.repository.QuizResultRepository;
import com.example.userservice.security.JwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/quizResult")
public class QuizResultController {

    @Autowired
    private QuizResultRepository classDetailsRepository;
    @Autowired
    private QuizClassesRepository quizClassesRepository;

    // Endpoint to insert a new class detail
    @PostMapping("/insert")
    public QuizResult insertClassDetails(@RequestBody QuizResult classDetails) {
        System.out.println(classDetails.toString());
        return classDetailsRepository.save(classDetails);
    }

    // Endpoint to get all class details
    @GetMapping("/getAll")
    public List<QuizResult> getAllClassDetails(@RequestHeader(value = "Authorization", required = true) String token) {
        final String finalToken = token.substring(7);
        String username = JwtGenerator.extractUsername(finalToken);
        List<QuizResult> studentAttendance=classDetailsRepository.findByIdNumber(username);
        List<QuizClasses> classes=quizClassesRepository.findAll();
        return getAllAttendance(studentAttendance,classes);
    }
    // Endpoint to get all class details
    @GetMapping("/getAllAdmin")
    public List<QuizResult> getAllClassDetailsAdmin(@RequestHeader(value = "Authorization", required = true) String token) {
        return classDetailsRepository.findAll();
    }

    // Endpoint to delete a class detail by ID
    @PostMapping("/indel")
    public ResponseEntity<String> deleteClassDetail(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        System.out.println("Deleting class with ID: " + id);

        if (classDetailsRepository.existsById(id)) {
            classDetailsRepository.deleteById(id);
            return ResponseEntity.ok("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
        }
    }

    // Endpoint to update the merit based on Total marks and Obtained marks
    @PostMapping("/updateMerit")
    public ResponseEntity<String> updateMerit(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        Optional<QuizResult> classDetailsOpt = classDetailsRepository.findById(id);

        if (classDetailsOpt.isPresent()) {
            QuizResult classDetails = classDetailsOpt.get();
            double totalMarks = Double.parseDouble(payload.get("totalMarks"));
            double obtainedMarks = Double.parseDouble(payload.get("obtainedMarks"));

            double merit = (obtainedMarks / totalMarks) * 100;
            classDetails.setMerit(merit);
            classDetailsRepository.save(classDetails);

            return ResponseEntity.ok("Merit updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Class not found");
        }
    }

    public List<QuizResult> getAllAttendance(List<QuizResult> studentAttendance, List<QuizClasses> classes) {
        List<QuizResult> studentAttendance1=new ArrayList<>();


        for(QuizClasses classes1:classes) {
            if(studentAttendance.stream().noneMatch(attendance -> attendance.getClassNumber().equals(classes1.getClassNumber()) && attendance.getClassName().equals(classes1.getClassName()))) {
                QuizResult attendance=new QuizResult();
                attendance.setClassNumber(classes1.getClassNumber());
                attendance.setClassName(classes1.getClassName());

                studentAttendance1.add(attendance);

            }else {
                studentAttendance1.add(studentAttendance.stream().filter(attendance -> attendance.getClassNumber().equals(classes1.getClassNumber()) && attendance.getClassName().equals(classes1.getClassName())).findFirst().get());
            }
        }

        return studentAttendance1;
    }
}
