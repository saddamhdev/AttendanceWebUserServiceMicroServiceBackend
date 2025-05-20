package com.example.userservice.service;
import com.example.userservice.model.QuizAttendance;
import com.example.userservice.repository.QuizAttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class QuizAttendanceService {

    @Autowired
    private QuizAttendanceRepository quizAttendanceRepository;

    public Optional<QuizAttendance> getAttendanceById(String id) {
        return quizAttendanceRepository.findById(id);
    }
    public QuizAttendance saveAttendance(QuizAttendance attendance) {
        Optional<QuizAttendance> existingAttendance = quizAttendanceRepository.findById(attendance.getId());

        if (existingAttendance.isPresent()) {
            QuizAttendance existing = existingAttendance.get();
            existing.setStatus(attendance.getStatus());
            existing.setDatetime(attendance.getDatetime());
            existing.setLateReason(attendance.getLateReason());
            if(attendance.getStatus().equals("Absent")){
                existing.setLateReason(" ");
            }
            return quizAttendanceRepository.save(existing);
        }

        // If not found, save new attendance
        return quizAttendanceRepository.save(attendance);
    }

    public QuizAttendance updateAttendance(String id, String status, String datetime, String lateReason,String idNumber) {
        Optional<QuizAttendance> existingAttendance = quizAttendanceRepository.findById(id);

        String className = existingAttendance.get().getClassName();
        String classNumber = existingAttendance.get().getClassNumber();

        Optional<QuizAttendance> data=quizAttendanceRepository.findByIdNumberAndClassNameAndClassNumber(idNumber,className,classNumber);
        if(data.isPresent()){
            QuizAttendance attendance=data.get();
            attendance.setStatus(status);
            attendance.setDatetime(datetime);
            attendance.setLateReason(lateReason);
            if(status.equals("Absent")){
                attendance.setLateReason(" ");
            }
            return quizAttendanceRepository.save(attendance);
        }else{
            QuizAttendance attendance=new QuizAttendance();
            attendance.setId(idNumber);
            attendance.setStatus(status);
            attendance.setDatetime(datetime);
            attendance.setLateReason(lateReason);
            if(status.equals("Absent")){
                attendance.setLateReason(" ");
            }
            return quizAttendanceRepository.save(attendance);
        }


    }
}
