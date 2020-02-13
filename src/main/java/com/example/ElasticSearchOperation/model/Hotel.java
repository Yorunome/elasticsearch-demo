package com.example.ElasticSearchOperation.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "autocomplete-v5-1")
public class Hotel {

    @Id
    private String id;
    private boolean active;
    private String locationName;
    private String name;
    private Integer nearbyHotel;
    private String path;
    private String type;

}
