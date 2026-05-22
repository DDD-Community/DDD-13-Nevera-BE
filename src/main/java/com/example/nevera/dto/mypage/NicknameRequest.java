package com.example.nevera.dto.mypage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NicknameRequest(
        @NotBlank(message = "{validation.mypage.nickname.not_blank}")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,6}$", message = "{validation.mypage.nickname.pattern}")
        String nickname
) {}
