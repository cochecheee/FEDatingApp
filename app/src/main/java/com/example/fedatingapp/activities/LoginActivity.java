package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.AuthResponse;
import com.example.fedatingapp.api.RetrofitClient;

import com.example.fedatingapp.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    // Khai bao
    private TextView textViewRegister;
    private TextView textViewForgotPassword;
    private TextInputLayout textInputLayoutEmail;
    private TextInputEditText editTextEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText editTextPassword;
    private MaterialButton buttonLoginEmailPassword;
    private ApiService apiService; // Khai báo ApiService
    private TokenManager tokenManager; // Để lưu token

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this); // Khởi tạo TokenManager

        // Ánh xạ View
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLoginEmailPassword = findViewById(R.id.buttonLoginEmailPassword);
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);

        buttonLoginEmailPassword.setOnClickListener(v -> {
            loginUser();
        });

        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        textViewForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void logout() {

    }

    private void loginUser() {
        // Xóa lỗi cũ (nếu có)
        textInputLayoutEmail.setError(null);
        textInputLayoutPassword.setError(null);

        // Validate input cơ bản
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Có thể thêm validate độ dài mật khẩu nếu muốn

        // Tạo đối tượng request
//        LoginRequest loginRequest = new LoginRequest(email, password);

        // Gọi API
        apiService.loginByEmail(email,password).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Đăng nhập thành công
                        AuthResponse authData = apiResponse.getData();
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // === LƯU TOKEN ===
                        tokenManager.saveTokens(authData.getToken());
                        // ==================

                        // Chuyển sang màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        // Xóa các activity cũ khỏi stack để không back lại màn hình login
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // Kết thúc LoginActivity

                    } else {
                        // API trả về thành công nhưng logic backend báo lỗi (ví dụ: sai mật khẩu)
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Lỗi HTTP (4xx, 5xx)
                    String errorMessage = "Đăng nhập thất bại. Vui lòng thử lại.";
                    if (response.errorBody() != null) {
                        try {
                            // Cố gắng parse lỗi từ server nếu có
                            // Bạn cần điều chỉnh cách parse này tùy thuộc cấu trúc lỗi API trả về
                            // Ví dụ đơn giản là đọc chuỗi lỗi:
                            errorMessage = response.errorBody().string();
                            // Hoặc parse thành ApiResponse nếu lỗi cũng dùng cấu trúc đó
                            // Gson gson = new Gson();
                            // Type type = new TypeToken<ApiResponse<Object>>() {}.getType();
                            // ApiResponse<Object> errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            // if (errorResponse != null && errorResponse.getMessage() != null) {
                            //    errorMessage = errorResponse.getMessage();
                            // }
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable throwable) {
                // Lỗi mạng hoặc lỗi khi xử lý request/response
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                throwable.printStackTrace(); // In lỗi ra Logcat để debug
            }
        });
    }


}