package com.example.nevera.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "{validation.auth.email.not_blank}")
        @Email(message = "{validation.auth.email.format}")
        String email,

        @NotBlank(message = "{validation.auth.password.not_blank}")
        String password
) {}