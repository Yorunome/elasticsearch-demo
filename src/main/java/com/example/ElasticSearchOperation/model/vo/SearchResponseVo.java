package com.example.ElasticSearchOperation.model.vo;


import lombok.*;
import net.karneim.pojobuilder.GeneratePojoBuilder;


@Data
@ToString
@Getter
@Setter
@EqualsAndHashCode
@GeneratePojoBuilder
public class SearchResponseVo {

    private String id;
    private String name;
    private String locationName;
    private String type;
    private int nearbyHotel;
    private String path;

}
