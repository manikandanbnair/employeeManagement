package com.Employee.API.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.Employee.API.response.EmployeeResponseDTO;

import java.util.List;

@Document(collection = "managerEmployee")
public class ManagerEmployeeModel {

    @Id
    private String id;
    private String name;
    private String department;
    private List<EmployeeResponseDTO> employeeList;

    // Getters and setters
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
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<EmployeeResponseDTO> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<EmployeeResponseDTO> employeeList) {
        this.employeeList = employeeList;
    }
}
