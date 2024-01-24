package org.mojodojocasahouse.extra.dto.requests;

import lombok.Data;

@Data
public class DeviceRegisteringRequest {

    private String token;

    public DeviceRegisteringRequest(String token) {
        this.token = token;
    }

    public DeviceRegisteringRequest() {}
}
