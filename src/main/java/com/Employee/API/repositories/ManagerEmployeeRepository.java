package com.Employee.API.repositories;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.Employee.API.models.ManagerEmployeeModel;


public interface ManagerEmployeeRepository extends MongoRepository<ManagerEmployeeModel, String> {
    Optional<ManagerEmployeeModel> findById(String id);
}
