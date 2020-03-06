package com.example.ElasticSearchOperation.model.response;


import com.example.ElasticSearchOperation.impl.constants.ElasticSearchIndex;
import com.example.ElasticSearchOperation.model.vo.SearchResponseVo;
import lombok.*;
import net.karneim.pojobuilder.GeneratePojoBuilder;
import org.springframework.data.elasticsearch.annotations.Document;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Getter
@Setter
@GeneratePojoBuilder
@Document(indexName = ElasticSearchIndex.index)
public class SearchHotelResponse extends SearchResponseVo {
}
