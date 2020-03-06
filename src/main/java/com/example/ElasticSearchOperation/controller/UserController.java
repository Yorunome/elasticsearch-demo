package com.example.ElasticSearchOperation.controller;

import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.response.BaseResponse;
import com.example.ElasticSearchOperation.model.response.SearchHotelResponse;
import com.example.ElasticSearchOperation.service.ElasticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import rx.Single;
import rx.schedulers.Schedulers;

import static com.example.ElasticSearchOperation.controller.constant.APIConstants.*;

import java.net.URI;
import java.util.List;



@RestController
@RequestMapping(value = BASE_PATH)
public class UserController {

    @Autowired
    private ElasticService elasticService;


    @PostMapping(value = ADD)
    public BaseResponse<String> createHotelLocation(@RequestBody HotelDTO hotelDTO) {

        return new BaseResponse<>(elasticService.createHotelSearch(hotelDTO));

    }


    @PostMapping(value = UPDATE)
    public BaseResponse<String> update(@RequestBody UpdateDTO updateDTO) {

        return elasticService.update(updateDTO);

    }

    @GetMapping(value = DELETE)
    public BaseResponse<String> delete(@PathVariable final String id) {

        return (elasticService.delete(id));

    }

    @PostMapping(value = SPELL_CHECK_SEARCH)
    public BaseResponse<List<Hotel>> spellCheckSearch(@RequestBody SearchDTO searchDTO) {

        return (elasticService.improvedSearch(searchDTO));

    }


    @PostMapping(value = REACTIVE_SEARCH)
    public DeferredResult<BaseResponse<List<Hotel>>> search(@RequestBody SearchDTO searchDTO) {
        DeferredResult<BaseResponse<List<Hotel>>> responseDeferredResult = new DeferredResult<>();


        elasticService
                .searchWithReactive(searchDTO).subscribe(responseDeferredResult::setResult,responseDeferredResult::setErrorResult);

        return responseDeferredResult;
    }


}




