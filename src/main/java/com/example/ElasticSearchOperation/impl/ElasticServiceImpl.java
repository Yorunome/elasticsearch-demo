package com.example.ElasticSearchOperation.impl;

import com.example.ElasticSearchOperation.config.ElasticConfig;
import com.example.ElasticSearchOperation.dto.UserDTO;
import com.example.ElasticSearchOperation.model.Employee;
import com.example.ElasticSearchOperation.model.Word;
import com.example.ElasticSearchOperation.repository.EmployeeRepository;
import com.example.ElasticSearchOperation.service.ElasticService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;



@Service
public class ElasticServiceImpl implements ElasticService {

    public static final String TEST_SUGGESTER = "test_suggester";

    @Autowired
    private Client client;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    private ElasticConfig config;

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
    public List<Employee> searchByDetails(String terms, String minMatchCriteria) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", terms).minimumShouldMatch(minMatchCriteria))
                .build();

        SearchQuery searchQuery1 = new NativeSearchQueryBuilder().withQuery(QueryBuilders.matchQuery("description", terms)
                .fuzziness(Fuzziness.AUTO)
                .prefixLength(3)
                .minimumShouldMatch(minMatchCriteria))
                .build();


        SearchQuery searchQuery2 = null;

                searchQuery2 = new NativeSearchQueryBuilder().withQuery(QueryBuilders
                        .nestedQuery("userSettings",
                                     QueryBuilders.termQuery("hobby", "bowling"),
                                     ScoreMode.Max))
                        .build();




        List<Employee> employees = elasticsearchTemplate.queryForList(searchQuery1, Employee.class);

        return employees;
    }

    @Override
    public Word checkSpellErrors(String word){

        List<String> suggestedWords = new ArrayList<>();
        Word spellCheckWord = new Word(word, suggestedWords);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder termSuggestionBuilder = SuggestBuilders.termSuggestion("description").text(word);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);

        SearchResponse searchResponse = config.client().prepareSearch("users_ver_1").suggest(suggestBuilder).execute().actionGet();
        Suggest suggestions = searchResponse.getSuggest();

        TermSuggestion termSuggestion = suggestions.getSuggestion("suggest_user");
        for (TermSuggestion.Entry entry: termSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option: entry) {

                suggestedWords.add(option.getText().string());

            }

        }

        spellCheckWord.setSpellCheckWords(suggestedWords);
        return spellCheckWord;

    }

}
