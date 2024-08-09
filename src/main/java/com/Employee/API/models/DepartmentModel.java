package com.Employee.API.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="department")
public class DepartmentModel {
    @Id
    private String id;
    private String departmentName;
    private String managerName;
    
    public String getDepId() {
        return id;
    }
    public void setDepId(String depId) {
        this.id = depId;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public String getManagerName() {
        return managerName;
    }
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    
}
