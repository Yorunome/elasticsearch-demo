package com.example.ElasticSearchOperation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String userId;

    private String name;

    private Date creationDate = new Date();

    private Map<String, String> userSettings = new HashMap<>();

}