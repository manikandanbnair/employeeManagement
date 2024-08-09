package com.Employee.API.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Document(collection = "employee")
public class EmployeeModel {

    @Id
    private String id;
    private String name;
    private String designation;
    private String email;
    @Field("department")
    private String department;
    private String mobile;
    private String location;
    private String managerId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime dateOfJoining;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedTime;

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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getdepartment() {
        return department;
    }

    public void setdepartment(String department) {
        this.department = department;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public LocalDateTime getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(LocalDateTime dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "EmployeeModel [id=" + id + ", name=" + name + ", designation=" + designation + ", email=" + email
                + ", department=" + department + ", mobile=" + mobile + ", location=" + location + ", managerId="
                + managerId + ", dateOfJoining=" + dateOfJoining + ", createdTime=" + createdTime + ", updatedTime="
                + updatedTime + "]";
    }
}
