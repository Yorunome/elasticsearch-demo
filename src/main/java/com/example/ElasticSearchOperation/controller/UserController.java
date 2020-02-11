package com.example.ElasticSearchOperation.controller;

import com.example.ElasticSearchOperation.dto.UserDTO;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@RestController
@RequestMapping("/rest/users")
public class UserController {

    @Autowired
    Client client;

    @PostMapping("/create")
    public String create(@RequestBody UserDTO userDTO) throws IOException {


        IndexResponse response = client.prepareIndex("users", "employee", userDTO.getUserId())
                .setSource(jsonBuilder()
                        .startObject()
                        .field("name", userDTO.getName())
                        .field("userSettings", userDTO.getUserSettings())
                        .endObject()
                ).get();
        System.out.println("response id:"+response.getId());
        return response.getResult().toString();

    }

    @GetMapping("/view/{id}")
    public Map<String, Object> view(@PathVariable final String id) {

        GetResponse getResponse = client.prepareGet("users", "employee", id).get();
        return getResponse.getSource();

    }


    @GetMapping("/view/name/{field}")

    public Map<String, Object> searchByName(@PathVariable final String field) {

        Map<String,Object> map = null;
        SearchResponse response = client.prepareSearch("users")
                .setTypes("employee")
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                //.setQuery(QueryBuilders.matchQuery("name", field))
                .setQuery(QueryBuilders.matchQuery("name", field))
                .get();

        List<SearchHit> searchHits = Arrays.asList(response.getHits().getHits());
        map =   searchHits.get(0).getSourceAsMap();
        return map;

    }


    @GetMapping("/update/{id}")

    public String update(@PathVariable final String id) throws IOException {

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("users")
                .type("employee")
                .id(id)
                .doc(jsonBuilder()
                        .startObject()
                        .field("name", "Rajesh")
                        .endObject());

        try {
            UpdateResponse updateResponse = client.update(updateRequest).get();
            System.out.println(updateResponse.status());
            return updateResponse.status().toString();

        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }

        return "Exception";

    }

    @GetMapping("/delete/{id}")

    public String delete(@PathVariable final String id) {

        DeleteResponse deleteResponse = client.prepareDelete("users", "employee", id).get();
        return deleteResponse.getResult().toString();

    }

}


