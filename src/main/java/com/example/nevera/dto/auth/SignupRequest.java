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
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
                message = "{validation.auth.password.pattern}")
        String password,



        @NotBlank(message = "이름은 필수 입력입니다.")

        @NotBlank(message = "{validation.auth.name.not_blank}")

        String name
) {

}