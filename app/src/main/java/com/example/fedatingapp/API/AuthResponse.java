package com.example.fedatingapp.api;

public class AuthResponse {
    public String getToken() {
        return token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    private String token;
    private long expiresIn;
}
