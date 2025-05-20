package com.example.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "quiz_notice")
public class QuizNotice {
    @Id
    private String id;
    private String name;
    private Date datetime;
    private String text;
    private String status;

    public  QuizNotice(){

    }

    public QuizNotice(String id, String name, Date datetime, String text, String status) {
        this.id = id;
        this.name = name;
        this.datetime = datetime;
        this.text = text;
        this.status = status;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QuizNotice{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", datetime=" + datetime +
                ", text='" + text + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
