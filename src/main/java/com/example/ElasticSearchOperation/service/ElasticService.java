package com.example.ElasticSearchOperation.service;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;
import com.example.ElasticSearchOperation.model.response.BaseResponse;
import org.elasticsearch.search.aggregations.Aggregation;

import java.util.List;

public interface ElasticService {

    BaseResponse update(UpdateDTO updateDTO);

    BaseResponse delete(String id);

    BaseResponse checkSpellErrors(String word);

    BaseResponse improvedSearch(SearchDTO searchDTO);

    String createHotelSearch(HotelDTO hotelDTO);


}
