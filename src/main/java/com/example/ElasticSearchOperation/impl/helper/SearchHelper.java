package com.example.ElasticSearchOperation.impl.helper;

import com.example.ElasticSearchOperation.config.ElasticConfig;
import com.example.ElasticSearchOperation.dto.SearchDTO;
import com.example.ElasticSearchOperation.impl.constants.ElasticSearchIndex;
import com.example.ElasticSearchOperation.impl.constants.SearchFields;
import com.example.ElasticSearchOperation.model.Hotel;
import com.example.ElasticSearchOperation.model.response.BaseResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.util.List;


public class SearchHelper {


    public static SearchQuery searchQueryMaker(String terms, SearchDTO searchDTO) {

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(terms)
                        .field(SearchFields.name, 1.0f)
                        .field(SearchFields.locationName, 5.0f)
                        .field(SearchFields.type, 2.0f)
                        .fuzziness(Fuzziness.AUTO)
                        .prefixLength(3)
                        .minimumShouldMatch(searchDTO.getMinMatchCriteria())
                        .tieBreaker(0.5F)
                        .analyzer(ElasticSearchIndex.synonymsAnalyzer)
                        .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS))
                .withSort(SortBuilders.fieldSort(SearchFields.nearbyHotel).order(SortOrder.DESC))
                .build();

        return searchQuery;


    }

}
