package com.example.ElasticSearchOperation.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.MappedSuperclass;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public class BaseResponse <T> {

    private List<ErrorResponse> errors;
    private T data;

    public BaseResponse(T data) { this.data = data; }
    public BaseResponse(ErrorResponse errorResponse){
        errors = new ArrayList<>();
        errors.add(errorResponse);
    }

    public BaseResponse(List<ErrorResponse> errors) { this.errors = errors; }

}
