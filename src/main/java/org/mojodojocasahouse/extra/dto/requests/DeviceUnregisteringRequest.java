package org.mojodojocasahouse.extra.dto.requests;

import lombok.Data;

@Data
public class DeviceUnregisteringRequest {

    private String token; // Token can be null when unregistering

    public DeviceUnregisteringRequest(String token) {
        this.token = token;
    }

    public DeviceUnregisteringRequest() {}
}
