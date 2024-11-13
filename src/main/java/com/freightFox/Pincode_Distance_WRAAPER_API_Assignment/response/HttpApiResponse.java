package com.freightFox.Pincode_Distance_WRAAPER_API_Assignment.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HttpApiResponse {
    public String timeStamp;
    public int statusCode;
    public String message;
    public String error;
    public String reason;
    public String developerMessage;
    public Boolean success;
    public Map<?,?> data;



    public static HttpApiResponse getExceptionHttpApiResponse(String message,Map<?,?> item,
                                                              int statusCode,String err){
        return  HttpApiResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(item)
                .message(message)
                .success(false)
                .statusCode(statusCode)
                .error(err)
                .build();
    }


    public static HttpApiResponse getSuccessHttpApiResponse(String message, Map<?,?> item,
                                                            int statusCode){
        return  HttpApiResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(item)
                .message(message)
                .success(true)
                .statusCode(statusCode)
                .build();
    }

}