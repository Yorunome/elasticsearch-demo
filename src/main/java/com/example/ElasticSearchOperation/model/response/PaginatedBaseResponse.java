package com.example.ElasticSearchOperation.model.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PaginatedBaseResponse<T> extends BaseResponse {

    private Long totalHits;
    private Integer pageSize;

    private PaginatedBaseResponse(T data, Long totalHits, Integer pageSize) {

        super(data);
        this.totalHits = totalHits;
        this.pageSize = pageSize;
    }

}
