package com.example.ElasticSearchOperation.impl.constants;

public interface ElasticSearchIndex {

    String index = "autocomplete-v5-6";
    String type = "_doc";
    String wordJoinAnalyzer = "word_join_analyzer";
    String synonymsAnalyzer = "synonyms_analyzer";
    String nameSuggester = "name_suggest";
    String locationNameSuggester = "locationName_suggest";

}
