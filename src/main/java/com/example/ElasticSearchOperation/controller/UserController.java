package com.example.ElasticSearchOperation.controller;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.dto.UserDTO;
//import com.example.ElasticSearchOperation.model.Employee;
import com.example.ElasticSearchOperation.model.Hotel;
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
    private Client client;

    @Autowired
    private ElasticService elasticService;

//    @PostMapping("/createEmployee")
//    public String createEmployee(@RequestBody UserDTO userDTO) {
//
//        return elasticService.createEmployee(userDTO);
//
//    }

    @PostMapping("/createHotel")
    public String createHotelLocation(@RequestBody HotelDTO hotelDTO){

        return elasticService.createHotelSearch(hotelDTO);

    }

//    @GetMapping("/view/{id}")
//    public Map<String, Object> view(@PathVariable final String id) {
//
//        return elasticService.view(id);
//
//    }


//    @GetMapping("/view/name/{field}")
//    public Map<String, Object> searchByName(@PathVariable final String field) {
//
//        return elasticService.searchByName(field);
//
//    }


    @PostMapping("/update")
    public String update(@RequestBody UpdateDTO updateDTO){

        return elasticService.update(updateDTO);

    }

//    @GetMapping("/delete/{id}")
//    public String delete(@PathVariable final String id) {
//
//        return elasticService.delete(id);
//
//    }

    @PostMapping("/search")
    public List<Hotel> searchByDetails(@RequestBody SearchDTO searchDTO){

        return elasticService.searchByDetails(searchDTO.getTerms(), searchDTO);

    }

    @GetMapping("/spellCheck/{word}")
    public Word getWords(@PathVariable String word) throws Exception{

        return elasticService.checkSpellErrors(word);

    }

    @PostMapping("/spellCheckSearch")
    public List<Hotel> spellCheckSearch(@RequestBody SearchDTO searchDTO){

        return elasticService.improvedSearch(searchDTO);

    }


}


