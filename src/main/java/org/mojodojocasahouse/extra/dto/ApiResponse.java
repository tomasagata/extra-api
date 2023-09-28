package org.mojodojocasahouse.extra.dto;

import lombok.Data;

@Data
public class ApiResponse {

    String message;

    public ApiResponse(String message){
        this.message = message;
    }

}
