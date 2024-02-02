package org.mojodojocasahouse.extra.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Getter
@Setter
@ConfigurationProperties(prefix = "gcp.firebase")
public class FirebaseProperties {
    // Base64 encoded firebase-service-account.json
    private String serviceAccount;
}
