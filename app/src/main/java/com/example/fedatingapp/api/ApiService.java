package com.example.fedatingapp.api;

import com.example.fedatingapp.api.AuthResponse;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.models.MatchFeed;
import com.example.fedatingapp.models.Profile;
import com.example.fedatingapp.models.UserSettings;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    //--------------------------GET
    /**
     * Lấy danh sách thẻ khám phá người dùng.
     * GET /api/users/me/cards
     *
     * @param authToken Token xác thực người dùng.
     */
    @Headers({"Accept: application/json"})
    @GET("users/me/cards")
    Call<ApiResponse<List<Profile>>> getDiscoveryCards(
            @Header("Authorization") String authToken
    );

    /**
     * Lấy cài đặt người dùng.
     * GET /api/users/me/settings
     *
     * @param authToken Token xác thực người dùng.
     */
    @GET("users/me/settings")
    Call<ApiResponse<UserSettings>> getSettings(@Header("Authorization") String authToken);
    /**
     * Lấy người dùng match thành công.
     * GET /api/users/matches
     *
     * @param authToken Token xác thực người dùng.
     */
    @Headers({"Accept: application/json"})
    @GET("users/matches")
    Call<ApiResponse<List<MatchFeed>>> getMatches(@Header("Authorization") String authToken);

    //----------------------------POST
    /**
     * Đăng nhập bằng email và mật khẩu.
     * POST /api/auth/login/email
     *
     * @param email Email người dùng.
     * @param password Mật khẩu người dùng.
     */
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("auth/login/email")
    Call<ApiResponse<AuthResponse>> loginByEmail(
            @Field("email") String email,
            @Field("password") String password
    );

    /**
     * Yêu cầu mã OTP để đăng ký tài khoản.
     * POST /api/auth/register/request-otp
     *
     * @param email Email đăng ký.
     */
    @Headers({"Accept: application/json"})
    @POST("auth/register/request-otp")
    Call<ApiResponse<String>> requestRegistrationOtp(@Query("email") String email);

    /**
     * Xác thực mã OTP khi đăng ký.
     * POST /api/auth/register/verify-otp
     *
     * @param email Email đăng ký.
     * @param otpCode Mã OTP nhận được.
     */
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("auth/register/verify-otp")
    Call<ApiResponse<String>> verifyRegistrationOtp(@Query("email") String email, @Query("otpCode") String otpCode);

    /**
     * Đặt mật khẩu sau khi xác thực OTP thành công.
     * POST /api/auth/register/set-password
     *
     * @param email Email đăng ký.
     * @param password Mật khẩu mới.
     * @param confirmedPassword Xác nhận mật khẩu.
     */
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("auth/register/set-password")
    Call<ApiResponse<AuthResponse>> setPassword(
            @Field("email") String email,
            @Field("password") String password,
            @Field("confirmedPassword") String confirmedPassword
    );

    /**
     * Thích một người dùng khác.
     * POST /api/users/{id}/like
     *
     * @param userId ID của người dùng được thích.
     * @param authToken Token xác thực người dùng.
     */
    @Headers({"Accept: application/json"})
    @POST("users/{id}/like")
    Call<ApiResponse<String>> likeUser(@Path("id") Long userId,
                                       @Header("Authorization") String authToken);

    /**
     * Không thích một người dùng khác.
     * POST /api/users/{id}/dislike
     *
     * @param userId ID của người dùng không được thích.
     * @param authToken Token xác thực người dùng.
     */
    @Headers({"Accept: application/json"})
    @POST("users/{id}/dislike")
    Call<ApiResponse<String>> dislikeUser(@Path("id") Long userId,
                                          @Header("Authorization") String authToken);

    @Headers({"Accept: application/json"})
    @GET("users/{id}/profile")
    Call<Users> profile(@Path("id") Long userId,
                                          @Header("Authorization") String authToken);

    //----------------------------------PUT
    /**
     * Lưu cài đặt người dùng.
     * PUT /api/users/me/settings
     *
     * @param settings Đối tượng cài đặt cần lưu.
     * @param authToken Token xác thực người dùng.
     */
    @Headers({"Accept: application/json"})
    @PUT("users/me/settings")
    Call<ApiResponse<Void>> saveSettings(
            @Body UserSettings settings,
            @Header("Authorization") String authToken
    );
    /**
     * Cập nhật vị trí hiện tại của người dùng.
     * PUT /api/v1/users/me/location?lat=xx.xxx&long=yy.yyy
     * @param latitude Vĩ độ.
     * @param longitude Kinh độ.
     */
    @Headers({"Accept: application/json"}) // Content-Type không cần thiết vì không có body
    @PUT("api/v1/users/me/location") // Endpoint backend
    Call<ApiResponse> updateUserLocation( // Hoặc Call<ApiResponse<Void>> nếu backend trả về ApiResponse
                                   @Query("lat") double latitude,   // ** Dùng @Query **
                                   @Query("long") double longitude,  // ** Dùng @Query **
                                   // Không cần token nếu dùng Interceptor
                                   @Header("Authorization") String authToken // Chỉ thêm nếu không dùng Interceptor
    );
    //DELETE
}