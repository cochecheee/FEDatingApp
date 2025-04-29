package com.example.fedatingapp.api;

import com.example.fedatingapp.api.request.LoginRequest;
import com.example.fedatingapp.api.response.ApiResponse;
import com.example.fedatingapp.api.response.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    //GET

    //POST
    // Định nghĩa endpoint login với @Field
    @Headers({"Accept: application/json"}) // Content-Type sẽ là application/x-www-form-urlencoded do @FormUrlEncoded
    @FormUrlEncoded // <<== BẮT BUỘC: Gửi dữ liệu dưới dạng form-url-encoded
    @POST("auth/login/email") // <<== Đảm bảo đường dẫn này khớp với Controller (bao gồm /api/auth nếu cần)
    Call<ApiResponse<AuthResponse>> loginByEmail(
            @Field("email") String email,        // <<== Sử dụng @Field cho từng tham số
            @Field("password") String password
    );
    @Headers({"Accept: application/json"})
    // Không cần @FormUrlEncoded nếu dùng @Query cho POST (ít phổ biến)
    // Hoặc nếu backend thực sự nhận GET thì dùng @GET
    @POST("auth/register/request-otp") // Đảm bảo đường dẫn khớp
    Call<ApiResponse<String>> requestRegistrationOtp(@Query("email") String email);
    // --- Endpoint Verify OTP (Dùng @Body) ---
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("auth/register/verify-otp") // Đảm bảo đường dẫn khớp backend
    Call<ApiResponse<String>> verifyRegistrationOtp(@Query("email") String email, @Query("otpCode") String otpCode);
    // --- Endpoint Set Password (Dùng @Field) ---
    @Headers({"Accept: application/json"})
    @FormUrlEncoded // Gửi form-url-encoded
    @POST("auth/register/set-password") // Đảm bảo đường dẫn khớp backend
    Call<ApiResponse<AuthResponse>> setPassword(
                                                 @Field("email") String email,
                                                 @Field("password") String password,
                                                 @Field("confirmedPassword") String confirmedPassword
    );

    //PUT


    //DELETE
}
