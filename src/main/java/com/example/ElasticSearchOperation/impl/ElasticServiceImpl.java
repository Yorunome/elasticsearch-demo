package com.example.ElasticSearchOperation.impl;

import com.example.ElasticSearchOperation.ElasticSearchOperationApplication;
import com.example.ElasticSearchOperation.dto.UserDTO;
import com.example.ElasticSearchOperation.entity.Employee;
import com.example.ElasticSearchOperation.repository.EmployeeRepository;
import com.example.ElasticSearchOperation.service.ElasticService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;



@Service
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    Client client;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Override
    public String createEmployee(UserDTO userDTO) {

//        IndexResponse response = null;
//        try {
//            response = client.prepareIndex("users", "employee", userDTO.getUserId())
//                    .setSource(jsonBuilder()
//                            .startObject()
//                            .field("name", userDTO.getName())
//                            .field("userSettings", userDTO.getUserSettings())
//                            .endObject()
//                    ).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("response id:"+response.getId());
//        return response.getResult().toString();

        Employee employee = new Employee();
        BeanUtils.copyProperties(userDTO, employee);
        employeeRepository.save(employee);
        return "Employee added";



    }

    @Override
    public Map<String, Object> view(String id) {

        GetResponse getResponse = client.prepareGet("users", "employee", id).get();
        return getResponse.getSource();


    }

    @Override
    public Map<String, Object> searchByName(String field) {

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

    @Override
    public String update(String id) {

        UpdateRequest updateRequest = new UpdateRequest();
        try {
            updateRequest.index("users")
                    .type("employee")
                    .id(id)
                    .doc(jsonBuilder()
                            .startObject()
                            .field("name", "Rajesh")
                            .endObject());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            UpdateResponse updateResponse = client.update(updateRequest).get();
            System.out.println(updateResponse.status());
            return updateResponse.status().toString();

        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }

        return "Exception";

    }

    @Override
    public String delete(String id) {

        DeleteResponse deleteResponse = client.prepareDelete("users", "employee", id).get();
        return deleteResponse.getResult().toString();

    }

    @Override
    public List<Employee> searchByDetails(String terms) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", terms).minimumShouldMatch("%75"))
                .build();

        List<Employee> employees = elasticsearchTemplate.queryForList(searchQuery, Employee.class);

        return employees;
    }
}
