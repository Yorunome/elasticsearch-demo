package com.example.ElasticSearchOperation.repository;

import com.example.ElasticSearchOperation.model.Hotel;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends ElasticsearchCrudRepository <Hotel, String> {
}
