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
@Document(indexName = "autocomplete-v5-6", type = "_doc")
public class Hotel {

    @Id
    @Field(type = FieldType.Auto)
    private String id;

    @Field(type = FieldType.Boolean)
    private boolean active;

    @Field(type = FieldType.Text, analyzer = "word_join_analyzer")
    private String locationName;

    @Field(type = FieldType.Auto, analyzer = "word_join_analyzer")
    private String name;

    @Field(type = FieldType.Integer)
    private Integer nearbyHotel;

    @Field(type = FieldType.Keyword)
    private String path;

    @Field(type = FieldType.Text)
    private String type;

}
