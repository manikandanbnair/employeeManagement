package com.Employee.API.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "manager")
public class ManagerModel {
    @Id
    private String id;
    private String name;
    @Field("department")
    private String Department;
    
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
    public String getDepartment() {
        return Department;
    }
    public void setDepartment(String department) {
        Department = department;
    }

    
}
