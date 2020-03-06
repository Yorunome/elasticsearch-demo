package com.example.ElasticSearchOperation.impl;

import com.example.ElasticSearchOperation.config.ElasticConfig;
import com.example.ElasticSearchOperation.dto.HotelDTO;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.dto.UpdateDTO;
import com.example.ElasticSearchOperation.impl.constants.ElasticSearchIndex;
import com.example.ElasticSearchOperation.impl.constants.SearchFields;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.Word;
import com.example.ElasticSearchOperation.model.response.BaseResponse;
import com.example.ElasticSearchOperation.model.response.SearchHotelResponse;
import com.example.ElasticSearchOperation.service.ElasticService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.*;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import rx.Single;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.example.ElasticSearchOperation.impl.helper.SearchHelper.searchQueryMaker;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;



@Service
@Slf4j
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    private Client client;

    @Autowired
    private ElasticConfig config;

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;


    @Override
    public String createHotelSearch(HotelDTO hotelDTO) {

        try {
            IndexResponse response = client
                    .prepareIndex(ElasticSearchIndex.index, ElasticSearchIndex.type)
                    .setSource(jsonBuilder()
                            .startObject()
                            .field(SearchFields.id, hotelDTO.getId())
                            .field(SearchFields.name, hotelDTO.getName())
                            .field(SearchFields.locationName, hotelDTO.getLocationName())
                            .field(SearchFields.active, hotelDTO.isActive())
                            .field(SearchFields.nearbyHotel, hotelDTO.getNearbyHotel())
                            .field(SearchFields.type, hotelDTO.getType())
                            .field(SearchFields.path, hotelDTO.getPath())
                            .endObject()
                    )
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Hotel Created";

    }


    @Override
    public BaseResponse<String> update(UpdateDTO updateDTO) {

        UpdateRequest updateRequest = new UpdateRequest();
        try {
            updateRequest.index(ElasticSearchIndex.index)
                    .type(ElasticSearchIndex.type)
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
            return new BaseResponse<>(updateResponse.status().toString());

        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e);
        }

        return new BaseResponse<>("Field not found");

    }

    @Override
    public BaseResponse<String> delete(String id) {

        DeleteResponse deleteResponse = client.prepareDelete(ElasticSearchIndex.index, ElasticSearchIndex.type, id).get();
        return new BaseResponse<>(deleteResponse.getResult().toString());

    }

    @Override
    public BaseResponse<List<Hotel>> improvedSearch(SearchDTO searchDTO){

        String[] wordList = searchDTO.getTerms().split(" ");
        List <String> searchWords = Arrays.asList(wordList);
        List <String> spellCheckedWords = new ArrayList<>();

        for (String word : searchWords){

            List <String> getWords = checkSpellErrors(word).getData().getSpellCheckWords();
            if (!(getWords.isEmpty())) {
                String suggestedText = getWords.get(0);
                spellCheckedWords.add(suggestedText);
            }
            else spellCheckedWords.add(word);
        }

        String finalSearchString = spellCheckedWords.stream().collect(Collectors.joining(" "));
        SearchQuery searchQuery = searchQueryMaker(finalSearchString, searchDTO);
        List <Hotel> hotels = elasticsearchTemplate.queryForList(searchQuery, Hotel.class);
        return new BaseResponse<>(hotels);

    }

    @Override
    public Single<BaseResponse<List<Hotel>>> searchWithReactive(SearchDTO searchDTO){

        return Single.create(singleEmitter -> {
            try {
                String[] wordList = searchDTO.getTerms().split(" ");
                List<String> searchWords = Arrays.asList(wordList);
                List<String> spellCheckedWords = new ArrayList<>();

                for (String word : searchWords) {

                    List<String> getWords = checkSpellErrors(word).getData().getSpellCheckWords();
                    if (!(getWords.isEmpty())) {
                        String suggestedText = getWords.get(0);
                        spellCheckedWords.add(suggestedText);
                    } else spellCheckedWords.add(word);
                }

                String finalSearchString = spellCheckedWords.stream().collect(Collectors.joining(" "));
                SearchQuery searchQuery = searchQueryMaker(finalSearchString, searchDTO);
                List<Hotel> hotels = elasticsearchTemplate.queryForList(searchQuery, Hotel.class);
                BaseResponse<List<Hotel>> results = new BaseResponse<>(hotels);
                singleEmitter.onSuccess(results);
            } catch (Exception e) {
                log.error("Error getAutocompleteListElasticsearch : {}", e);
                singleEmitter.onError(e);
            }
        });

    }



    @Override
    public BaseResponse<Word> checkSpellErrors(String word){

        List<String> suggestedWords = new ArrayList<>();
        HashMap <Float, String> scoredWords = new HashMap<>();
        Word spellCheckWord = new Word(word, suggestedWords);

        if (word.length() <= 2) return new BaseResponse<>(spellCheckWord);

        SearchSourceBuilder searchNameSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder nameSuggestionBuilder = SuggestBuilders.termSuggestion( SearchFields.name)
                .text(word)
                //.analyzer("synonyms_analyzer")
                .analyzer(ElasticSearchIndex.wordJoinAnalyzer)
                //.minDocFreq(0.1f)
                .suggestMode(TermSuggestionBuilder.SuggestMode.POPULAR)
                .prefixLength(3)
                .maxTermFreq(0.1f)
                .minWordLength(3)
                //.size(1)
                .sort(SortBy.SCORE);


        SuggestBuilder suggestNameBuilder = new SuggestBuilder();
        suggestNameBuilder.addSuggestion(ElasticSearchIndex.nameSuggester, nameSuggestionBuilder);
        searchNameSourceBuilder.suggest(suggestNameBuilder);

        SearchResponse searchNameResponse = config.client()
                .prepareSearch()
                .setIndices(ElasticSearchIndex.index).setTypes(ElasticSearchIndex.type)
                .suggest(suggestNameBuilder)
                .execute()
                .actionGet();
        Suggest nameSuggestions = searchNameResponse.getSuggest();

        TermSuggestion termNameSuggestion = nameSuggestions.getSuggestion(ElasticSearchIndex.nameSuggester);
        for (TermSuggestion.Entry entry: termNameSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option: entry) {

                scoredWords.put(option.getScore(), option.getText().string());

            }

        }

        SearchSourceBuilder searchLocationSourceBuilder = new SearchSourceBuilder();
        SuggestionBuilder locationNameSuggestionBuilder = SuggestBuilders.termSuggestion( SearchFields.locationName)
                .text(word)
                //.analyzer("synonyms_analyzer")
                .analyzer(ElasticSearchIndex.wordJoinAnalyzer)
                //.minDocFreq(0.1f)
                .suggestMode(TermSuggestionBuilder.SuggestMode.POPULAR)
                .minWordLength(3)
                .maxTermFreq(0.1f)
                .prefixLength(3)
                //.size(1)
                .sort(SortBy.SCORE);


        SuggestBuilder suggestLocationBuilder = new SuggestBuilder();
        suggestLocationBuilder.addSuggestion(ElasticSearchIndex.locationNameSuggester, locationNameSuggestionBuilder);
        searchLocationSourceBuilder.suggest(suggestLocationBuilder);

        SearchResponse searchLocationResponse = config.client()
                .prepareSearch()
                .setIndices(ElasticSearchIndex.index).setTypes(ElasticSearchIndex.type)
                .suggest(suggestLocationBuilder)
                .execute()
                .actionGet();
        Suggest locationSuggestions = searchLocationResponse.getSuggest();

        TermSuggestion termLocationSuggestion = locationSuggestions.getSuggestion(ElasticSearchIndex.locationNameSuggester);
        for (TermSuggestion.Entry entry: termLocationSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option: entry) {

                scoredWords.put(option.getScore(), option.getText().string());

            }

        }

        LinkedHashMap <Float, String> sortedScoredMap = new LinkedHashMap<>();

        scoredWords.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedScoredMap.put(x.getKey(), x.getValue()));

        suggestedWords.addAll(sortedScoredMap.values().stream().collect(Collectors.toList()));

        spellCheckWord.setSpellCheckWords(suggestedWords);
        return new BaseResponse<>(spellCheckWord);

    }

}
