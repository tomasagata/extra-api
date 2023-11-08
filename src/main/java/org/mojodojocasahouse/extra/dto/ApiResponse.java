package org.mojodojocasahouse.extra.dto;

import lombok.Data;

@Data
public class ApiResponse {

    String message;

    Object response;

    public ApiResponse(String message){
        this.message = message;
    }

    public ApiResponse(String message, Object response){
        this.message = message;
        this.response = response;
    }

}
