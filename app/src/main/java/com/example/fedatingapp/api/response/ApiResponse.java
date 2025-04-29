package com.example.fedatingapp.api.response;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> { // Sử dụng Generic Type <T>

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private T data; // Kiểu dữ liệu của data sẽ thay đổi (AuthResponse, null, ...)

    // Thêm getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    public boolean isSuccess() {
        // Coi status 2xx là thành công (có thể cần điều chỉnh)
        return status >= 200 && status < 300;
    }
}

