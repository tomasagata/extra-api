package org.mojodojocasahouse.extra.dto;

import lombok.Data;

@Data
public class UsuarioRegistroResponseDto {
    String message;
    String error;
    String reason;

    public UsuarioRegistroResponseDto(String message){
        this.message = message;
    }

    public UsuarioRegistroResponseDto(String error, String reason){
        this.error = error;
        this.reason = reason;
    }
}
