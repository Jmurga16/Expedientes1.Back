package com.gestionexpedientes.security.dto;

public class JwtTokenDto {
    private String token;

    public JwtTokenDto() {
    }

    public JwtTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
