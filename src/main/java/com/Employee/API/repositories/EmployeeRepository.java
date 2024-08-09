package com.Employee.API.repositories;



import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.Employee.API.models.EmployeeModel;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {

    boolean existsByEmail(String email);
    List<EmployeeModel> findAll(Sort sort);
}

