package com.example.ElasticSearchOperation.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "autocomplete-v5-4")
public class Hotel {

    @Id
    private String id;
    private boolean active;

    @Field(type = FieldType.Text, analyzer = "word_join_analyzer")
    private String locationName;
    @Field(type = FieldType.Auto, analyzer = "word_join_analyzer")
    private String name;
    private Integer nearbyHotel;
    private String path;
    private String type;

}
