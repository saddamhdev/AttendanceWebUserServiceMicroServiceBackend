package com.example.userservice.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "QuizClasses")
public class QuizClasses {

    @Id
    private String id;

    private LocalDateTime datetime;
    private String className;
    private String classNumber;
    private String trainerName;
    private String status; // optional for status updates
    public QuizClasses(){

    }

    public QuizClasses(LocalDateTime datetime, String className, String classNumber, String trainerName) {
        this.datetime = datetime;
        this.className = className;
        this.classNumber = classNumber;
        this.trainerName = trainerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
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

    @Override
    public String toString() {
        return "QuizClasses{" +
                "id='" + id + '\'' +
                ", datetime=" + datetime +
                ", className='" + className + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", trainerName='" + trainerName + '\'' +
                '}';
    }
}
