package com.example.ElasticSearchOperation.impl;

import com.example.ElasticSearchOperation.config.ElasticConfig;
import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;

import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;

import com.example.ElasticSearchOperation.repository.HotelRepository;
import com.example.ElasticSearchOperation.service.ElasticService;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.*;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;



@Service
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    private Client client;

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    private ElasticConfig config;

    @Autowired
    private HotelRepository hotelRepository;



    @Override
    public String createHotelSearch(HotelDTO hotelDTO) {

        Hotel hotel = new Hotel();
        BeanUtils.copyProperties(hotelDTO, hotel);
        hotelRepository.save(hotel);
        return "Hotel added";

    }


    @Override
    public String update(UpdateDTO updateDTO) {

        UpdateRequest updateRequest = new UpdateRequest();
        try {
            updateRequest.index("autocomplete-v5-4")
                    .type("hotel")
                    .id(updateDTO.getId())
                    .doc(jsonBuilder()
                            .startObject()
                            .field(updateDTO.getFieldName(), updateDTO.getFieldValue())
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

        DeleteResponse deleteResponse = client.prepareDelete("autocomplete-v5-4", "hotel", id).get();
        return deleteResponse.getResult().toString();

    }

    @Override
    public List<Hotel> improvedSearch(SearchDTO searchDTO){

        String[] wordList = searchDTO.getTerms().split(" ");
        List <String> searchWords = Arrays.asList(wordList);
        List <String> spellCheckedWords = new ArrayList<>();

        for (String word : searchWords){

            List <String> getWords = checkSpellErrors(word).getSpellCheckWords();
            if (!(getWords.isEmpty())) {
                String suggestedText = getWords.get(0);
                spellCheckedWords.add(suggestedText);
            }
            else spellCheckedWords.add(word);
        }

        String finalSearchString = spellCheckedWords.stream().collect(Collectors.joining(" "));
        return searchByDetails(finalSearchString, searchDTO);

    }


    @Override
    public List<Hotel> searchByDetails(String terms, SearchDTO searchDTO) {



//        SearchQuery searchQuery3 = new NativeSearchQueryBuilder()
//                                        .withQuery(QueryBuilders
//                                                .nestedQuery("userSettings",
//                                                        QueryBuilders.boolQuery()
//                                                                  .must(QueryBuilders
//                                                                          .multiMatchQuery(terms, "userSettings.gender", "userSettings.hobby", "userSettings.occupation")
//                                                                                .fuzziness(Fuzziness.AUTO)
//                                                                                .prefixLength(3)
//                                                                                .minimumShouldMatch(searchDTO.getMinMatchCriteria())),
//                                                                          ScoreMode.Max))
//
//                                        .withQuery(QueryBuilders.multiMatchQuery(terms, "name", "description")
//                                                .fuzziness(Fuzziness.AUTO)
//                                                .prefixLength(3)
//                                                .minimumShouldMatch(searchDTO.getMinMatchCriteria()))
//                                        .build();

        SearchQuery searchQuery4 = new NativeSearchQueryBuilder().withQuery(QueryBuilders.multiMatchQuery(terms)
                                                                               .field("name", 1.0f)
                                                                               .field("locationName", 5.0f)
                                                                               .field("type", 2.0f)
                                                                               .fuzziness(Fuzziness.AUTO)
                                                                               .prefixLength(3)
                                                                               .minimumShouldMatch(searchDTO.getMinMatchCriteria())
                                                                               .tieBreaker(0.5F)
                                                                               .analyzer("synonyms_analyzer")
                                                                               .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS))
                                                                               .withSort(SortBuilders.fieldSort("nearbyHotel").order(SortOrder.DESC))
                                                                 .build();

        List <Hotel> hotels = elasticsearchTemplate.queryForList(searchQuery4, Hotel.class);

        return hotels;
    }

    @Override
    public Word checkSpellErrors(String word){

        List<String> suggestedWords = new ArrayList<>();
        HashMap <Float, String> scoredWords = new HashMap<>();
        Word spellCheckWord = new Word(word, suggestedWords);

        if (word.length() <= 2) return spellCheckWord;

        SearchSourceBuilder searchNameSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder nameSuggestionBuilder = SuggestBuilders.termSuggestion( "name")
                                                                 .text(word)
                                                                 //.analyzer("synonyms_analyzer")
                                                                 .analyzer("word_join_analyzer")
                                                                 //.minDocFreq(0.1f)
                                                                 .suggestMode(TermSuggestionBuilder.SuggestMode.POPULAR)
                                                                 .prefixLength(2)
                                                                 .maxTermFreq(0.1f)
                                                                 .minWordLength(2)
                                                                 //.size(1)
                                                                 .sort(SortBy.SCORE);


        SuggestBuilder suggestNameBuilder = new SuggestBuilder();
        suggestNameBuilder.addSuggestion("suggest_name", nameSuggestionBuilder);
        searchNameSourceBuilder.suggest(suggestNameBuilder);

        SearchResponse searchNameResponse = config.client()
                                        .prepareSearch()
                                        .setIndices("autocomplete-v5-6").setTypes("_doc")
                                        .suggest(suggestNameBuilder)
                                        .execute()
                                        .actionGet();
        Suggest nameSuggestions = searchNameResponse.getSuggest();

        TermSuggestion termNameSuggestion = nameSuggestions.getSuggestion("suggest_name");
        for (TermSuggestion.Entry entry: termNameSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option: entry) {

                //suggestedWords.add(option.getText().string());
                scoredWords.put(option.getScore(), option.getText().string());

            }

        }

        SearchSourceBuilder searchLocationSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder locationNameSuggestionBuilder = SuggestBuilders.termSuggestion( "locationName")
                .text(word)
                //.analyzer("synonyms_analyzer")
                .analyzer("word_join_analyzer")
                .minDocFreq(0.1f)
                .suggestMode(TermSuggestionBuilder.SuggestMode.POPULAR)
                .minWordLength(2)
                .maxTermFreq(0.1f)
                .prefixLength(2)
                //.size(1)
                .sort(SortBy.SCORE);


        SuggestBuilder suggestLocationBuilder = new SuggestBuilder();
        suggestLocationBuilder.addSuggestion("suggest_locationName", locationNameSuggestionBuilder);
        searchLocationSourceBuilder.suggest(suggestLocationBuilder);

        SearchResponse searchLocationResponse = config.client()
                .prepareSearch()
                .setIndices("autocomplete-v5-6").setTypes("_doc")
                .suggest(suggestLocationBuilder)
                .execute()
                .actionGet();
        Suggest locationSuggestions = searchLocationResponse.getSuggest();

        TermSuggestion termLocationSuggestion = locationSuggestions.getSuggestion("suggest_locationName");
        for (TermSuggestion.Entry entry: termLocationSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option: entry) {

                //suggestedWords.add(option.getText().string());
                scoredWords.put(option.getScore(), option.getText().string());

            }

        }

        LinkedHashMap <Float, String> sortedScoredMap = new LinkedHashMap<>();

        scoredWords.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedScoredMap.put(x.getKey(), x.getValue()));

        suggestedWords = sortedScoredMap.values().stream().collect(Collectors.toList());

        spellCheckWord.setSpellCheckWords(suggestedWords);
        return spellCheckWord;

    }

}
