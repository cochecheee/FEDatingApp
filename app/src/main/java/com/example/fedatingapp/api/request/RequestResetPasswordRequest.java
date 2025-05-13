package com.example.fedatingapp.api.request;

import com.google.gson.annotations.SerializedName;

public class RequestResetPasswordRequest {
    @SerializedName("identifier") // Khớp với backend
    private String identifier; // Có thể là email hoặc SĐT

    public RequestResetPasswordRequest(String identifier) {
        this.identifier = identifier;
    }
}
