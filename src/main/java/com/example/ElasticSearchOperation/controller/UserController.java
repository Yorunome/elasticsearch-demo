package com.example.ElasticSearchOperation.controller;

import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UserDTO;
import com.example.ElasticSearchOperation.model.Employee;
import com.example.ElasticSearchOperation.model.Word;
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

    @PostMapping("/search")
    public List <Employee> searchByDetails(@RequestBody SearchDTO searchDTO){

        return elasticService.searchByDetails(searchDTO.getTerms(), searchDTO.getMinMatchCriteria());

    }

    @GetMapping("/spellCheck/{word}")
    public Word getWords(@PathVariable String word) throws Exception{

        return elasticService.checkSpellErrors(word);

    }

    @PostMapping("/spellCheckSearch")
    public List <Employee> spellCheckSearch(@RequestBody SearchDTO searchDTO){

        return elasticService.improvedSearch(searchDTO.getTerms(), searchDTO.getMinMatchCriteria());

    }


}


