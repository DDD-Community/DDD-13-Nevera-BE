package com.example.nevera.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    private final String firebaseCredentials;

    public FirebaseConfig(@Value("${firebase.credentials}") String firebaseCredentials) {
        this.firebaseCredentials = firebaseCredentials;
    }

    @PostConstruct
    public void firebaseApp() throws IOException {
        byte[] decoded = Base64.getDecoder().decode(firebaseCredentials);
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                new ByteArrayInputStream(decoded)
        );
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}