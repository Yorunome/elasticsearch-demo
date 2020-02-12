package com.example.ElasticSearchOperation.repository;

import com.example.ElasticSearchOperation.model.Employee;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends ElasticsearchCrudRepository<Employee, String> {
}
