package com.example.fedatingapp.utils;

import androidx.annotation.NonNull;
import android.content.Context;
import android.util.Log; // Import Log

import com.example.fedatingapp.utils.TokenManager; // ** Import TokenManager **
import com.mindorks.placeholderview.BuildConfig;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp Interceptor để tự động thêm header "Authorization: Bearer <token>"
 * vào các request API, lấy token từ TokenManager.
 */
public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor"; // Tag log
    private TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        // Khởi tạo TokenManager với Application Context để tránh leak Activity/Fragment context
        this.tokenManager = new TokenManager(context.getApplicationContext());
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        // Lấy request gốc
        Request originalRequest = chain.request();

        // Lấy access token từ TokenManager
        String accessToken = tokenManager.getAccessToken();

        // Tạo request mới builder
        Request.Builder builder = originalRequest.newBuilder();

        // Chỉ thêm header nếu token hợp lệ
        if (accessToken != null && !accessToken.isEmpty()) {
            String bearerToken = "Bearer " + accessToken;
            builder.header("Authorization", bearerToken);
            Log.d(TAG, "Adding Authorization header for request to: " + originalRequest.url());
            // Chỉ log một phần token khi debug để bảo mật
            if(BuildConfig.DEBUG) { // Kiểm tra build config
                Log.v(TAG, "Token: " + bearerToken.substring(0, Math.min(bearerToken.length(), 15)) + "...");
            }
        } else {
            // Không thêm header nếu không có token
            Log.w(TAG, "No access token found. Request to " + originalRequest.url() + " sent without Authorization header.");
        }

        // Luôn thêm Accept header nếu backend yêu cầu hoặc để đảm bảo nhận JSON
        // Hoặc bạn có thể dùng @Headers trong ApiService interface
        builder.header("Accept", "application/json");


        // Xây dựng request mới
        Request newRequest = builder.build();

        // Thực hiện request và trả về response
        return chain.proceed(newRequest);
    }
}