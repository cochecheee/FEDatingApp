package com.example.fedatingapp.api;

import android.content.Context;

import com.example.fedatingapp.utils.AuthInterceptor;
import com.mindorks.placeholderview.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static OkHttpClient okHttpClient = null; // Lưu trữ OkHttpClient để tái sử dụng
    private static final String BASE_URL = "http://192.168.0.139:8080/api/";

    public static Retrofit getRetrofit() {
//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(chain -> {
//                    Request newRequest = chain.request().newBuilder()
//                            .addHeader("Authorization", "Bearer " + authToken)
//                            .build();
//                    return chain.proceed(newRequest);
//                })
//                .build();

        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    /**
     * Lấy instance Retrofit đã được cấu hình.
     * Phương thức này là thread-safe nhờ vào cách kiểm tra và khởi tạo.
     *
     * @param context Context cần thiết để khởi tạo AuthInterceptor (nên dùng Application Context).
     * @return Instance Retrofit đã cấu hình.
     */
    public static Retrofit getRetrofit(Context context) {
        // Double-checked locking pattern để đảm bảo thread-safety và hiệu năng
        if (retrofit == null) {
            synchronized (RetrofitClient.class) {
                if (retrofit == null) {
                    // Khởi tạo OkHttpClient nếu chưa có
                    if (okHttpClient == null) {
                        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS) // Thời gian chờ kết nối
                                .readTimeout(45, TimeUnit.SECONDS)    // Thời gian chờ đọc response
                                .writeTimeout(45, TimeUnit.SECONDS);  // Thời gian chờ ghi request

                        // Thêm AuthInterceptor để tự động gắn token
                        // Truyền Application Context để tránh memory leak
                        httpClientBuilder.addInterceptor(new AuthInterceptor(context.getApplicationContext()));

                        // Thêm Logging Interceptor chỉ khi ở chế độ DEBUG
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                            httpClientBuilder.addInterceptor(loggingInterceptor);
                        }

                        okHttpClient = httpClientBuilder.build();
                    }

                    // Khởi tạo Retrofit
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(okHttpClient) // ** Sử dụng OkHttpClient đã có Interceptor **
                            .addConverterFactory(GsonConverterFactory.create()) // Sử dụng Gson để parse JSON
                            .build();
                }
            }
        }
        return retrofit;
    }
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }
    public static ApiService getApiService(Context context) {
        return getRetrofit(context).create(ApiService.class);
    }
}