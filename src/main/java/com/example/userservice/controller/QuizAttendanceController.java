package com.example.userservice.controller;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizClasses;
import com.example.userservice.repository.QuizAttendanceRepository;
import com.example.userservice.repository.QuizClassesRepository;
import com.example.userservice.security.JwtGenerator;
import com.example.userservice.service.QuizAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/quizAttendance")
public class QuizAttendanceController {

    @Autowired
    private QuizAttendanceService quizAttendanceService;
        @Autowired
        private QuizAttendanceRepository quizAttendanceRepository;

      @Autowired
      private QuizClassesRepository quizClassesRepository;
    // Insert new attendance (POST)
    @PostMapping("/insert")
    public ResponseEntity<QuizAttendance> insertAttendance(@RequestBody QuizAttendance attendance,@RequestHeader(value = "Authorization", required = true) String token) {
        final String finalToken = token.substring(7);
        String username = JwtGenerator.extractUsername(finalToken);

        Optional<QuizClasses> inf0=quizClassesRepository.findByClassNameAndClassNumber(attendance.getClassName(),attendance.getClassNumber());
        String classGivenTime=inf0.get().getDatetime().toString();
       // System.out.println(classGivenTime+"  "+attendance.getDatetime()+"  "+attendance.getStatus( ));
        Optional<QuizAttendance> data=quizAttendanceRepository.findByIdNumberAndClassNameAndClassNumber(username,attendance.getClassName(),attendance.getClassNumber());
        if(data.isPresent()){
            QuizAttendance attendance2=data.get();
            attendance2.setStatus(checkArrivalStatus(classGivenTime,attendance.getDatetime()));
            attendance2.setDatetime(attendance.getDatetime());
            attendance2.setLateReason(attendance.getLateReason());
            attendance2.setCreateDatetime(getDhakaTimeString());

            return ResponseEntity.ok(quizAttendanceRepository.save(attendance2));
        }else{
            QuizAttendance attendance2=new QuizAttendance();
            attendance2.setIdNumber(username);
            attendance2.setStatus(checkArrivalStatus(classGivenTime,attendance.getDatetime()));

            attendance2.setDatetime(attendance.getDatetime());
            attendance2.setLateReason(attendance.getLateReason());
            attendance2.setClassName(attendance.getClassName());
            attendance2.setClassNumber(attendance.getClassNumber());
            attendance2.setCreateDatetime(getDhakaTimeString());

            return ResponseEntity.ok(quizAttendanceRepository.save(attendance2));
        }

    // return null;
    }
    @PostMapping("/insertFromAdmin")
    public ResponseEntity<QuizAttendance> insertAttendanceFromAdmin(@RequestBody QuizAttendance attendance,@RequestHeader(value = "Authorization", required = true) String token) {

        String username = attendance.getIdNumber();

        Optional<QuizClasses> inf0=quizClassesRepository.findByClassNameAndClassNumber(attendance.getClassName(),attendance.getClassNumber());
        String classGivenTime=inf0.get().getDatetime().toString();
         System.out.println(classGivenTime+"  "+attendance.getDatetime()+"  "+attendance.getStatus( ));
        Optional<QuizAttendance> data=quizAttendanceRepository.findByIdNumberAndClassNameAndClassNumber(username,attendance.getClassName(),attendance.getClassNumber());
        if(data.isPresent()){
            QuizAttendance attendance2=data.get();
            attendance2.setStatus(checkArrivalStatus(classGivenTime,attendance.getDatetime()));
            attendance2.setDatetime(attendance.getDatetime());
            attendance2.setLateReason(attendance.getLateReason());
            attendance2.setCreateDatetime(attendance.getCreateDatetime());

            return ResponseEntity.ok(quizAttendanceRepository.save(attendance2));
        }else{
            QuizAttendance attendance2=new QuizAttendance();
            attendance2.setIdNumber(username);
            attendance2.setStatus(checkArrivalStatus(classGivenTime,attendance.getDatetime()));

            attendance2.setDatetime(attendance.getDatetime());
            attendance2.setLateReason(attendance.getLateReason());
            attendance2.setClassName(attendance.getClassName());
            attendance2.setClassNumber(attendance.getClassNumber());
            attendance2.setCreateDatetime(attendance.getCreateDatetime());

            return ResponseEntity.ok(quizAttendanceRepository.save(attendance2));
        }

        // return null;
    }
    @PostMapping("/update")
    public ResponseEntity<QuizAttendance> insertAttendanceUpdate(@RequestBody QuizAttendance attendance,@RequestHeader(value = "Authorization", required = true) String token) {
        System.out.println(attendance.toString());
        Optional<QuizAttendance> data=quizAttendanceRepository.findById(attendance.getId());
         if(data.isPresent()){
             QuizAttendance attendance2=data.get();
             attendance2.setDatetime(attendance.getDatetime());
             attendance2.setLateReason(attendance.getLateReason());
             attendance2.setCreateDatetime(getDhakaTimeString());
             return ResponseEntity.ok(quizAttendanceRepository.save(attendance2));
         }
        return ResponseEntity.ok(null);

        // return null;
    }
    @PostMapping("/delete")
    public ResponseEntity<QuizAttendance> insertAttendanceDelete(@RequestBody QuizAttendance attendance,@RequestHeader(value = "Authorization", required = true) String token) {
       // System.out.println(attendance.toString());
        quizAttendanceRepository.deleteById(attendance.getId());
        return ResponseEntity.ok(null);

        // return null;
    }
    // Get attendance by ID (for testing or other purposes)
    @GetMapping("/{id}")
    public ResponseEntity<QuizAttendance> getAttendance(@PathVariable String id) {
        return quizAttendanceService.getAttendanceById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update attendance status
    @PutMapping("/update/{id}")
    public ResponseEntity<QuizAttendance> updateAttendance(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam String datetime,
            @RequestParam(required = false) String lateReason,@RequestHeader(value = "Authorization", required = true) String token) {

        final String finalToken = token.substring(7);
        String username = JwtGenerator.extractUsername(finalToken);


        try {
            QuizAttendance updatedAttendance = quizAttendanceService.updateAttendance(id, status, datetime, lateReason,username);
            return ResponseEntity.ok(updatedAttendance);  // Return the updated record
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(null);  // Handle not found case
        }
    }

    @GetMapping("/getAll")
    public List<QuizAttendance> getAllNotices(@RequestHeader(value = "Authorization", required = true) String token) {

        final String finalToken = token.substring(7);
        String username = JwtGenerator.extractUsername(finalToken);
        List<QuizAttendance> studentAttendance=quizAttendanceRepository.findByIdNumber(username);
        List<QuizClasses> classes=quizClassesRepository.findAll();

        return getAllAttendance(studentAttendance,classes);
    }
    @GetMapping("/getAllAdmin")
    public List<QuizAttendance> getAllNoticesAdmin(@RequestHeader(value = "Authorization", required = true) String token) {


        return quizAttendanceRepository.findAll();
    }

    public List<QuizAttendance> getAllAttendance(List<QuizAttendance> studentAttendance,List<QuizClasses> classes) {
         List<QuizAttendance> studentAttendance1=new ArrayList<>();


        for(QuizClasses classes1:classes) {
            if(studentAttendance.stream().noneMatch(attendance -> attendance.getClassNumber().equals(classes1.getClassNumber()) && attendance.getClassName().equals(classes1.getClassName()))) {
                QuizAttendance attendance=new QuizAttendance();
                attendance.setClassNumber(classes1.getClassNumber());
                attendance.setClassName(classes1.getClassName());
                attendance.setDatetime(classes1.getDatetime().toString());
                // Convert class datetime to LocalDateTime and add 16 hours
                LocalDateTime classTime = classes1.getDatetime();
                LocalDateTime classPlus16Hours = classTime.plusHours(3);

                // If class time + 16 hours > current time, set status as Absent
                if (LocalDateTime.now().isAfter(classPlus16Hours)) {
                    attendance.setStatus("Absent");
                }

                studentAttendance1.add(attendance);
            }else {
                QuizAttendance attendance2=studentAttendance.stream().filter(attendance -> attendance.getClassNumber().equals(classes1.getClassNumber()) && attendance.getClassName().equals(classes1.getClassName())).findFirst().get();
                attendance2.setDatetime(classes1.getDatetime().toString());
                studentAttendance1.add(attendance2);
            }
        }

        return studentAttendance1;
    }
    public static String getDhakaTimeString() {
        ZoneId dhakaZone = ZoneId.of("Asia/Dhaka");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(dhakaZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return zonedDateTime.format(formatter);
    }

    // Method to check if the person is Present or Late
    public static String checkArrivalStatus(String scheduledTime, String actualTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Parse the date-time strings into LocalDateTime objects
        LocalDateTime scheduledDateTime = LocalDateTime.parse(scheduledTime, formatter);
        LocalDateTime actualDateTime = LocalDateTime.parse(actualTime, formatter);

        // Compare the actual time with the scheduled time
        if (actualDateTime.isBefore(scheduledDateTime) || actualDateTime.equals(scheduledDateTime)) {
            return "Present"; // If actual time is on or before the scheduled time
        } else {
            return "Late"; // If actual time is after the scheduled time
        }
    }
}
