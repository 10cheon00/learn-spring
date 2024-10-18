package com.learn.security.dto;

public class AuthResponseDto {
    private final String accessToken;
    private String tokenType;

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return this.accessToken;
    }
}
