package com.example.fedatingapp.api.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email") // Đảm bảo tên khớp với JSON key của API
    private String email;

    @SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
