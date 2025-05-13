package com.example.fedatingapp.api.request;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordRequest {
    // Giữ nguyên @SerializedName nếu tên key JSON khác tên biến Java
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("otp")
    private String otp;
    @SerializedName("newPassword")
    private String newPassword;
    @SerializedName("confirmPassword")
    private String confirmPassword;

    // ** 1. THÊM CONSTRUCTOR KHÔNG THAM SỐ (NO-ARGS CONSTRUCTOR) **
    public ResetPasswordRequest() {
    }

    // 2. Constructor có tham số (Vẫn có thể giữ lại nếu bạn dùng nó khi tạo đối tượng)
    public ResetPasswordRequest(String identifier, String otp, String newPassword, String confirmPassword) {
        this.identifier = identifier;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    // ** 3. THÊM SETTERS CÔNG KHAI CHO TẤT CẢ CÁC TRƯỜNG **
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Getters (cũng nên có)
    public String getIdentifier() { return identifier; }
    public String getOtp() { return otp; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
}