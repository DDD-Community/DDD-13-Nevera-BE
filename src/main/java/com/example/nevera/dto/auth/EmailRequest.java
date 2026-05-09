package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "{validation.auth.email.not_blank}")
        String email
) {


}
