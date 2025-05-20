package com.example.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "quizLink")
public class QuizLink {

    @Id
    private String id;
    private String className;
    private String classNumber;
    private String trainerName;
    private LocalDateTime datetime;
    private String link;

    // Constructors
    public QuizLink() {}

    public QuizLink(String className, String classNumber, String trainerName, LocalDateTime datetime, String link) {
        this.className = className;
        this.classNumber = classNumber;
        this.trainerName = trainerName;
        this.datetime = datetime;
        this.link = link;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
