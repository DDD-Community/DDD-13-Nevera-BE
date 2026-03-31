package com.example.nevera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_auth")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "auth_code", nullable = false)
    private String authCode;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public EmailAuth(String email, String authCode, LocalDateTime expirationTime) {
        this.email = email;
        this.authCode = authCode;
        this.expirationTime = expirationTime;
        this.isVerified = false;
        this.createdAt = LocalDateTime.now();
    }

    public void updateCode(String newCode, LocalDateTime newExpirationTime) {
        this.authCode = newCode;
        this.expirationTime = newExpirationTime;
        this.isVerified = false;
    }


    public void markAsVerified() {
        this.isVerified = true;
    }
}