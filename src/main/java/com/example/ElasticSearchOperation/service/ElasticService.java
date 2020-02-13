package com.example.ElasticSearchOperation.service;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.dto.UserDTO;
import com.example.ElasticSearchOperation.model.Employee;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;

import java.util.List;
import java.util.Map;

public interface ElasticService {

    String createEmployee(UserDTO userDTO);
    Map<String, Object> view(final String id);
    Map<String, Object> searchByName(final String field);
    String update(UpdateDTO updateDTO);
    String delete(final String id);
    Map<String,Object> searchByDetails(final String terms, SearchDTO searchDTO);
    Word checkSpellErrors(String word);
    Map<String,Object> improvedSearch(SearchDTO searchDTO);
    String createHotelSearch(HotelDTO hotelDTO);


}
