package com.example.ElasticSearchOperation.service;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;
import com.example.ElasticSearchOperation.model.response.BaseResponse;
import com.example.ElasticSearchOperation.model.response.SearchHotelResponse;
import org.elasticsearch.search.aggregations.Aggregation;
import rx.Single;

import java.util.List;

public interface ElasticService {

    BaseResponse update(UpdateDTO updateDTO);

    BaseResponse delete(String id);

    BaseResponse checkSpellErrors(String word);

    BaseResponse improvedSearch(SearchDTO searchDTO);

    String createHotelSearch(HotelDTO hotelDTO);

    Single<BaseResponse<List<Hotel>>> searchWithReactive(SearchDTO searchDTO);


}
