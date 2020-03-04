package com.example.ElasticSearchOperation.model.response.constants;

public enum ErrorCodes {

    INVALID_SEARCH_TERM;

    public static ErrorCodes fromValue(String value){

        ErrorCodes errorCode = INVALID_SEARCH_TERM;
        return errorCode;
    }

}
