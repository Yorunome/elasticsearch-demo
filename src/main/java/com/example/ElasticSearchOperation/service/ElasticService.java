package com.example.ElasticSearchOperation.service;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;

import java.util.List;

public interface ElasticService {

    String update(UpdateDTO updateDTO);
    String delete(final String id);
    List<Hotel> searchByDetails(final String terms, SearchDTO searchDTO);
    Word checkSpellErrors(String word);
    List<Hotel> improvedSearch(SearchDTO searchDTO);
    String createHotelSearch(HotelDTO hotelDTO);


}
