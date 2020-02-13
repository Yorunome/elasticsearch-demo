package com.example.ElasticSearchOperation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDTO {

    private String id;
    private String fieldName;
    private String fieldValue;

}
