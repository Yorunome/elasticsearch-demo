package com.example.ElasticSearchOperation.controller;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;
import com.example.ElasticSearchOperation.service.ElasticService;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    private Client client;

    @Autowired
    private ElasticService elasticService;


    @PostMapping("/createHotel")
    public String createHotelLocation(@RequestBody HotelDTO hotelDTO){

        return elasticService.createHotelSearch(hotelDTO);

    }


    @PostMapping("/update")
    public String update(@RequestBody UpdateDTO updateDTO){

        return elasticService.update(updateDTO);

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable final String id) {

        return elasticService.delete(id);

    }

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


