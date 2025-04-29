package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.api.response.ApiResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    // Khai báo View
    private TextInputLayout textInputLayoutEmail; // Đổi tên ID trong XML nếu khác
    private TextInputEditText editTextEmail;    // Đổi tên ID trong XML nếu khác
    private MaterialButton buttonSendOtp;
    private TextView textViewBackToLogin;

    // Khai báo ApiService
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Ánh xạ Views
        // Lưu ý: Đảm bảo ID trong XML khớp với R.id...
        textInputLayoutEmail = findViewById(R.id.textInputLayoutPhone); // !!! ID trong XML đang là textInputLayoutPhone
        editTextEmail = findViewById(R.id.editTextPhone);             // !!! ID trong XML đang là editTextPhone
        buttonSendOtp = findViewById(R.id.buttonSendOtp);
        textViewBackToLogin = findViewById(R.id.textViewBackToLogin);

        // Xử lý sự kiện click link Quay lại Đăng nhập
        textViewBackToLogin.setOnClickListener(v -> {
            // Chuyển về LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng RegisterActivity
        });

        // Xử lý sự kiện click nút Gửi OTP
        buttonSendOtp.setOnClickListener(v -> {
            requestOtp();
        });
    }

    private void requestOtp(){
        // Xóa lỗi cũ
        textInputLayoutEmail.setError(null);
        String email = editTextEmail.getText().toString().trim();

        // Gọi API request OTP
        // Lưu ý: API backend đang dùng @RequestParam("email")
        // Nếu ApiService của bạn định nghĩa theo chuẩn @Body thì cần sửa ApiService hoặc Backend
        // Giả sử ApiService đã định nghĩa đúng với @Query("email") hoặc tương đương cho @RequestParam
        apiService.requestRegistrationOtp(email).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    // API trả về thành công (HTTP 2xx), kiểm tra message từ API
                    Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình VerifyOtpActivity và truyền email
                    Intent intent = new Intent(RegisterActivity.this, VerifyOtpActivity.class); // <<== TẠO ACTIVITY NÀY
                    intent.putExtra("email", email); // Truyền email sang màn hình xác thực
                    startActivity(intent);
                    // Không finish() ở đây, để người dùng có thể back lại đổi email nếu cần

                } else {
                    // Lỗi HTTP hoặc API trả về lỗi logic
                    String errorMessage = "Yêu cầu OTP thất bại."; // Thông báo mặc định
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMessage = response.body().getMessage(); // Lấy lỗi từ API nếu có
                    } else if (response.errorBody() != null) {
                        try {
                            // Cố gắng đọc lỗi chi tiết từ errorBody
                            errorMessage = "Lỗi " + response.code() + ": " + response.message();
                            // Có thể parse errorBody thành ApiResponse nếu cấu trúc lỗi giống
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    } else {
                        errorMessage = "Lỗi không xác định: " + response.code();
                    }
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}