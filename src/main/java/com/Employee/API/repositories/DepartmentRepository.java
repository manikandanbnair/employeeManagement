package com.Employee.API.repositories;

import com.Employee.API.models.DepartmentModel;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends MongoRepository<DepartmentModel, String> {

    Optional<DepartmentModel> findByDepartmentName(String departmentName);
    List<DepartmentModel> findAll(Sort Sort);
}
