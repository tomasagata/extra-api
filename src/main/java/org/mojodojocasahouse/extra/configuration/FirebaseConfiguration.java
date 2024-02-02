package org.mojodojocasahouse.extra.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseConfiguration {

    private final FirebaseProperties firebaseProperties;

    public FirebaseConfiguration(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials googleCredentials) {
        FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();

        return FirebaseApp.initializeApp(firebaseOptions);
    }

    @Bean
    GoogleCredentials googleCredentials() {
        try {
            if (firebaseProperties.getProjectId() != null) {

                RSAPrivateKey privateKey = RsaKeyConverters
                        .pkcs8()
                        .convert(new ByteArrayInputStream(firebaseProperties.getPrivateKey().getBytes()));

                return ServiceAccountCredentials.newBuilder()
                        .setProjectId(firebaseProperties.getProjectId())
                        .setPrivateKeyId(firebaseProperties.getPrivateKeyId())
                        .setPrivateKey(privateKey)
                        .setClientEmail(firebaseProperties.getClientEmail())
                        .setClientId(firebaseProperties.getClientId())
                        .build();
            }
            else {
                // Use standard credentials chain. Useful when running inside GKE
                return GoogleCredentials.getApplicationDefault();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
