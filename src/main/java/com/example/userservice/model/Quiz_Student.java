package com.example.userservice.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document(collection = "StudentInfo")
public class Quiz_Student {
    @Id
    private String id;
    private String idNumber;
    private String password;

    public Quiz_Student() {
    }
    public Quiz_Student(String idNumber, String password) {
        this.idNumber = idNumber;
        this.password = password;
    }
    public String getIdNumber() {
        return idNumber;
    }
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Quiz_Student{" +
                "id='" + id + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
