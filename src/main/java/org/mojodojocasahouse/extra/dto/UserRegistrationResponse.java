package org.mojodojocasahouse.extra.dto;

import lombok.Data;

@Data
public class UserRegistrationResponse {
    String message;
    String error;
    String reason;

    public UserRegistrationResponse(String message){
        this.message = message;
    }

    public UserRegistrationResponse(String error, String reason){
        this.error = error;
        this.reason = reason;
    }

    public UserRegistrationResponse(){}

}
