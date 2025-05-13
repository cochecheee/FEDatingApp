package com.example.fedatingapp.api.request;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    @SerializedName("identifier")
    private String identifier; // Email hoặc SĐT đã dùng để yêu cầu OTP
    @SerializedName("otp")
    private String otp;
    @SerializedName("new_password")
    private String newPassword;
    @SerializedName("confirm_password")
    private String confirmPassword;

    public ResetPasswordRequest(String identifier, String otp, String newPassword, String confirmPassword) {
        this.identifier = identifier;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
