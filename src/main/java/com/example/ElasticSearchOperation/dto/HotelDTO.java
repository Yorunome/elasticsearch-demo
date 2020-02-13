package com.example.ElasticSearchOperation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelDTO {

    private String id;
    private boolean active;
    private String locationName;
    private String name;
    private Integer nearbyHotel;
    private String path;
    private String type;

}
