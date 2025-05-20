package com.example.userservice.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "QuizResult")

public class QuizResult {

    @Id
    private String id;

    private String idNumber;
    private String className;
    private String classNumber;
    private double totalMarks;
    private double obtainMarks;
    private double merit;

    // Getters and Setters


    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

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

    public double getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(double totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getObtainMarks() {
        return obtainMarks;
    }

    public void setObtainMarks(double obtainMarks) {
        this.obtainMarks = obtainMarks;
    }

    public double getMerit() {
        return merit;
    }

    public void setMerit(double merit) {
        this.merit = merit;
    }

    public QuizResult() {
    }

    public QuizResult(String idNumber,String className, String classNumber, double totalMarks, double obtainMarks, double merit) {
        this.idNumber = idNumber;
        this.className = className;
        this.classNumber = classNumber;
        this.totalMarks = totalMarks;
        this.obtainMarks = obtainMarks;
        this.merit = merit;
    }

    @Override
    public String toString() {
        return "QuizResult{" +
                "id='" + id + '\'' +
                ", studentId='" + idNumber + '\'' +
                ", className='" + className + '\'' +
                ", classNumber='" + classNumber + '\'' +
                ", totalMarks=" + totalMarks +
                ", obtainMarks=" + obtainMarks +
                ", merit=" + merit +
                '}';
    }
}
