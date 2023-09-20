package org.mojodojocasahouse.extra.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserAuthenticationResponse {

    String message;

    Boolean status;

    public UserAuthenticationResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }
}