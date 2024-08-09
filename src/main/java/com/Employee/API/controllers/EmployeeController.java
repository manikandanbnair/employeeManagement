package com.Employee.API.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Employee.API.helpers.ManagerChangeRequest;
import com.Employee.API.models.DepartmentModel;
import com.Employee.API.models.EmployeeModel;
import com.Employee.API.models.ManagerModel;
import com.Employee.API.response.ManagerChangeResponseDTO;
import com.Employee.API.response.ResponseDTO;
import com.Employee.API.response.ResponseMessage;
import com.Employee.API.services.EmployeeService;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // Get all employees
    @GetMapping("/employees")
    public List<EmployeeModel> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // Get all managers
    @GetMapping("/managers")
    public List<ManagerModel> getAllManagers() {
        return employeeService.getAllManagers();
    }

    //Get all departments
    @GetMapping("/departments")
    public List<DepartmentModel> getAllDepartments()
    {
        return employeeService.getAllDepartments();
    }

    @GetMapping("/managerWithYear")
    public ResponseEntity<ResponseDTO> getManagerWithExperience(
            @RequestParam(value = "managerId", required = false) String managerId,
            @RequestParam(value = "year", required = false) Integer minYearsOfExperience) {

        ResponseDTO responseDTO = employeeService.managerWithExperience(managerId, minYearsOfExperience);

        if (responseDTO != null) {
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    // Add new employee
    @PostMapping("/newEmployee")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeModel employee) {
        try {
            employeeService.addEmployee(employee);
            return new ResponseEntity<>(new ResponseMessage("Successfully created."), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessage("An error occurred while processing the request"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // change manager
    @PutMapping("/newmanager")
    public ResponseEntity<ManagerChangeResponseDTO> changeManager(@RequestBody ManagerChangeRequest request) {
        String employeeId = request.getEmployeeId();
        String newManagerId = request.getManagerId();

        ManagerChangeResponseDTO responseDTO;
        try {
            responseDTO = employeeService.changeEmployeeManager(employeeId, newManagerId);
        } catch (IllegalArgumentException e) {
            
            responseDTO = new ManagerChangeResponseDTO(e.getMessage());
        }

        return ResponseEntity.ok(responseDTO);

    }

 
    // Delete an employee
    @DeleteMapping("/oldEmployee")
    public ResponseEntity<ResponseMessage> deleteEmployee(@RequestParam  String employeeId) {
        try {
            
            ResponseMessage response = employeeService.deleteEmployee(employeeId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ResponseMessage errorResponse = new ResponseMessage();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

}
