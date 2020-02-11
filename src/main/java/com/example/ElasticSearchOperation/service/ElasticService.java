package com.example.ElasticSearchOperation.service;

import com.example.ElasticSearchOperation.dto.UserDTO;
import com.example.ElasticSearchOperation.entity.Employee;

import java.util.List;
import java.util.Map;

public interface ElasticService {

    String createEmployee(UserDTO userDTO);
    Map<String, Object> view(final String id);
    Map<String, Object> searchByName(final String field);
    String update(final String id);
    String delete(final String id);
    List<Employee> searchByDetails(final String terms, String minMatchCriteria);


}
