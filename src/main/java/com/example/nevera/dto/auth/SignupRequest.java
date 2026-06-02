package com.example.nevera.dto.auth;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.common.exception.ErrorCode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank(message = "{validation.auth.email.not_blank}")
        @Email(message = "{validation.auth.email.format}")
        String email,

        @NotBlank(message = "{validation.auth.password.not_blank}")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,20}",
                message = "{validation.auth.password.pattern}")
        String password,

        @NotBlank(message = "{validation.auth.name.not_blank}")
        String name
) {

}