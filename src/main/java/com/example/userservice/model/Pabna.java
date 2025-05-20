package com.example.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "pabna")
public class Pabna {
    @Id
    private String id;

    private String idNumber;
    private String name;
    private String parent_idNumber;
    private String level;

    public Pabna(String idNumber, String name, String parent_idNumber, String level) {
        this.idNumber = idNumber;
        this.name = name;
        this.parent_idNumber = parent_idNumber;
        this.level = level;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_idNumber() {
        return parent_idNumber;
    }

    public void setParent_idNumber(String parent_idNumber) {
        this.parent_idNumber = parent_idNumber;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public Pabna() {}

    @Override
    public String toString() {
        return "Pabna{" +
                "id='" + id + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", name='" + name + '\'' +
                ", parent_idNumber='" + parent_idNumber + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
