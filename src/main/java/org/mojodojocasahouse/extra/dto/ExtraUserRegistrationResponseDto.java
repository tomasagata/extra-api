package org.mojodojocasahouse.extra.dto;

import lombok.Data;

@Data
public class ExtraUserRegistrationResponseDto {
    String message;
    String error;
    String reason;

    public ExtraUserRegistrationResponseDto(String message){
        this.message = message;
    }

    public ExtraUserRegistrationResponseDto(String error, String reason){
        this.error = error;
        this.reason = reason;
    }

    public ExtraUserRegistrationResponseDto(){}

}
