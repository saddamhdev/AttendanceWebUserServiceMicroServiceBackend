package com.example.userservice.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "quiz_attendance")
public class QuizAttendance {
    @Id
    private String id;

    private String idNumber;// MongoDB's unique identifier
    private String className;
    private String classNumber;
    private String status;
    private String lateReason;
    private String datetime;

    private String createDatetime;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLateReason() {
        return lateReason;
    }

    public void setLateReason(String lateReason) {
        this.lateReason = lateReason;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public QuizAttendance() {
    }
    public QuizAttendance(String idNumber,String className, String classNumber, String status, String datetime) {
       this.idNumber = idNumber;
        this.className = className;
        this.classNumber = classNumber;
        this.status = status;
    }

    @Override
    public String toString() {
        return "QuizAttendance{" +
                "id='" + id + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", className='" + className + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", status='" + status + '\'' +
                ", lateReason='" + lateReason + '\'' +
                ", datetime='" + datetime + '\'' +
                ", createDatetime='" + createDatetime + '\'' +
                '}';
    }
}
