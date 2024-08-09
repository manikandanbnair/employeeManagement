package com.Employee.API.repositories;

import org.springframework.stereotype.Repository;

import com.Employee.API.models.ManagerModel;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface ManagerRepository extends MongoRepository<ManagerModel, String> {
    List<ManagerModel> findAll(Sort sort);
}
