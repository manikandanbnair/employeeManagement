package com.Employee.API.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.Employee.API.helpers.EmailValidator;
import com.Employee.API.models.EmployeeModel;
import com.Employee.API.models.ManagerEmployeeModel;
import com.Employee.API.models.ManagerModel;
import com.Employee.API.models.DepartmentModel;
import com.Employee.API.repositories.EmployeeRepository;
import com.Employee.API.repositories.ManagerEmployeeRepository;
import com.Employee.API.repositories.ManagerRepository;
import com.Employee.API.repositories.DepartmentRepository;
import com.Employee.API.response.EmployeeResponseDTO;
import com.Employee.API.response.ManagerChangeResponseDTO;
import com.Employee.API.response.ResponseDTO;
import com.Employee.API.response.ResponseMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerEmployeeRepository managerEmployeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<DepartmentModel> getAllDepartments() {
        return departmentRepository.findAll(Sort.by(Sort.Order.asc("id")));
    }

    public List<EmployeeModel> getAllEmployees() {
        return employeeRepository.findAll(Sort.by(Sort.Order.asc("id")));
    }

    public List<ManagerModel> getAllManagers() {
        return managerRepository.findAll(Sort.by(Sort.Order.asc("id")));
    }

    // Add a new employee
    public EmployeeModel addEmployee(EmployeeModel employee) throws Exception {
        final String id = employee.getId();
        final String email = employee.getEmail();
        final String designation = employee.getDesignation();
        final String mobileNumber = employee.getMobile();
        final String department = employee.getdepartment();
        final String managerId = employee.getManagerId();

        // for validating the employee data
        validateEmployeeData(id, email, designation, mobileNumber, department, managerId);

        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedTime(now);
        employee.setUpdatedTime(now);

        // to check if an employee can be a manager
        if (managerId.equals("0")) {

            if (designation.equalsIgnoreCase("Account Manager")) {

                handleEmployeeAsManager(employee, department);

                return employeeRepository.save(employee);
            } else {
                throw new IllegalArgumentException("Designation must be Account Manager for a new manager.");
            }
        } else {
            // add an employee to a manager if the manager exists
            Optional<ManagerEmployeeModel> managerOpt = managerEmployeeRepository.findById(managerId);
            if (managerOpt.isPresent()) {
                ManagerEmployeeModel manager = managerOpt.get();
                String managerDepartment = manager.getDepartment();
                // check for designation validity and department compatability
                if (!designation.equalsIgnoreCase("Account Manager")) {

                    if (department.equalsIgnoreCase(managerDepartment)) {
                        updateManagerEmployeeList(manager, employee);

                        return employeeRepository.save(employee);

                    } else {
                        throw new IllegalArgumentException(
                                "Employee's department does not match the manager's department.");
                    }

                } else {
                    throw new IllegalArgumentException(
                            "Designation cannot be Account Manager for an employee with a valid manager ID.");
                }
            } else {
                throw new IllegalArgumentException("Manager with ID " + managerId + " does not exist.");
            }
        }
    }

    // employee details validation
    private void validateEmployeeData(String id, String email, String designation, String mobileNumber,
            String department, String managerId) throws IllegalArgumentException {
        if (employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee with id " + id + " already exists.");
        }

        if (!designation.equalsIgnoreCase("Account Manager") && !designation.equalsIgnoreCase("associate")) {
            throw new IllegalArgumentException("Designation can only be Account Manager or associate");
        }

        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Employee with email already exists.");
        }

        if (!department.equalsIgnoreCase("sales")
                && !department.equalsIgnoreCase("delivery")
                && !department.equalsIgnoreCase("QA")
                && !department.equalsIgnoreCase("engineering")
                && !department.equalsIgnoreCase("BA")) {
            throw new IllegalArgumentException(
                    "Invalid department. Must be one of: sales, delivery, QA, engineering, BA");
        }

        if (mobileNumber.length() != 10 || !mobileNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid mobile number. It must be a 10-digit number");
        }

        if (!EmailValidator.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!managerId.equals("0") && !employeeRepository.existsById(managerId)) {
            throw new IllegalArgumentException("Invalid manager ID");
        }
    }

    // creating a new manager if a manager is not appointed to a department
    public void handleEmployeeAsManager(EmployeeModel newManager, String department) {

        // Check if the department exists
        Optional<DepartmentModel> departmentOpt = departmentRepository.findByDepartmentName(department);
        if (departmentOpt.isPresent()) {
            DepartmentModel dept = departmentOpt.get();
            String currentManagerName = dept.getManagerName();

            // Check if the department already has a manager
            if (currentManagerName != null && !currentManagerName.isEmpty()) {
                throw new IllegalArgumentException(
                        "Department " + dept.getDepartmentName() + " already has a manager.");
            } else {

                ManagerEmployeeModel newManagerEmployee = new ManagerEmployeeModel();
                newManagerEmployee.setId(newManager.getId());
                newManagerEmployee.setName(newManager.getName());
                newManagerEmployee.setDepartment(department);
                newManagerEmployee.setEmployeeList(new ArrayList<>());
                managerEmployeeRepository.save(newManagerEmployee);

                // Update the departmentDB with the new manager
                dept.setManagerName(newManager.getName());
                departmentRepository.save(dept);

                // Creating a new manager in managerDB
                ManagerModel newManagerModel = new ManagerModel();
                newManagerModel.setId(newManager.getId());
                newManagerModel.setName(newManager.getName());
                newManagerModel.setDepartment(department);
                managerRepository.save(newManagerModel);

            }
        } else {
            throw new IllegalArgumentException("Department " + department + " does not exist.");
        }
    }

    private void updateManagerEmployeeList(ManagerEmployeeModel manager, EmployeeModel employee) {
        List<EmployeeResponseDTO> employeeList = manager.getEmployeeList();
        if (employeeList == null) {
            employeeList = new ArrayList<>();
        }

        // Convert EmployeeModel to EmployeeResponseDTO without managerId
        EmployeeResponseDTO employeeWithoutManagerId = convertToDTO(employee);

        // Check if employee is already in the list
        boolean employeeExists = employeeList.stream()
                .anyMatch(e -> e.getId().equals(employeeWithoutManagerId.getId()));

        if (!employeeExists) {
            // Add new employee to the list
            employeeList.add(employeeWithoutManagerId);
            manager.setEmployeeList(employeeList);
            managerEmployeeRepository.save(manager);
        }
    }

    // manager with year of experience
    public ResponseDTO managerWithExperience(String managerId, Integer minYearsOfExperience) {
        List<ResponseDTO.Details> detailsList = new ArrayList<>();
        // case 1
        boolean check = true;
        if (managerId != null && minYearsOfExperience != null) {
            try {
                Optional<ManagerEmployeeModel> managerOpt = managerEmployeeRepository.findById(managerId);
                if (managerOpt.isPresent()) {
                    ManagerEmployeeModel manager = managerOpt.get();
                    List<EmployeeResponseDTO> filteredEmployees = manager.getEmployeeList().stream()
                            .filter(employee -> calculateYearsOfExperience(
                                    employee.getDateOfJoining()) >= minYearsOfExperience)
                            .map(this::removeManagerId)
                            .collect(Collectors.toList());

                    ResponseDTO.Details details = new ResponseDTO.Details();
                    details.setAccountManager(manager.getName());
                    details.setDepartment(manager.getDepartment());
                    details.setId(manager.getId());
                    details.setEmployeeList(filteredEmployees);

                    detailsList.add(details);
                } else {
                    check = false;
                }
            } catch (IllegalArgumentException e) {
                e.getMessage();
            }

        } // case 2
        else if (managerId != null) {

            Optional<ManagerEmployeeModel> managerOpt = managerEmployeeRepository.findById(managerId);
            if (managerOpt.isPresent()) {
                ManagerEmployeeModel manager = managerOpt.get();
                List<EmployeeResponseDTO> employees = manager.getEmployeeList().stream()
                        .map(this::removeManagerId)
                        .collect(Collectors.toList());

                ResponseDTO.Details details = new ResponseDTO.Details();
                details.setAccountManager(manager.getName());
                details.setDepartment(manager.getDepartment());
                details.setId(manager.getId());
                details.setEmployeeList(employees);

                detailsList.add(details);
            }
        }
        // case 3
        else if (minYearsOfExperience != null) {

            List<ManagerEmployeeModel> allManagers = managerEmployeeRepository.findAll();
            for (ManagerEmployeeModel manager : allManagers) {
                List<EmployeeResponseDTO> filteredEmployees = manager.getEmployeeList().stream()
                        .filter(employee -> calculateYearsOfExperience(
                                employee.getDateOfJoining()) >= minYearsOfExperience)
                        .map(this::removeManagerId)
                        .collect(Collectors.toList());

                ResponseDTO.Details details = new ResponseDTO.Details();
                details.setAccountManager(manager.getName());
                details.setDepartment(manager.getDepartment());
                details.setId(manager.getId());
                details.setEmployeeList(filteredEmployees);

                detailsList.add(details);
            }
        } // case 4
        else {

            List<ManagerEmployeeModel> allManagers = managerEmployeeRepository.findAll();
            for (ManagerEmployeeModel manager : allManagers) {
                List<EmployeeResponseDTO> employees = manager.getEmployeeList().stream()
                        .map(this::removeManagerId)
                        .collect(Collectors.toList());

                ResponseDTO.Details details = new ResponseDTO.Details();
                details.setAccountManager(manager.getName());
                details.setDepartment(manager.getDepartment());
                details.setId(manager.getId());
                details.setEmployeeList(employees);

                detailsList.add(details);
            }
        }

        ResponseDTO responseDTO = new ResponseDTO(managerId);
        if (check) {
            responseDTO.setMessage("Successfully fetched");
            if (!detailsList.isEmpty()) {
                responseDTO.setDetails(detailsList);
            }

        } else {
            responseDTO.setMessage("Invalid Manager ID");

        }

        return responseDTO;
    }

    private int calculateYearsOfExperience(LocalDateTime dateOfJoining) {
        if (dateOfJoining == null) {
            return 0;
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        return (int) ChronoUnit.YEARS.between(dateOfJoining, currentDateTime);
    }

    private EmployeeResponseDTO removeManagerId(EmployeeResponseDTO employee) {
        return employee;
    }

    private EmployeeResponseDTO convertToDTO(EmployeeModel employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setDesignation(employee.getDesignation());
        dto.setEmail(employee.getEmail());
        dto.setdepartment(employee.getdepartment());
        dto.setMobile(employee.getMobile());
        dto.setLocation(employee.getLocation());
        dto.setDateOfJoining(employee.getDateOfJoining());
        dto.setCreatedTime(employee.getCreatedTime());
        dto.setUpdatedTime(employee.getUpdatedTime());
        return dto;
    }

    public ManagerChangeResponseDTO changeEmployeeManager(String employeeId, String newManagerId) {
        Optional<EmployeeModel> employeeOpt = employeeRepository.findById(employeeId);
        if (!employeeOpt.isPresent()) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " does not exist.");
        }

        EmployeeModel employee = employeeOpt.get();
        String name = employee.getName();
        String oldManagerId = employee.getManagerId();

        String oldManagerName = null;
        String newManagerName = null;
        String newManagerDepartment = null;

        //cannot assign a manager for an existing manager
        if (!oldManagerId.equals("0")) {
            Optional<ManagerModel> oldManagerOpt = managerRepository.findById(oldManagerId);
            if (oldManagerOpt.isPresent()) {
                oldManagerName = oldManagerOpt.get().getName();
            }
        } else {
            return new ManagerChangeResponseDTO("Cannot assign manager to another person.");
        }

        if (oldManagerId.equals(newManagerId)) {
            return new ManagerChangeResponseDTO(
                    "Employee with id " + employeeId + " is already assigned to manager with id " + newManagerId);
        }
        
        // check if the new manager id is valid
        Optional<ManagerEmployeeModel> newManagerOption = managerEmployeeRepository.findById(newManagerId);
        if (!newManagerOption.isPresent()) {
            return new ManagerChangeResponseDTO("The new Manager Id is invalid");
        }

        // Remove from old manager
        Optional<ManagerEmployeeModel> oldManagerOpt = managerEmployeeRepository.findById(oldManagerId);
        if (oldManagerOpt.isPresent()) {
            ManagerEmployeeModel oldManager = oldManagerOpt.get();
            List<EmployeeResponseDTO> oldEmployeeList = oldManager.getEmployeeList();
            oldEmployeeList.removeIf(e -> e.getId().equals(employeeId));
            managerEmployeeRepository.save(oldManager);
        }

        // Add to new manager
        Optional<ManagerEmployeeModel> newManagerOpt = managerEmployeeRepository.findById(newManagerId);
        if (newManagerOpt.isPresent()) {
            ManagerEmployeeModel newManager = newManagerOpt.get();
            List<EmployeeResponseDTO> newEmployeeList = newManager.getEmployeeList();

            newManagerName = newManager.getName();
            newManagerDepartment = newManager.getDepartment();

            // Update employee's details only if the manager is present
            employee.setManagerId(newManagerId);
            employee.setdepartment(newManagerDepartment);
            employee.setUpdatedTime(LocalDateTime.now());
            employeeRepository.save(employee);

            newEmployeeList.add(convertToDTO(employee));
            newManager.setEmployeeList(newEmployeeList);
            managerEmployeeRepository.save(newManager);
        }

        return new ManagerChangeResponseDTO(String.format("%s's manager has been successfully changed from " + oldManagerName + " to %s",
                name, newManagerName));
    }

    public ResponseMessage deleteEmployee(String id) {
        // Check if the employee exists
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee with id " + id + " does not exist.");
        }

        // Find the employee
        EmployeeModel employeeToDelete = employeeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Employee with id " + id + " does not exist."));
        String name = employeeToDelete.getName();
        String managerId = employeeToDelete.getManagerId();

        // Check if the employee to delete is a manager
        Optional<ManagerEmployeeModel> managerOpt = managerEmployeeRepository.findById(id);
        if (managerOpt.isPresent()) {
            ManagerEmployeeModel manager = managerOpt.get();
            List<EmployeeResponseDTO> employeeList = manager.getEmployeeList();

            // If the manager has employees, don't delete
            if (!employeeList.isEmpty()) {
                throw new IllegalArgumentException(
                        "Cannot delete manager with id " + id + " because they have employees to manage.");
            }

            // If the manager has no employees, delete the manager from both repositories
            String dep = manager.getDepartment();
            managerRepository.deleteById(id);
            managerEmployeeRepository.deleteById(id);

            // delete manager from department
            Optional<DepartmentModel> departmentOpt = departmentRepository.findByDepartmentName(dep);
            if (departmentOpt.isPresent()) {
                DepartmentModel department = departmentOpt.get();
                department.setManagerName(null);
                departmentRepository.save(department);
            }
        }

        // Delete the employee from the employee repository
        employeeRepository.deleteById(id);

        // If the employee had a manager, update the manager's employee list
        if (managerId != null && !managerId.equals("0")) {
            Optional<ManagerEmployeeModel> employeeManagerOpt = managerEmployeeRepository.findById(managerId);
            if (employeeManagerOpt.isPresent()) {
                ManagerEmployeeModel employeeManager = employeeManagerOpt.get();
                List<EmployeeResponseDTO> employeeList = employeeManager.getEmployeeList();

                // Remove the employee from the manager's list
                employeeList.removeIf(e -> e.getId().equals(id));
                employeeManager.setEmployeeList(employeeList);
                managerEmployeeRepository.save(employeeManager);
            }
        }

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("Successfully deleted " + name + " from the organization.");
        return responseMessage;
    }

}
