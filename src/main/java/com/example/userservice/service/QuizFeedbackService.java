package com.example.userservice.service;

import com.example.userservice.model.QuizAttendance;
import com.example.userservice.model.QuizFeedback;
import com.example.userservice.model.QuizFeedbackRequest;
import com.example.userservice.repository.QuizFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuizFeedbackService {

    @Autowired
    private QuizFeedbackRepository feedbackRepository;
    public boolean updateFeedback( Integer rating, String comment,String className,String classNumber,String username,String trainerName ) {

        Optional<QuizFeedback> data=feedbackRepository.findByIdNumberAndClassNameAndClassNumber(username,className,classNumber);
        if(data.isPresent()){
            QuizFeedback attendance2=data.get();
            attendance2.setRating(rating);
            attendance2.setComment(comment);
            feedbackRepository.save(attendance2);

            return true;
        }else{
            QuizFeedback attendance2=new QuizFeedback();
            attendance2.setIdNumber(username);
            attendance2.setRating(rating);
            attendance2.setComment(comment);
            attendance2.setTrainerName(trainerName);

            attendance2.setClassName(className);
            attendance2.setClassNumber(classNumber);

            feedbackRepository.save(attendance2);
            return  true;
        }
    }

}
