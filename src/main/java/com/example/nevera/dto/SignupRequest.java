package com.example.nevera.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank(message = "이메일은 필수 입력입니다.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
                message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        String password,

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String passwordCheck,

        @NotBlank(message = "이름은 필수 입력입니다.")
        String name
) {
    // 비밀번호 일치 여부를 객체 내부에서 확인하는 로직
    public boolean isPasswordMatch() {
        return password.equals(passwordCheck);
    }
}