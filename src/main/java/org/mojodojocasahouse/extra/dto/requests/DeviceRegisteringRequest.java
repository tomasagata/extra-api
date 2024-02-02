package org.mojodojocasahouse.extra.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRegisteringRequest {

    @NotBlank(message = "Token is mandatory")
    private String token;

    public DeviceRegisteringRequest(String token) {
        this.token = token;
    }

    public DeviceRegisteringRequest() {}
}
