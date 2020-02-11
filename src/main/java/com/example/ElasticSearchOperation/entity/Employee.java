package com.example.ElasticSearchOperation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "users", type = "employee")
public class Employee {

    @Id
    private String userId;
    private String name;
    private Date creationDate;
    private String description;

    @Field(type = FieldType.Object, includeInParent = true)
    private Map<String, String> userSettings = new HashMap<>();

}
