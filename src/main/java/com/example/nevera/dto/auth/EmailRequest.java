package com.example.nevera.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        String email
) {


}
