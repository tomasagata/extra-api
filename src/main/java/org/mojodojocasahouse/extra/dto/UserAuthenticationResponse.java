package org.mojodojocasahouse.extra.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class UserAuthenticationResponse {

    String message;

    UUID session;

    public UserAuthenticationResponse(String message) {
        this.message = message;
    }
}