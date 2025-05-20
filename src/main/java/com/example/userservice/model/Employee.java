package com.example.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "employees")
public class Employee {
    @Id
    private String id;

    private String idNumber;
    private String name;
    private String joinDate;
    private String designation;
    private String email;
    private String password;
    private List<String> type;
    private String status;
    private String endDate;
    private String position;
    private String currentTimee;

    public Employee() {}


    public Employee(String id, String idNumber, String name, String joinDate, String designation, String email, String password,List <String> type, String status, String endDate, String position, String currentTimee) {
        this.id = id;
        this.idNumber = idNumber;
        this.name = name;
        this.joinDate = joinDate;
        this.designation = designation;
        this.email = email;
        this.password = password;
        this.type = type;
        this.status = status;
        this.endDate = endDate;
        this.position = position;
        this.currentTimee = currentTimee;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCurrentTimee() {
        return currentTimee;
    }

    public void setCurrentTimee(String currentTimee) {
        this.currentTimee = currentTimee;
    }

    public Employee(String idNumber, String name, String designation, String joinDate) {
        this.idNumber = idNumber;
        this.name = name;
        this.designation = designation;
        this.joinDate = joinDate;
    }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getJoinDate() { return joinDate; }
    public void setJoinDate(String joinDate) { this.joinDate = joinDate; }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", name='" + name + '\'' +
                ", joinDate='" + joinDate + '\'' +
                ", designation='" + designation + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", status='" + status + '\'' +
                ", endDate='" + endDate + '\'' +
                ", position='" + position + '\'' +
                ", currentTimee='" + currentTimee + '\'' +
                '}';
    }
}
