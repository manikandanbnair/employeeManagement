package com.Employee.API;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.Employee.API.controllers.EmployeeController;
import com.Employee.API.helpers.ManagerChangeRequest;
import com.Employee.API.models.DepartmentModel;
import com.Employee.API.models.EmployeeModel;
import com.Employee.API.models.ManagerModel;
import com.Employee.API.response.ManagerChangeResponseDTO;
import com.Employee.API.response.ResponseDTO;
import com.Employee.API.response.ResponseMessage;
import com.Employee.API.services.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testGetAllEmployeesFromController() throws Exception {
        // Create a mock employee
        EmployeeModel employee = new EmployeeModel();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setDesignation("Associate");
        employee.setLocation("Delhi");
        employee.setManagerId("1001");
        employee.setMobile("1234567890");
        employee.setdepartment("sales");
        employee.setCreatedTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        employee.setUpdatedTime(LocalDateTime.of(2024, 1, 2, 10, 0));

        List<EmployeeModel> employees = Arrays.asList(employee);

        // Mock the behavior of employeeService
        when(employeeService.getAllEmployees()).thenReturn(employees);

        // Perform the request and verify
        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].designation").value("Associate"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].department").value("sales"))
                .andExpect(jsonPath("$[0].mobile").value("1234567890"))
                .andExpect(jsonPath("$[0].location").value("Delhi"))
                .andExpect(jsonPath("$[0].managerId").value("1001"));

    }

    @Test
    void testGetAllManagersFromController() throws Exception {
        // Create a mock manager
        ManagerModel manager = new ManagerModel();
        manager.setId("1");
        manager.setName("Jane Smith");
        manager.setDepartment("HR");

        List<ManagerModel> managers = Arrays.asList(manager);

        // Mock the behavior of employeeService
        when(employeeService.getAllManagers()).thenReturn(managers);

        // Perform the request and verify
        mockMvc.perform(get("/api/managers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Jane Smith"))
                .andExpect(jsonPath("$[0].department").value("HR"));
    }


    
    @Test
    void testGetAllDepartments() throws Exception {
     

        // Create a mock department
        DepartmentModel department = new DepartmentModel();
        department.setDepId("1");
        department.setDepartmentName("HR");
        department.setManagerName("1001");

        // Create a list of departments
        List<DepartmentModel> departments = Arrays.asList(department);

        // Mock the behavior of employeeService
        when(employeeService.getAllDepartments()).thenReturn(departments);

        // Perform the request and verify
        mockMvc.perform(get("/api/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].depId").value("1"))
                .andExpect(jsonPath("$[0].departmentName").value("HR"))
                .andExpect(jsonPath("$[0].managerName").value("1001"));
    }


    @Test
    void testAddEmployeeSuccess() throws Exception {
        // Create a mock employee
        EmployeeModel employee = new EmployeeModel();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setDesignation("Associate");
        employee.setLocation("Delhi");
        employee.setManagerId("1001");
        employee.setMobile("1234567890");
        employee.setdepartment("sales");
        employee.setCreatedTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        employee.setUpdatedTime(LocalDateTime.of(2024, 1, 2, 10, 0));

        // Mock the behavior of employeeService
        when(employeeService.addEmployee(employee)).thenReturn(employee);
        // Perform the request and verify
        mockMvc.perform(post("/api/newEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Successfully created."));
    }

    @Test
    void testAddEmployeeBadRequest() throws Exception {
        // Create a mock employee with invalid data
        EmployeeModel employee1 = new EmployeeModel();

        doThrow(new IllegalArgumentException(""))
                .when(employeeService).addEmployee(any(EmployeeModel.class));

        // Perform the request and verify
        mockMvc.perform(post("/api/newEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isBadRequest());
        // .andExpect(jsonPath("$.message").value("Invalid employee data"));
    }

    @Test
    void testAddEmployeeInternalServerError() throws Exception {
        // Create a mock employee
        EmployeeModel employee = new EmployeeModel();
        // Set fields for valid employee data

        // Mock the behavior of employeeService to throw a generic exceptiondoThrow(new IllegalArgumentException(""))
        doThrow(new Exception("")).when(employeeService).addEmployee(any(EmployeeModel.class));

        // Perform the request and verify
        mockMvc.perform(post("/api/newEmployee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An error occurred while processing the request"));
    }

    @Test
    void testDeleteEmployeeSuccess() throws Exception {
        // Mock the behavior of employeeService
        ResponseMessage response = new ResponseMessage("Successfully deleted.");
        when(employeeService.deleteEmployee("1")).thenReturn(response);

        // Perform the request and verify
        mockMvc.perform(delete("/api/oldEmployee")
                .param("employeeId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Successfully deleted.\"}"));
    }

    @Test
    void testDeleteEmployeeNotFound() throws Exception {
        // Mock the behavior of employeeService to throw an exception
        doThrow(new IllegalArgumentException("Employee not found.")).when(employeeService).deleteEmployee("1");

        // Perform the request and verify
        mockMvc.perform(delete("/api/oldEmployee")
                .param("employeeId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Employee not found.\"}"));
    }

    @Test
    void testChangeManagerSuccess() throws Exception {
        // Create a request and response DTO
        ManagerChangeRequest request = new ManagerChangeRequest();
        request.setEmployeeId("123");
        request.setManagerId("456");

        ManagerChangeResponseDTO responseDTO = new ManagerChangeResponseDTO("Manager changed successfully");

        // Mock the behavior of employeeService
        when(employeeService.changeEmployeeManager("123", "456")).thenReturn(responseDTO);

        // Perform the request and verify
        mockMvc.perform(put("/api/newmanager")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeId\":\"123\",\"managerId\":\"456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Manager changed successfully\"}"));
    }

    @Test
    void testChangeManagerFailure() throws Exception {
        // Create a request and response DTO
        ManagerChangeRequest request = new ManagerChangeRequest();
        request.setEmployeeId("123");
        request.setManagerId("456");

        //ManagerChangeResponseDTO responseDTO = new ManagerChangeResponseDTO("Invalid manager ID");

        // Mock the behavior of employeeService to throw an exception
        doThrow(new IllegalArgumentException("Invalid manager ID")).when(employeeService)
                .changeEmployeeManager("123", "456");

        // Perform the request and verify
        mockMvc.perform(put("/api/newmanager")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"employeeId\":\"123\",\"managerId\":\"456\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Invalid manager ID\"}"));
    }


    @Test
    void testGetManagerWithExperience_Success() throws Exception {
        // Create a mock Details object
        ResponseDTO.Details mockDetails = new ResponseDTO.Details();
        mockDetails.setAccountManager("John Doe");
        mockDetails.setDepartment("HR");
        mockDetails.setId("123");
    
        // Create a mock ResponseDTO object
        ResponseDTO mockResponse = new ResponseDTO(null);
        mockResponse.setDetails(Arrays.asList(mockDetails)); // Add the Details object to the list
    
        // Mock the behavior of the employeeService
        when(employeeService.managerWithExperience("123", 5)).thenReturn(mockResponse);
    
        // Perform the request and verify the response
        mockMvc.perform(get("/api/managerWithYear")
                .param("managerId", "123")
                .param("year", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details[0].id").value("123"))
                .andExpect(jsonPath("$.details[0].accountManager").value("John Doe"))
                .andExpect(jsonPath("$.details[0].department").value("HR"));
    }
    

    @Test
    void testGetManagerWithExperience_NotFound() throws Exception {
        when(employeeService.managerWithExperience("456", 10)).thenReturn(null);

        mockMvc.perform(get("/api/managerWithYear")
                .param("managerId", "456")
                .param("year", "10"))
                .andExpect(status().isNotFound());
    }

}
