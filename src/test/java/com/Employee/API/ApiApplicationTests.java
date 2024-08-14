package com.Employee.API;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.Employee.API.controllers.EmployeeController;
//import com.Employee.API.helpers.EmailValidator;
import com.Employee.API.models.DepartmentModel;
import com.Employee.API.models.EmployeeModel;
import com.Employee.API.models.ManagerEmployeeModel;
import com.Employee.API.models.ManagerModel;
import com.Employee.API.repositories.DepartmentRepository;
import com.Employee.API.repositories.EmployeeRepository;
import com.Employee.API.repositories.ManagerEmployeeRepository;
import com.Employee.API.repositories.ManagerRepository;
import com.Employee.API.response.EmployeeResponseDTO;
import com.Employee.API.response.ManagerChangeResponseDTO;
import com.Employee.API.response.ResponseDTO;
import com.Employee.API.response.ResponseMessage;
import com.Employee.API.services.EmployeeService;


@SpringBootTest
class ApiApplicationTests {

	@Mock
	private EmployeeRepository employeeRepository;

	@Mock
	private ManagerRepository managerRepository;

	@Mock
	private ManagerEmployeeRepository managerEmployeeRepository;

	@Mock
	private DepartmentRepository departmentRepository;

	@InjectMocks
	private EmployeeService employeeService;

	@InjectMocks
	private EmployeeController employeeController;

	@Mock
	private EmployeeModel employee;

	@Mock
	private ManagerEmployeeModel managerEmployee;

    @Mock
	private DepartmentModel department;



	private MockMvc mockMvc;

	@BeforeEach

	void setUp() {
		


		MockitoAnnotations.openMocks(this);


	    department = new DepartmentModel();
		department.setDepId("d1");
		department.setDepartmentName("sales");
		department.setManagerName(null);
		// Initialize EmployeeModel
		employee = new EmployeeModel();
		employee.setId("1");
		employee.setName("John Doe");
		employee.setEmail("john.doe@example.com");
		employee.setDesignation("Associate");
		employee.setLocation("Delhi");
		employee.setManagerId("1001"); // This should be a valid ID in mocks
		employee.setMobile("1234567890");
		employee.setdepartment("sales");
		employee.setCreatedTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        employee.setUpdatedTime(LocalDateTime.of(2024, 1, 2, 10, 0));

		// Initialize ManagerEmployeeModel
		managerEmployee = new ManagerEmployeeModel();
		managerEmployee.setId("1001");
		managerEmployee.setName("Alice Johnson"); // Ensure consistency
		managerEmployee.setDepartment("Sales");
		managerEmployee.setEmployeeList(new ArrayList<>());

		// Mock repository behavior
		when(employeeRepository.existsById("1001")).thenReturn(true); 
		when(employeeRepository.existsById("1")).thenReturn(true); 
		when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(managerEmployee));

		mockMvc = MockMvcBuilders.standaloneSetup(employeeController).build();
	
	}

	@Test
	void testAddEmployee() throws Exception {
        // Prepare mock behavior
        when(employeeRepository.existsById("1")).thenReturn(false);  // Employee ID should be unique
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(false);  // Email should be unique
        when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(managerEmployee));  // Manager exists
        when(employeeRepository.save(employee)).thenReturn(employee);  // Save operation should return the employee
        EmployeeModel savedEmployee = employeeService.addEmployee(employee);

        // Verify results
        assertNotNull(savedEmployee, "Saved employee should not be null");
        assertEquals("John Doe", savedEmployee.getName(), "Employee name should match");
        assertEquals("john.doe@example.com", savedEmployee.getEmail(), "Employee email should match");
    }

	@Test
	void testAddEmployeeWithExistingID() throws Exception {

		employee = new EmployeeModel();
		employee.setId("1001");
		employee.setName("John Doe");
		employee.setEmail("john.doe@example.com");
		employee.setDesignation("Associate");
		employee.setLocation("Delhi");
		employee.setManagerId("1001");
		employee.setMobile("1234567890");
		employee.setdepartment("sales");
		employee.setCreatedTime(LocalDateTime.now());
		employee.setUpdatedTime(LocalDateTime.now());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			employeeService.addEmployee(employee);
		});
		assertEquals("Employee with id 1001 already exists.", exception.getMessage());

	}

	@Test
	void testAddEmployeeWithInvalidMail() throws Exception {

		employee = new EmployeeModel();
		employee.setId("1002");
		employee.setName("John Doe");
		employee.setEmail("john.doexample.com");
		employee.setDesignation("Associate");
		employee.setLocation("Delhi");
		employee.setManagerId("1001");
		employee.setMobile("1234567890");
		employee.setdepartment("sales");
		employee.setCreatedTime(LocalDateTime.now());
		employee.setUpdatedTime(LocalDateTime.now());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			employeeService.addEmployee(employee);
		});
		assertEquals("Invalid email format", exception.getMessage());

	}
	@Test
void testAddEmployeeWithExistingEmail() throws Exception {
    // Setup the test data
    EmployeeModel employee = new EmployeeModel();
    employee.setId("1002");
    employee.setName("John Doe");
    employee.setEmail("john.doe@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Delhi");
    employee.setManagerId("1001");
    employee.setMobile("1234567890");
    employee.setdepartment("sales");
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Mock the behavior of employeeRepository to return true for existing email
    when(employeeRepository.existsByEmail(employee.getEmail())).thenReturn(true);

    // Execute and verify
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.addEmployee(employee);
    });
    assertEquals("Employee with email already exists.", exception.getMessage());
}


	@Test
	void testAddEmployeeWithInvalidDesignation() throws Exception {

		employee = new EmployeeModel();
		employee.setId("1002");
		employee.setName("John Doe");
		employee.setEmail("john@example.com");
		employee.setDesignation("Engineer");
		employee.setLocation("Delhi");
		employee.setManagerId("1001");
		employee.setMobile("1234567890");
		employee.setdepartment("sales");
		employee.setCreatedTime(LocalDateTime.now());
		employee.setUpdatedTime(LocalDateTime.now());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			employeeService.addEmployee(employee);
		});
		assertEquals("Designation can only be Account Manager or associate", exception.getMessage());

	}

	@Test
	void testAddEmployeeWithInvalidDepartment() {
		employee = new EmployeeModel();
		employee.setId("1002");
		employee.setName("John Doe");
		employee.setEmail("john@example.com");
		employee.setDesignation("Associate");
		employee.setLocation("Delhi");
		employee.setManagerId("1001");
		employee.setMobile("1234567890");
		employee.setdepartment("invalidDepartment"); // Invalid department
		employee.setCreatedTime(LocalDateTime.now());
		employee.setUpdatedTime(LocalDateTime.now());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			employeeService.addEmployee(employee);
		});
		assertEquals("Invalid department. Must be one of: sales, delivery, QA, engineering, BA",
				exception.getMessage());
	}

 
	@Test
	void testAddEmployeeWithInvalidMobileNumber() {
		employee = new EmployeeModel();
		employee.setId("1002");
		employee.setName("John Doe");
		employee.setEmail("john@example.com");
		employee.setDesignation("Associate");
		employee.setLocation("Delhi");
		employee.setManagerId("1001");
		employee.setMobile("12345"); // Invalid mobile number
		employee.setdepartment("sales");
		employee.setCreatedTime(LocalDateTime.now());
		employee.setUpdatedTime(LocalDateTime.now());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			employeeService.addEmployee(employee);
		});
		assertEquals("Invalid mobile number. It must be a 10-digit number", exception.getMessage());
	}
   

@Test
void testAddEmployeeWithVariousInvalidMobileNumbers() {
    // Invalid mobile number (shorter than 10 digits)
    EmployeeModel shortMobile = new EmployeeModel();
    shortMobile.setId("1003");
    shortMobile.setName("Jane Doe");
    shortMobile.setEmail("jane@example.com");
    shortMobile.setDesignation("Associate");
    shortMobile.setLocation("Delhi");
    shortMobile.setManagerId("1001");
    shortMobile.setMobile("12345"); // Short mobile number
    shortMobile.setdepartment("sales");
    shortMobile.setCreatedTime(LocalDateTime.now());
    shortMobile.setUpdatedTime(LocalDateTime.now());

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.addEmployee(shortMobile);
    });
    assertEquals("Invalid mobile number. It must be a 10-digit number", exception.getMessage());

    // Invalid mobile number (non-numeric)
    EmployeeModel nonNumericMobile = new EmployeeModel();
    nonNumericMobile.setId("1004");
    nonNumericMobile.setName("Alice Smith");
    nonNumericMobile.setEmail("alice@example.com");
    nonNumericMobile.setDesignation("Associate");
    nonNumericMobile.setLocation("Delhi");
    nonNumericMobile.setManagerId("1001");
    nonNumericMobile.setMobile("123456789x"); // Non-numeric mobile number
    nonNumericMobile.setdepartment("sales");
    nonNumericMobile.setCreatedTime(LocalDateTime.now());
    nonNumericMobile.setUpdatedTime(LocalDateTime.now());

    exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.addEmployee(nonNumericMobile);
    });
    assertEquals("Invalid mobile number. It must be a 10-digit number", exception.getMessage());
}


@Test
void testAddEmployeeInEngineering() throws Exception {
    // Create a mock EmployeeModel
    EmployeeModel employee = new EmployeeModel();
    employee.setId("1");
    employee.setName("John Doe");
    employee.setEmail("john.doe@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Delhi");
    employee.setManagerId("1001");
    employee.setMobile("1234567890");
    employee.setdepartment("Engineering");
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Create a mock ManagerEmployeeModel
    ManagerEmployeeModel managerEmployee = new ManagerEmployeeModel();
    managerEmployee.setId("1001");
    managerEmployee.setDepartment("Engineering");
    managerEmployee.setEmployeeList(new ArrayList<>());

    // Prepare mock behavior
    when(employeeRepository.existsById("1")).thenReturn(false);  // Employee ID should be unique
    when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(false);  // Email should be unique
    when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(managerEmployee));  // Manager exists
    when(employeeRepository.save(employee)).thenReturn(employee);  // Save operation should return the employee

    // Call the method to be tested
    EmployeeModel savedEmployee = employeeService.addEmployee(employee);

    // Verify results
    assertNotNull(savedEmployee, "Saved employee should not be null");
    assertEquals("John Doe", savedEmployee.getName(), "Employee name should match");
    assertEquals("john.doe@example.com", savedEmployee.getEmail(), "Employee email should match");

    // Verify interactions with the mocked repositories
    verify(employeeRepository).existsById("1");
    verify(employeeRepository).existsByEmail("john.doe@example.com");
    verify(managerEmployeeRepository).findById("1001");
    verify(employeeRepository).save(employee);
}

@Test
void testAddEmployeeInBA() throws Exception {
    // Create a mock EmployeeModel
    EmployeeModel employee = new EmployeeModel();
    employee.setId("2");
    employee.setName("Jane Smith");
    employee.setEmail("jane.smith@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Mumbai");
    employee.setManagerId("1002");
    employee.setMobile("9876543210");
    employee.setdepartment("BA");
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Create a mock ManagerEmployeeModel
    ManagerEmployeeModel managerEmployee = new ManagerEmployeeModel();
    managerEmployee.setId("1002");
    managerEmployee.setDepartment("BA");
    managerEmployee.setEmployeeList(new ArrayList<>());

    // Prepare mock behavior
    when(employeeRepository.existsById("2")).thenReturn(false);  // Employee ID should be unique
    when(employeeRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);  // Email should be unique
    when(managerEmployeeRepository.findById("1002")).thenReturn(Optional.of(managerEmployee));  // Manager exists
    when(employeeRepository.save(employee)).thenReturn(employee);  // Save operation should return the employee

    // Call the method to be tested
    EmployeeModel savedEmployee = employeeService.addEmployee(employee);

    // Verify results
    assertNotNull(savedEmployee, "Saved employee should not be null");
    assertEquals("Jane Smith", savedEmployee.getName(), "Employee name should match");
    assertEquals("jane.smith@example.com", savedEmployee.getEmail(), "Employee email should match");

    // Verify interactions with the mocked repositories
    verify(employeeRepository).existsById("2");
    verify(employeeRepository).existsByEmail("jane.smith@example.com");
    verify(managerEmployeeRepository).findById("1002");
    verify(employeeRepository).save(employee);
}

@Test
void testAddEmployeeInSales() throws Exception {
    // Create a mock EmployeeModel
    EmployeeModel employee = new EmployeeModel();
    employee.setId("3");
    employee.setName("Emily Davis");
    employee.setEmail("emily.davis@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Bangalore");
    employee.setManagerId("1003");
    employee.setMobile("1112233445");
    employee.setdepartment("Sales");
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Create a mock ManagerEmployeeModel
    ManagerEmployeeModel managerEmployee = new ManagerEmployeeModel();
    managerEmployee.setId("1003");
    managerEmployee.setDepartment("Sales");
    managerEmployee.setEmployeeList(new ArrayList<>());

    // Prepare mock behavior
    when(employeeRepository.existsById("3")).thenReturn(false);  // Employee ID should be unique
    when(employeeRepository.existsByEmail("emily.davis@example.com")).thenReturn(false);  // Email should be unique
    when(managerEmployeeRepository.findById("1003")).thenReturn(Optional.of(managerEmployee));  // Manager exists
    when(employeeRepository.save(employee)).thenReturn(employee);  // Save operation should return the employee

    // Call the method to be tested
    EmployeeModel savedEmployee = employeeService.addEmployee(employee);

    // Verify results
    assertNotNull(savedEmployee, "Saved employee should not be null");
    assertEquals("Emily Davis", savedEmployee.getName(), "Employee name should match");
    assertEquals("emily.davis@example.com", savedEmployee.getEmail(), "Employee email should match");

    // Verify interactions with the mocked repositories
    verify(employeeRepository).existsById("3");
    verify(employeeRepository).existsByEmail("emily.davis@example.com");
    verify(managerEmployeeRepository).findById("1003");
    verify(employeeRepository).save(employee);
}

@Test
void testAddEmployeeInDelivery() throws Exception {
    // Create a mock EmployeeModel
    EmployeeModel employee = new EmployeeModel();
    employee.setId("4");
    employee.setName("Michael Brown");
    employee.setEmail("michael.brown@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Hyderabad");
    employee.setManagerId("1004");
    employee.setMobile("5556667778");
    employee.setdepartment("Delivery");
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Create a mock ManagerEmployeeModel
    ManagerEmployeeModel managerEmployee = new ManagerEmployeeModel();
    managerEmployee.setId("1004");
    managerEmployee.setDepartment("Delivery");
    managerEmployee.setEmployeeList(new ArrayList<>());

    // Prepare mock behavior
    when(employeeRepository.existsById("4")).thenReturn(false);  // Employee ID should be unique
    when(employeeRepository.existsByEmail("michael.brown@example.com")).thenReturn(false);  // Email should be unique
    when(managerEmployeeRepository.findById("1004")).thenReturn(Optional.of(managerEmployee));  // Manager exists
    when(employeeRepository.save(employee)).thenReturn(employee);  // Save operation should return the employee

    // Call the method to be tested
    EmployeeModel savedEmployee = employeeService.addEmployee(employee);

    // Verify results
    assertNotNull(savedEmployee, "Saved employee should not be null");
    assertEquals("Michael Brown", savedEmployee.getName(), "Employee name should match");
    assertEquals("michael.brown@example.com", savedEmployee.getEmail(), "Employee email should match");

    // Verify interactions with the mocked repositories
    verify(employeeRepository).existsById("4");
    verify(employeeRepository).existsByEmail("michael.brown@example.com");
    verify(managerEmployeeRepository).findById("1004");
    verify(employeeRepository).save(employee);
}



	@Test
	void testChangeEmployeeManager() {
		// Initialize additional data for the test
		EmployeeModel employeeToUpdate = new EmployeeModel();
		employeeToUpdate.setId("2");
		employeeToUpdate.setName("Jane Doe");
		employeeToUpdate.setEmail("jane.doe@example.com");
		employeeToUpdate.setDesignation("Associate");
		employeeToUpdate.setLocation("Delhi");
		employeeToUpdate.setManagerId("1001"); // Previous manager ID
		employeeToUpdate.setMobile("9876543210");
		employeeToUpdate.setdepartment("sales");
		employeeToUpdate.setCreatedTime(LocalDateTime.now());
		employeeToUpdate.setUpdatedTime(LocalDateTime.now());

		ManagerModel oldManager = new ManagerModel();
		oldManager.setId("1001");
		oldManager.setName("Alice Johnson");
		oldManager.setDepartment("Sales");

		ManagerModel newManager = new ManagerModel();
		newManager.setId("1002");
		newManager.setName("Bob Smith");
		newManager.setDepartment("Engineering");

		ManagerEmployeeModel oldManagerEmployee = new ManagerEmployeeModel();
		oldManagerEmployee.setId("1001");
		oldManagerEmployee.setName("Alice Johnson");
		oldManagerEmployee.setDepartment("Sales");
		oldManagerEmployee.setEmployeeList(new ArrayList<>());

		ManagerEmployeeModel newManagerEmployee = new ManagerEmployeeModel();
		newManagerEmployee.setId("1002");
		newManagerEmployee.setName("Bob Smith");
		newManagerEmployee.setDepartment("Engineering");
		newManagerEmployee.setEmployeeList(new ArrayList<>());
		newManagerEmployee.getEmployeeList().add(new EmployeeResponseDTO());

		// Mock repository behavior
		when(employeeRepository.findById("2")).thenReturn(Optional.of(employeeToUpdate));
		when(managerRepository.findById("1001")).thenReturn(Optional.of(oldManager));
		when(managerRepository.findById("1002")).thenReturn(Optional.of(newManager));
		when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(oldManagerEmployee));
		when(managerEmployeeRepository.findById("1002")).thenReturn(Optional.of(newManagerEmployee));

		// Mock save behavior
		when(employeeRepository.save(employeeToUpdate)).thenReturn(employeeToUpdate);

		// Mock updated manager save behavior
		ManagerModel updatedOldManager = new ManagerModel();
		updatedOldManager.setId("1001");
		updatedOldManager.setName("Alice Johnson");
		updatedOldManager.setDepartment("Sales");

		ManagerModel updatedNewManager = new ManagerModel();
		updatedNewManager.setId("1002");
		updatedNewManager.setName("Bob Smith");
		updatedNewManager.setDepartment("Engineering");

		when(managerRepository.save(oldManager)).thenReturn(updatedOldManager);
		when(managerRepository.save(newManager)).thenReturn(updatedNewManager);

		// Execute the method
		ManagerChangeResponseDTO response = employeeService.changeEmployeeManager("2", "1002");

		// Verify results
		String expectedMessage = "Jane Doe's manager has been successfully changed from Alice Johnson to Bob Smith";
		assertEquals(expectedMessage, response.getMessage(), "Response message should match");

		// Check if the employee's manager ID and department are updated
		assertEquals("1002", employeeToUpdate.getManagerId());
		assertEquals("Engineering", employeeToUpdate.getdepartment());

	}

	
	@Test
	void testDeleteNonExistentEmployee() {
    when(employeeRepository.existsById("999")).thenReturn(false);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.deleteEmployee("999");
    });
    assertEquals("Employee with id 999 does not exist.", exception.getMessage());
	}

	@Test
	void testDeleteExistingEmployeeNotManager() {	
    when(employeeRepository.existsById("1")).thenReturn(true);
    when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
    when(managerEmployeeRepository.findById("1")).thenReturn(Optional.empty());
    
    ResponseMessage response = employeeService.deleteEmployee("1");

    assertEquals("Successfully deleted John Doe from the organization.", response.getMessage());
	}

	@Test
	void testDeleteManagerWithEmployees() {

		// Create EmployeeResponseDTO instance
		EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
		employeeResponseDTO.setId("2001");
		employeeResponseDTO.setName("John Doe");
		employeeResponseDTO.setDesignation("Developer");
		employeeResponseDTO.setLocation("New York");
		employeeResponseDTO.setMobile("1234567890");
		employeeResponseDTO.setdepartment("Sales");

		// Create ManagerEmployeeModel instance with a non-empty employee list
		ManagerEmployeeModel managerWithEmployees = new ManagerEmployeeModel();
		managerWithEmployees.setId("1001");
		managerWithEmployees.setName("Alice Johnson");
		managerWithEmployees.setDepartment("Sales");

		// Create a list of EmployeeResponseDTO and add the DTO to it
		List<EmployeeResponseDTO> employeeList = new ArrayList<>();
		employeeList.add(employeeResponseDTO);

		managerWithEmployees.setEmployeeList(employeeList); // Set the employee list in manager

		// Setup mocks
		when(employeeRepository.existsById("1001")).thenReturn(true); // Ensure employee exists
		when(employeeRepository.findById("1001")).thenReturn(Optional.of(employee));
		when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(managerWithEmployees));

		// Assert that an exception is thrown when trying to delete the manager
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			employeeService.deleteEmployee("1001");
		});
		assertEquals("Cannot delete manager with id 1001 because they have employees to manage.",
				exception.getMessage());

		// Verify interactions with mocks
		
		verify(employeeRepository, times(1)).findById("1001");
		verify(managerEmployeeRepository, times(1)).findById("1001");
		verify(managerEmployeeRepository, never()).save(any(ManagerEmployeeModel.class));
		verify(departmentRepository, never()).save(any(DepartmentModel.class));
	}

	@Test
    void testGetAndSetId() {
        String id = "123";
        department.setDepId(id);
        assertEquals(id, department.getDepId());
    }

    @Test
    void testGetAndSetDepartmentName() {
        String departmentName = "Sales";
        department.setDepartmentName(departmentName);
        assertEquals(departmentName, department.getDepartmentName());
    }

    @Test
    void testGetAndSetManagerName() {
        String managerName = "John Doe";
        department.setManagerName(managerName);
        assertEquals(managerName, department.getManagerName());
    }


	@Test
    void testGetAllDepartments() {
        // Create test data
        DepartmentModel department1 = new DepartmentModel();
        department1.setDepId("1");
        department1.setDepartmentName("HR");
        department1.setManagerName("John Doe");

        DepartmentModel department2 = new DepartmentModel();
        department2.setDepId("2");
        department2.setDepartmentName("Sales");
        department2.setManagerName("Jane Doe");

        List<DepartmentModel> departments = Arrays.asList(department1, department2);

        // Mock the behavior of departmentRepository
        when(departmentRepository.findAll(Sort.by(Sort.Order.asc("id")))).thenReturn(departments);

        // Call the method under test
        List<DepartmentModel> result = employeeService.getAllDepartments();

        // Assert the result
        assertEquals(2, result.size());
        assertEquals("HR", result.get(0).getDepartmentName());
        assertEquals("Sales", result.get(1).getDepartmentName());
    }

	@Test
	 void testGetAllManagers() {
        // Create test data
        ManagerModel manager1 = new ManagerModel();
        manager1.setId("1001");
        manager1.setName("John Doe");
        manager1.setDepartment("HR");

        ManagerModel manager2 = new ManagerModel();
        manager2.setId("1002");
        manager2.setName("Jane Doe");
        manager2.setDepartment("Sales");

        List<ManagerModel> managers = Arrays.asList(manager1, manager2);

        // Mock the behavior of managerRepository
        when(managerRepository.findAll(Sort.by(Sort.Order.asc("id")))).thenReturn(managers);

        // Call the method under test
        List<ManagerModel> result = employeeService.getAllManagers();

        // Assert the result
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
    }

	@Test
    public void testGetAllEmployees() {
        // Create test data
        EmployeeModel employee1 = new EmployeeModel();
        employee1.setId("1001");
        employee1.setName("John Doe");
        employee1.setEmail("johndoe@example.com");
        employee1.setDesignation("Manager");
        employee1.setLocation("New York");
        employee1.setManagerId("0");
        employee1.setMobile("1234567890");
        employee1.setdepartment("HR");
        employee1.setCreatedTime(LocalDateTime.now());
        employee1.setUpdatedTime(LocalDateTime.now());

        EmployeeModel employee2 = new EmployeeModel();
        employee2.setId("1002");
        employee2.setName("Jane Doe");
        employee2.setEmail("janedoe@example.com");
        employee2.setDesignation("Associate");
        employee2.setLocation("Los Angeles");
        employee2.setManagerId("1001");
        employee2.setMobile("0987654321");
        employee2.setdepartment("Sales");
        employee2.setCreatedTime(LocalDateTime.now());
        employee2.setUpdatedTime(LocalDateTime.now());

        List<EmployeeModel> employees = Arrays.asList(employee1, employee2);

        // Mock the behavior of employeeRepository
        when(employeeRepository.findAll(Sort.by(Sort.Order.asc("id")))).thenReturn(employees);

        // Call the method under test
        List<EmployeeModel> result = employeeService.getAllEmployees();
        // Assert the result
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
    }

	@Test
    void testAddManagerToNonExistingDepartment() {
       

        // Setup test data
        String department = "Finance"; // Non-existing department
        ManagerEmployeeModel newManager = new ManagerEmployeeModel();
        newManager.setId("M001");
        newManager.setName("Jane Smith");

        // Mock the behavior of departmentRepository to return an empty Optional
        when(departmentRepository.findByDepartmentName(department)).thenReturn(Optional.empty());

        // Execute and Verify
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.handleEmployeeAsManager(employee, department);  // Replace with your actual method name
        });

        assertEquals("Department " + department + " does not exist.", exception.getMessage());

        // Verify no interactions with other repositories
        verify(managerEmployeeRepository, never()).save(newManager);
        verify(departmentRepository, never()).save(any(DepartmentModel.class));
        verify(managerRepository, never()).save(any(ManagerModel.class));
    }

	@Test
    public void testHandleEmployeeAsManager_DepartmentExists_NoManager() {
        // Setup test data
        String departmentName = "Sales";
        EmployeeModel newManager = new EmployeeModel();
        newManager.setId("2001");
        newManager.setName("Alice Smith");

        DepartmentModel department = new DepartmentModel();
        department.setDepId("dept01");
        department.setDepartmentName(departmentName);
        department.setManagerName(""); // No current manager

        // Mock repository behavior
        when(departmentRepository.findByDepartmentName(departmentName)).thenReturn(Optional.of(department));

        // Call the method under test
        employeeService.handleEmployeeAsManager(newManager, departmentName);

        // Verify that new manager was saved in managerEmployeeRepository
        verify(managerEmployeeRepository, times(1)).save(any(ManagerEmployeeModel.class));

        // Verify that department was updated with new manager
        verify(departmentRepository, times(1)).save(department);

        // Verify that new manager was saved in managerRepository
        verify(managerRepository, times(1)).save(any(ManagerModel.class));
    }



	@Test
    public void testHandleEmployeeAsManager_DepartmentExists_WithManager() {
        // Setup test data
        String departmentName = "Sales";
        EmployeeModel newManager = new EmployeeModel();
        newManager.setId("2001");
        newManager.setName("Alice Smith");

        DepartmentModel department = new DepartmentModel();
        department.setDepId("dept01");
        department.setDepartmentName(departmentName);
        department.setManagerName("John Doe"); // Already has a manager

        // Mock repository behavior
        when(departmentRepository.findByDepartmentName(departmentName)).thenReturn(Optional.of(department));

        // Call the method under test and expect an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.handleEmployeeAsManager(newManager, departmentName);
        });

        // Verify the exception message
        assertEquals("Department Sales already has a manager.", exception.getMessage());

        // Verify that no repositories were called to save
        verify(managerEmployeeRepository, times(0)).save(any(ManagerEmployeeModel.class));
        verify(departmentRepository, times(0)).save(any(DepartmentModel.class));
        verify(managerRepository, times(0)).save(any(ManagerModel.class));
    }

	@Test
	public void testManagerWithExperience_BothManagerIdAndExperienceProvided_ValidManager() {
		// Setup test data
		String managerId = "1001";
		int minYearsOfExperience = 5;
	
		EmployeeModel employee1 = new EmployeeModel();
		employee1.setId("2001");
		employee1.setName("Alice");
		employee1.setDateOfJoining(LocalDateTime.now().minusYears(6));
	
		EmployeeModel employee2 = new EmployeeModel();
		employee2.setId("2002");
		employee2.setName("Bob");
		employee2.setDateOfJoining(LocalDateTime.now().minusYears(4));
	
		List<EmployeeResponseDTO> employeeResponseList = List.of(
			convertToEmployeeResponseDTO(employee1),
			convertToEmployeeResponseDTO(employee2)
		);
	
		ManagerEmployeeModel manager = new ManagerEmployeeModel();
		manager.setId(managerId);
		manager.setName("John Doe");
		manager.setDepartment("Sales");
		manager.setEmployeeList(employeeResponseList);
	
		// Mock repository behavior
		when(managerEmployeeRepository.findById(managerId)).thenReturn(Optional.of(manager));
	
		// Call the method under test
		ResponseDTO responseDTO = employeeService.managerWithExperience(managerId, minYearsOfExperience);
	
		// Verify the response
		assertEquals("Successfully fetched", responseDTO.getMessage());
		assertEquals(1, responseDTO.getDetails().size());
		assertEquals(managerId, responseDTO.getDetails().get(0).getId());
		assertEquals(1, responseDTO.getDetails().get(0).getEmployeeList().size());
		assertEquals("Alice", responseDTO.getDetails().get(0).getEmployeeList().get(0).getName());
	
		// Verify the repository was called
		verify(managerEmployeeRepository, times(1)).findById(managerId);
	}
	
	private EmployeeResponseDTO convertToEmployeeResponseDTO(EmployeeModel employee) {
		EmployeeResponseDTO employeeResponse = new EmployeeResponseDTO();
		employeeResponse.setId(employee.getId());
		employeeResponse.setName(employee.getName());
		employeeResponse.setDateOfJoining(employee.getDateOfJoining());
		// set other fields as necessary
		return employeeResponse;
	}

    @Test
public void testManagerWithExperience_ManagerIdOnly_ValidManager() {
    // Setup test data
    String managerId = "1001";

    EmployeeModel employee1 = new EmployeeModel();
    employee1.setId("2001");
    employee1.setName("Alice");
    employee1.setDateOfJoining(LocalDateTime.now().minusYears(3)); // Example date of joining

    EmployeeResponseDTO employeeResponseDTO = convertToEmployeeResponseDTO(employee1);

    ManagerEmployeeModel manager = new ManagerEmployeeModel();
    manager.setId(managerId);
    manager.setName("John Doe");
    manager.setDepartment("Sales");
    manager.setEmployeeList(List.of(employeeResponseDTO)); // Use EmployeeResponseDTO

    // Mock repository behavior
    when(managerEmployeeRepository.findById(managerId)).thenReturn(Optional.of(manager));

    // Call the method under test
    ResponseDTO responseDTO = employeeService.managerWithExperience(managerId, null);

    // Verify the response
    assertEquals("Successfully fetched", responseDTO.getMessage());
    assertEquals(1, responseDTO.getDetails().size());
    assertEquals(managerId, responseDTO.getDetails().get(0).getId());
    assertEquals(1, responseDTO.getDetails().get(0).getEmployeeList().size());
    assertEquals("Alice", responseDTO.getDetails().get(0).getEmployeeList().get(0).getName());

    // Verify the repository was called
    verify(managerEmployeeRepository, times(1)).findById(managerId);
}

@Test
public void testManagerWithExperience_MinYearsOfExperience_Valid() {
    // Setup test data
    Integer minYearsOfExperience = 5;

    EmployeeModel employee1 = new EmployeeModel();
    employee1.setId("2001");
    employee1.setName("Alice");
    employee1.setDateOfJoining(LocalDateTime.now().minusYears(6)); // 6 years of experience

    EmployeeModel employee2 = new EmployeeModel();
    employee2.setId("2002");
    employee2.setName("Bob");
    employee2.setDateOfJoining(LocalDateTime.now().minusYears(3)); // 3 years of experience

    EmployeeResponseDTO employeeResponseDTO1 = convertToEmployeeResponseDTO(employee1);
    EmployeeResponseDTO employeeResponseDTO2 = convertToEmployeeResponseDTO(employee2);

    ManagerEmployeeModel manager = new ManagerEmployeeModel();
    manager.setId("1001");
    manager.setName("John Doe");
    manager.setDepartment("Sales");
    manager.setEmployeeList(List.of(employeeResponseDTO1, employeeResponseDTO2)); // Adding both employees

    // Mock repository behavior
    when(managerEmployeeRepository.findAll()).thenReturn(List.of(manager));

    // Call the method under test
    ResponseDTO responseDTO = employeeService.managerWithExperience(null, minYearsOfExperience);

    // Verify the response
    assertEquals("Successfully fetched", responseDTO.getMessage());
    assertEquals(1, responseDTO.getDetails().size());
    assertEquals(manager.getId(), responseDTO.getDetails().get(0).getId());
    assertEquals(1, responseDTO.getDetails().get(0).getEmployeeList().size()); // Only Alice should be included
    assertEquals("Alice", responseDTO.getDetails().get(0).getEmployeeList().get(0).getName());

    // Verify the repository was called
    verify(managerEmployeeRepository, times(1)).findAll();
}

@Test
public void testManagerWithExperience_NoManagerIdAndNoExperience_Valid() {
    // Setup test data
    EmployeeModel employee1 = new EmployeeModel();
    employee1.setId("2001");
    employee1.setName("Alice");
    employee1.setDateOfJoining(LocalDateTime.now().minusYears(6)); // 6 years of experience

    EmployeeResponseDTO employeeResponseDTO1 = convertToEmployeeResponseDTO(employee1);

    ManagerEmployeeModel manager = new ManagerEmployeeModel();
    manager.setId("1001");
    manager.setName("John Doe");
    manager.setDepartment("Sales");
    manager.setEmployeeList(List.of(employeeResponseDTO1)); // Adding one employee

    // Mock repository behavior
    when(managerEmployeeRepository.findAll()).thenReturn(List.of(manager));

    // Call the method under test
    ResponseDTO responseDTO = employeeService.managerWithExperience(null, null);

    // Verify the response
    assertEquals("Successfully fetched", responseDTO.getMessage());
    assertEquals(1, responseDTO.getDetails().size());
    assertEquals(manager.getId(), responseDTO.getDetails().get(0).getId());
    assertEquals(1, responseDTO.getDetails().get(0).getEmployeeList().size());
    assertEquals("Alice", responseDTO.getDetails().get(0).getEmployeeList().get(0).getName());

    // Verify the repository was called
    verify(managerEmployeeRepository, times(1)).findAll();
}

@Test
public void testDeleteManagerWithNoEmployees() {
    // Setup test data
	employee = new EmployeeModel();
	employee.setId("1001");
	employee.setName("John Doe");
	employee.setEmail("john.doe@example.com");
	employee.setDesignation("Account Manager");
	employee.setLocation("Delhi");
	employee.setManagerId("0");
	employee.setMobile("1234567890");
	employee.setdepartment("sales");
	employee.setCreatedTime(LocalDateTime.now());
	employee.setUpdatedTime(LocalDateTime.now());

	when(employeeRepository.findById("1001")).thenReturn(Optional.of(employee)); 

    String managerId = "1001";
    String departmentName = "Sales";

    ManagerEmployeeModel managerEmployeeModel = new ManagerEmployeeModel();
    managerEmployeeModel.setId(managerId);
    managerEmployeeModel.setName("John Doe");
    managerEmployeeModel.setDepartment(departmentName);
    managerEmployeeModel.setEmployeeList(new ArrayList<>()); // No employees

    ManagerModel managerModel = new ManagerModel();
    managerModel.setId(managerId);
    managerModel.setName("John Doe");
    managerModel.setDepartment(departmentName);

    DepartmentModel departmentModel = new DepartmentModel();
    departmentModel.setDepId("D001");
    departmentModel.setDepartmentName(departmentName);
    departmentModel.setManagerName("John Doe");

    // Mock repository behavior
    when(managerEmployeeRepository.findById(managerId)).thenReturn(Optional.of(managerEmployeeModel));
    when(departmentRepository.findByDepartmentName(departmentName)).thenReturn(Optional.of(departmentModel));

    // Call the method under test
    employeeService.deleteEmployee(managerId);

    // Verify the manager was deleted from both repositories
    verify(managerRepository, times(1)).deleteById(managerId);
    verify(managerEmployeeRepository, times(1)).deleteById(managerId);

    // Verify the manager was removed from the department
    assertNull(departmentModel.getManagerName()); // Manager name should be set to null
    verify(departmentRepository, times(1)).save(departmentModel);

    // Verify the departmentRepository was called
    verify(departmentRepository, times(1)).findByDepartmentName(departmentName);
}


public void testHandleEmployeeAsNewManager_Success() throws Exception {
	// Setup test data
	String departmentName = "Sales";
	DepartmentModel department = new DepartmentModel();
	department.setDepId("D001");
	department.setDepartmentName(departmentName);
	department.setManagerName(null);

	// Mock repository behavior
	when(departmentRepository.findByDepartmentName(departmentName)).thenReturn(Optional.of(department));
	when(employeeRepository.save(employee)).thenReturn(employee);

	// Call the method under test
	EmployeeModel savedEmployee = employeeService.addEmployee(employee);

	// Verify interactions and assert result
	verify(departmentRepository, times(1)).findByDepartmentName(departmentName);
	verify(managerEmployeeRepository, times(1)).save(any(ManagerEmployeeModel.class));
	verify(managerRepository, times(1)).save(any(ManagerModel.class));
	verify(employeeRepository, times(1)).save(employee);

	assertEquals(employee, savedEmployee);
}

@Test
public void testHandleEmployeeAsNewManager_InvalidDesignation() {
	// Change designation to an invalid value
	employee = new EmployeeModel();
	employee.setId("12");
	employee.setName("John Doe");
	employee.setEmail("john.doe@example.com");
	employee.setDesignation("Associate");
	employee.setLocation("Delhi");
	employee.setManagerId("0"); // This should be a valid ID in mocks
	employee.setMobile("1234567890");
	employee.setdepartment("sales");
	employee.setCreatedTime(LocalDateTime.now());
	employee.setUpdatedTime(LocalDateTime.now());
	

	// Expect IllegalArgumentException to be thrown
	IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
		employeeService.addEmployee(employee);
	});

	// Verify interactions and assert exception message
	verify(departmentRepository, never()).findByDepartmentName(toString());
	verify(managerEmployeeRepository, never()).save(any(ManagerEmployeeModel.class));
	verify(managerRepository, never()).save(any(ManagerModel.class));
	verify(employeeRepository, never()).save(any(EmployeeModel.class));

	assertEquals("Designation must be Account Manager for a new manager.", exception.getMessage());
}

@Test
void testAddEmployee_DepartmentMismatch() throws Exception {
    // Setup the employee with a department that does not match the manager's department
    EmployeeModel employee = new EmployeeModel();
    employee.setId("1003");
    employee.setName("Jane Smith");
    employee.setEmail("jane.smith@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Delhi");
    employee.setManagerId("1001"); // Assume this is a valid manager ID
    employee.setMobile("0987654321");
    employee.setdepartment("QA"); // Department that does not match the manager's department
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Setup the manager with a different department
    ManagerEmployeeModel manager = new ManagerEmployeeModel();
    manager.setId("1001");
    manager.setName("John Doe");
    manager.setDepartment("Sales"); // Manager's department is different from the employee's department

    // Mock repository behavior
    when(managerEmployeeRepository.findById(employee.getManagerId())).thenReturn(Optional.of(manager));

    // Execute and verify
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.addEmployee(employee);
    });
    assertEquals("Employee's department does not match the manager's department.", exception.getMessage());
}


@Test
void testAddEmployee_DesignatoinMismatch() throws Exception {
    // Setup the employee with a department that does not match the manager's department
    EmployeeModel employee = new EmployeeModel();
    employee.setId("1003");
    employee.setName("Jane Smith");
    employee.setEmail("jane.smith@example.com");
    employee.setDesignation("Account Manager");
    employee.setLocation("Delhi");
    employee.setManagerId("1001"); 
    employee.setMobile("0987654321");
    employee.setdepartment("Sales"); 
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Setup the manager with a different department
    ManagerEmployeeModel manager = new ManagerEmployeeModel();
    manager.setId("1001");
    manager.setName("John Doe");
    manager.setDepartment("Sales"); // Manager's department is different from the employee's department

    // Mock repository behavior
    when(managerEmployeeRepository.findById(employee.getManagerId())).thenReturn(Optional.of(manager));

    // Execute and verify
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        employeeService.addEmployee(employee);
    });
    assertEquals("Designation cannot be Account Manager for an employee with a valid manager ID.", exception.getMessage());
}


@Test
void testDeleteEmployee_ThrowsExceptionWhenEmployeeDoesNotExist() {
	// Setup
	String employeeId = "1234";
	when(employeeRepository.existsById(employeeId)).thenReturn(false);

	// Execute and Verify
	IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
		employeeService.deleteEmployee(employeeId);
	});

	assertEquals("Employee with id " + employeeId + " does not exist.", exception.getMessage());
}


@Test
void testGetId() {
assertEquals("1", employee.getId());
}

@Test
void testGetDesignation() {
assertEquals("Associate", employee.getDesignation());
}

@Test
void testGetEmail() {
assertEquals("john.doe@example.com", employee.getEmail());
}

@Test
void testGetDepartment() {
assertEquals("sales", employee.getdepartment());
}

@Test
void testGetMobile() {
assertEquals("1234567890", employee.getMobile());
}

@Test
void testGetLocation() {
assertEquals("Delhi", employee.getLocation());
}

@Test
void testGetCreatedTime() {
	assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), employee.getCreatedTime());
}

@Test
void testGetUpdatedTime() {
	assertEquals(LocalDateTime.of(2024, 1, 2, 10, 0), employee.getUpdatedTime());
}



@Test
    void testGettersAndSetters() {
        // Arrange
        EmployeeResponseDTO employee = new EmployeeResponseDTO();

        String id = "123";
        String name = "John Doe";
        String designation = "Software Engineer";
        String email = "john.doe@example.com";
        String department = "IT";
        String mobile = "1234567890";
        String location = "New York";
        LocalDateTime dateOfJoining = LocalDateTime.of(2022, 1, 15, 10, 0, 0);
        LocalDateTime createdTime = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
        LocalDateTime updatedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

       
        employee.setId(id);
        employee.setName(name);
        employee.setDesignation(designation);
        employee.setEmail(email);
        employee.setdepartment(department);
        employee.setMobile(mobile);
        employee.setLocation(location);
        employee.setDateOfJoining(dateOfJoining);
        employee.setCreatedTime(createdTime);
        employee.setUpdatedTime(updatedTime);

        assertEquals(id, employee.getId());
        assertEquals(name, employee.getName());
        assertEquals(designation, employee.getDesignation());
        assertEquals(email, employee.getEmail());
        assertEquals(department, employee.getdepartment());
        assertEquals(mobile, employee.getMobile());
        assertEquals(location, employee.getLocation());
        assertEquals(dateOfJoining, employee.getDateOfJoining());
        assertEquals(createdTime, employee.getCreatedTime());
        assertEquals(updatedTime, employee.getUpdatedTime());
    }


	@Test
    void testDefaultConstructor() {
        ManagerChangeResponseDTO responseDTO = new ManagerChangeResponseDTO();

        assertNull(responseDTO.getMessage());
    }

    @Test
    void testParameterizedConstructor() {
        String message = "Manager change successful";

        ManagerChangeResponseDTO responseDTO = new ManagerChangeResponseDTO(message);

        assertEquals(message, responseDTO.getMessage());
    }

    @Test
    void testSetMessage() {
        ManagerChangeResponseDTO responseDTO = new ManagerChangeResponseDTO();
        String message = "Manager change failed";

        responseDTO.setMessage(message);

        assertEquals(message, responseDTO.getMessage());
    }


	@Test
    void testToString() {
        EmployeeModel employee = new EmployeeModel();

        String id = "123";
        String name = "John Doe";
        String designation = "Software Engineer";
        String email = "john.doe@example.com";
        String department = "IT";
        String mobile = "1234567890";
        String location = "New York";
        String managerId = "456";
        LocalDateTime dateOfJoining = LocalDateTime.of(2022, 1, 15, 10, 0, 0);
        LocalDateTime createdTime = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
        LocalDateTime updatedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

        employee.setId(id);
        employee.setName(name);
        employee.setDesignation(designation);
        employee.setEmail(email);
        employee.setdepartment(department);
        employee.setMobile(mobile);
        employee.setLocation(location);
        employee.setManagerId(managerId); // Assuming you have a setter for managerId
        employee.setDateOfJoining(dateOfJoining);
        employee.setCreatedTime(createdTime);
        employee.setUpdatedTime(updatedTime);

        String expectedToString = "EmployeeModel [id=" + id + ", name=" + name + ", designation=" + designation + 
                ", email=" + email + ", department=" + department + ", mobile=" + mobile + 
                ", location=" + location + ", managerId=" + managerId + 
                ", dateOfJoining=" + dateOfJoining + ", createdTime=" + createdTime + 
                ", updatedTime=" + updatedTime + "]";

        String actualToString = employee.toString();

        assertEquals(expectedToString, actualToString);
    }



	@Test
    void testManagerIdEqualsZeroAndDesignationIsAccountManager() throws Exception {
		employee = new EmployeeModel();
        employee.setId("2");
        employee.setName("John Doe");
        employee.setDesignation("Account Manager");
        employee.setEmail("john.doe@example.com");
        employee.setdepartment("sales");
        employee.setMobile("1234567890");
        employee.setLocation("New York");
        employee.setManagerId("0");
        employee.setDateOfJoining(LocalDateTime.of(2022, 1, 15, 10, 0, 0));
        employee.setCreatedTime(LocalDateTime.of(2022, 1, 1, 12, 0, 0));
        employee.setUpdatedTime(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
      
        when(employeeRepository.save(employee)).thenReturn(employee);
		when(departmentRepository.findByDepartmentName("sales")).thenReturn(Optional.of(department));
        EmployeeModel savedEmployee = employeeService.addEmployee(employee);

        // Assert
        verify(employeeRepository, times(1)).save(employee);
        assertEquals(employee, savedEmployee);
    }

	

    // @Test
    // void testInvalidEmail() {
    //     assertFalse(EmailValidator.isValidEmail("plainaddress"));
    //     assertFalse(EmailValidator.isValidEmail("user@.com"));
    //     assertFalse(EmailValidator.isValidEmail("@example.com"));
    //     assertFalse(EmailValidator.isValidEmail("user@.com"));
    //     assertFalse(EmailValidator.isValidEmail("user@com"));
    // }

	@Test
    void testNullEmail() {
        assertFalse(employeeService.isValidEmail(null));
    }

	@Test
    void testEmptyEmail() {
    
        assertFalse(employeeService.isValidEmail(""));
    }

	@Test
    void testManagerIdNotFound() {
    
    String managerId = "100000";
    Integer minYearsOfExperience = 5;

    when(managerEmployeeRepository.findById(managerId)).thenReturn(Optional.empty());

    ResponseDTO result = employeeService.managerWithExperience(managerId, minYearsOfExperience);

    assertEquals("Invalid Manager ID", result.getMessage());
    }

	@Test
    void testCalculateYearsOfExperienceWhenDateOfJoiningIsNull() {
        LocalDateTime dateOfJoining = null;
        int yearsOfExperience = employeeService.calculateYearsOfExperience(dateOfJoining);
        assertEquals(0, yearsOfExperience, "Years of experience should be 0 when date of joining is null.");
    }

	@Test
    void testChangeEmployeeManagerWhenEmployeeDoesNotExist() {
        // Given
        String employeeId = "123";
        String newManagerId = "456";

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.changeEmployeeManager(employeeId, newManagerId);
        });

        assertEquals("Employee with id 123 does not exist.", thrownException.getMessage());
    }

	@Test
    void testCannotAssignManagerToAnotherPerson() {
        String employeeId = "123";
        String newManagerId = "456";
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setManagerId("0");

		EmployeeModel employee1 = new EmployeeModel();
		employee1.setId(newManagerId);
        employee1.setManagerId("0");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
		when(managerEmployeeRepository.findById(newManagerId)).thenReturn(Optional.of(managerEmployee));

        ManagerChangeResponseDTO response = employeeService.changeEmployeeManager(employeeId, newManagerId);

        assertEquals("Cannot assign manager to another person.", response.getMessage());
    }

	@Test
    void testEmployeeAlreadyAssignedToSameManager() {
        String employeeId = "123";
        String newManagerId = "456";
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setManagerId(newManagerId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        ManagerChangeResponseDTO response = employeeService.changeEmployeeManager(employeeId, newManagerId);

        assertEquals("Employee with id 123 is already assigned to manager with id 456", response.getMessage());
    }

	@Test
    void testNewManagerIdIsInvalid() {
        String employeeId = "123";
        String newManagerId = "999"; // Invalid manager ID
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setManagerId("456");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(managerEmployeeRepository.findById(newManagerId)).thenReturn(Optional.empty());

        ManagerChangeResponseDTO response = employeeService.changeEmployeeManager(employeeId, newManagerId);

        assertEquals("The new Manager Id is invalid", response.getMessage());
    }

    
    @Test
    void testManagerNotExist() {
        // Set up mock behavior
        String invalidManagerId = "1"; 
        when(managerEmployeeRepository.findById(invalidManagerId)).thenReturn(Optional.empty());
    
        EmployeeModel mockEmployee = new EmployeeModel();
        mockEmployee.setId("5");
        mockEmployee.setName("John Doe");
        mockEmployee.setEmail("john.doe@example.com");
        mockEmployee.setDesignation("Associate");
        mockEmployee.setLocation("Delhi");
        mockEmployee.setManagerId("1");
        mockEmployee.setMobile("1234567890");
        mockEmployee.setdepartment("sales");
        mockEmployee.setCreatedTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        mockEmployee.setUpdatedTime(LocalDateTime.of(2024, 1, 2, 10, 0));

        // Use mockEmployee in your test
        when(employeeRepository.findById("1")).thenReturn(Optional.of(mockEmployee));
    
        // Perform the test
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.addEmployee(mockEmployee);
        });
    
        // Verify the exception message
        assertEquals("Manager with ID " + invalidManagerId + " does not exist.", thrown.getMessage());
    }

    @Test
    void testDeleteEmployeeByIdThrowsExceptionWhenEmployeeNotFound() {
        String idToDelete = "500";

        when(employeeRepository.findById(idToDelete)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.deleteEmployee(idToDelete);
        }, "Expected deleteEmployeeById to throw, but it didn't");

        assertEquals("Employee with id " + idToDelete + " does not exist.", thrown.getMessage());
    }

    
    @Test
    void testUpdateDepartmentWhenEmployeeIsManager() {
        // Arrange
        String managerId = "1001";
        String departmentName = "Sales";

        // Mock behavior for finding the department
        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(employee));
        when(departmentRepository.findByDepartmentName(departmentName)).thenReturn(Optional.of(department));

        // Act
        assertDoesNotThrow(() -> {
            // Call the method you want to test
            employeeService.deleteEmployee(managerId);
        });

        // Verify the interactions
      //  verify(department).setManagerName(null);
        verify(departmentRepository).save(department);
    }

    @Test
    void testRemoveEmployeeFromManagerList() {
        // Initialize EmployeeModel
        employee = new EmployeeModel();
        employee.setId("1");
        employee.setName("John Doe");
        employee.setEmail("john.doe@example.com");
        employee.setDesignation("Associate");
        employee.setLocation("Delhi");
        employee.setManagerId("1001"); // Ensure this ID exists in mocks
        employee.setMobile("1234567890");
        employee.setdepartment("sales");
        employee.setCreatedTime(LocalDateTime.of(2024, 1, 1, 10, 0));
        employee.setUpdatedTime(LocalDateTime.of(2024, 1, 2, 10, 0));
    
        // Initialize ManagerEmployeeModel
        managerEmployee = new ManagerEmployeeModel();
        managerEmployee.setId("1001");
        managerEmployee.setName("Alice Johnson");
        managerEmployee.setDepartment("Sales");
        
        // Convert EmployeeModel to EmployeeResponseDTO
        EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId("1");
        employeeResponseDTO.setName("John Doe");
        // Ensure other necessary fields are set
    
        // Initialize employee list with the employee to be removed
        List<EmployeeResponseDTO> employeeList = new ArrayList<>();
        employeeList.add(employeeResponseDTO);
        managerEmployee.setEmployeeList(employeeList);
    
        // Mock repository behavior
        when(employeeRepository.existsById("1")).thenReturn(true);
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(managerEmployee));
    
        // Call the method to be tested
        employeeService.deleteEmployee("1");
    
        // Verify the employee was removed from the manager's employee list
        List<EmployeeResponseDTO> updatedEmployeeList = managerEmployee.getEmployeeList();
        assertTrue(updatedEmployeeList.stream().noneMatch(e -> "1".equals(e.getId())),
                   "Employee with id 1 should be removed from the manager's list.");
    }
    

    

    @Test
void testRemoveEmployeeWithVariousManagerIds() {
    // Define common EmployeeModel
    EmployeeModel employee = new EmployeeModel();
    employee.setId("1");
    
    // Define common ManagerEmployeeModel
    ManagerEmployeeModel managerEmployee = new ManagerEmployeeModel();
    managerEmployee.setId("1001");
    managerEmployee.setDepartment("Sales");
    managerEmployee.setEmployeeList(new ArrayList<>()); // Empty list initially
    
    // Define employeeResponseDTO
    EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO();
    employeeResponseDTO.setId("1");
    
    // Scenario 1: managerId is null
    employee.setManagerId(null);
    managerEmployee.getEmployeeList().add(employeeResponseDTO); // Add employee to manager's list

    // Mock repository behavior
    when(employeeRepository.existsById("1")).thenReturn(true);
    when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
    when(managerEmployeeRepository.findById("1001")).thenReturn(Optional.of(managerEmployee));

    // Call the method to be tested
    employeeService.deleteEmployee("1");

    // Verify the employee was not removed (since managerId is null)
    List<EmployeeResponseDTO> updatedEmployeeList = managerEmployee.getEmployeeList();
    assertTrue(updatedEmployeeList.stream().anyMatch(e -> "1".equals(e.getId())),
               "Employee with id 1 should still be in the manager's list when managerId is null.");

    // Reset managerEmployee's employee list for next scenario
    managerEmployee.setEmployeeList(new ArrayList<>());
    managerEmployee.getEmployeeList().add(employeeResponseDTO); // Add employee to manager's list
    
    // Scenario 2: managerId is "0"
    employee.setManagerId("0");

    // Call the method to be tested
    employeeService.deleteEmployee("1");

    // Verify the employee was not removed (since managerId is "0")
    updatedEmployeeList = managerEmployee.getEmployeeList();
    assertTrue(updatedEmployeeList.stream().anyMatch(e -> "1".equals(e.getId())),
               "Employee with id 1 should still be in the manager's list when managerId is '0'.");
}

@Test
void testUpdateManagerEmployeeListWhenEmployeeListIsNull() {
    // Create a mock EmployeeModel
    EmployeeModel employee = new EmployeeModel();
    employee.setId("1");
    employee.setName("John Doe");
    employee.setEmail("john.doe@example.com");
    employee.setDesignation("Associate");
    employee.setLocation("Delhi");
    employee.setManagerId("1001");
    employee.setMobile("1234567890");
    employee.setdepartment("Engineering");
    employee.setCreatedTime(LocalDateTime.now());
    employee.setUpdatedTime(LocalDateTime.now());

    // Create a mock ManagerEmployeeModel with a null employeeList
    ManagerEmployeeModel manager = new ManagerEmployeeModel();
    manager.setId("1001");
    manager.setName("Alice Johnson");
    manager.setDepartment("Engineering");
    manager.setEmployeeList(null);  // This simulates the null employeeList

    // Call the method to be tested
    employeeService.updateManagerEmployeeList(manager, employee);

    // Verify that the employeeList is initialized and contains the employee
    assertNotNull(manager.getEmployeeList(), "Employee list should not be null after updateManagerEmployeeList is called");
    assertEquals(1, manager.getEmployeeList().size(), "Employee list should contain one employee");
    assertEquals("1", manager.getEmployeeList().get(0).getId(), "Employee ID should match the added employee");
}

// @Test
// void testValidEmail() {
//     String validEmail = "test@example.com";
//     assertTrue(EmailValidator.isValidEmail(validEmail), "Expected email to be valid");
// }

// @Test
// void testInvalidEmailWithoutAtSymbol() {
//     String invalidEmail = "testexample.com";
//     assertFalse(EmailValidator.isValidEmail(invalidEmail), "Expected email to be invalid");
// }

// @Test
// void testInvalidEmailWithoutDomain() {
//     String invalidEmail = "test@.com";
//     assertFalse(EmailValidator.isValidEmail(invalidEmail), "Expected email to be invalid");
// }

// @Test
// void testInvalidEmailWithInvalidDomain() {
//     String invalidEmail = "test@example.c";
//     assertFalse(EmailValidator.isValidEmail(invalidEmail), "Expected email to be invalid");
// }

// @Test
// void testInvalidEmailWithSpecialCharacters() {
//     String invalidEmail = "test@exam!ple.com";
//     assertFalse(EmailValidator.isValidEmail(invalidEmail), "Expected email to be invalid");
// }

@Test
    void testChangeEmployeeManagerNew() {
        // Test IDs
        String employeeId = "1";
        String oldManagerId = "1001";
        String newManagerId = "1002";

        // Create a fully mocked EmployeeModel
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);
        employee.setName("John Doe");
        employee.setDesignation("Associate");
        employee.setEmail("john.doe@example.com");
        employee.setdepartment("Sales");
        employee.setMobile("1234567890");
        employee.setLocation("Delhi");
        employee.setManagerId(oldManagerId);
        employee.setDateOfJoining(LocalDateTime.of(2023, 1, 15, 10, 0));
        employee.setCreatedTime(LocalDateTime.of(2023, 1, 15, 10, 0));
        employee.setUpdatedTime(LocalDateTime.of(2023, 1, 16, 10, 0));

        ManagerEmployeeModel oldManager = new ManagerEmployeeModel();
        oldManager.setId(oldManagerId);
        oldManager.setName("Alice Johnson");
        oldManager.setDepartment("Sales");

        List<EmployeeResponseDTO> oldEmployeeList = new ArrayList<>();
        EmployeeResponseDTO employeeDTO = new EmployeeResponseDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setName("John Doe");
        oldEmployeeList.add(employeeDTO);
        oldManager.setEmployeeList(oldEmployeeList);

        ManagerEmployeeModel newManager = new ManagerEmployeeModel();
        newManager.setId(newManagerId);
        newManager.setName("Bob Smith");
        newManager.setDepartment("Marketing");
        newManager.setEmployeeList(new ArrayList<>()); 
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(managerEmployeeRepository.findById(oldManagerId)).thenReturn(Optional.of(oldManager));
        when(managerEmployeeRepository.findById(newManagerId)).thenReturn(Optional.of(newManager));

        ManagerChangeResponseDTO response = employeeService.changeEmployeeManager(employeeId, newManagerId);

        assertFalse(oldManager.getEmployeeList().stream().anyMatch(e -> e.getId().equals(employeeId)),
                "Employee should be removed from the old manager's list.");

        assertEquals(1, newManager.getEmployeeList().size(), "New manager's employee list should contain the employee.");
        assertEquals(employeeId, newManager.getEmployeeList().get(0).getId(), "New manager's list should include the employee.");

        // Verify that save operations were called on managerEmployeeRepository
        verify(managerEmployeeRepository).save(oldManager);
        verify(managerEmployeeRepository).save(newManager);
    }


}
