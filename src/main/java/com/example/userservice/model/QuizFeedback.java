package com.example.userservice.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz_feedback")
public class QuizFeedback {
    @Id
    private String id;

    private String dateTime;
    private String idNumber;
    private String className;
    private String classNumber;
    private String trainerName;
    private Integer rating;
    private String comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassNumber() {
        return classNumber;
    }

    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public QuizFeedback() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public QuizFeedback(String idNumber, String className, String classNumber, String trainerName) {
        this.idNumber=idNumber;
        this.className = className;
        this.classNumber = classNumber;
        this.trainerName = trainerName;
    }

    @Override
    public String toString() {
        return "QuizFeedback{" +
                "id='" + id + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", className='" + className + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", trainerName='" + trainerName + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}
