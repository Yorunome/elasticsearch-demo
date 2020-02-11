package com.example.ElasticSearchOperation.controller;

import com.example.ElasticSearchOperation.dto.UserDTO;
import com.example.ElasticSearchOperation.entity.Employee;
import com.example.ElasticSearchOperation.service.ElasticService;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    Client client;

    @Autowired
    ElasticService elasticService;

    @PostMapping("/createEmployee")
    public String createEmployee(@RequestBody UserDTO userDTO) {

        return elasticService.createEmployee(userDTO);

    }

    @GetMapping("/view/{id}")
    public Map<String, Object> view(@PathVariable final String id) {

        return elasticService.view(id);

    }


    @GetMapping("/view/name/{field}")
    public Map<String, Object> searchByName(@PathVariable final String field) {

        return elasticService.searchByName(field);

    }


    @GetMapping("/update/{id}")
    public String update(@PathVariable final String id){

        return elasticService.update(id);

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable final String id) {

        return elasticService.delete(id);

    }

    @GetMapping("/search/{terms}")
    public List <Employee> searchByDetails(@PathVariable final String terms){

        return elasticService.searchByDetails(terms);

    }

}


