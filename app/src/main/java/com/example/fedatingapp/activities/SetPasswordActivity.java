package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.api.AuthResponse;
import com.example.fedatingapp.utils.TokenManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetPasswordActivity extends AppCompatActivity {
    // Khai báo Views
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText editTextPassword;
    private TextInputLayout textInputLayoutConfirmPassword;
    private TextInputEditText editTextConfirmPassword;
    private MaterialButton buttonCompleteRegistration;

    // Khai báo biến
    private ApiService apiService;
    private String userEmail; // Lưu email nhận từ Intent
    private TokenManager tokenManager; // Để lưu token sau khi đăng ký thành công

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_password);

        apiService = RetrofitClient.getApiService();
        tokenManager = new TokenManager(this);

        // Ánh xạ Views
        textInputLayoutPassword = findViewById(R.id.textInputLayoutRegisterPassword);
        editTextPassword = findViewById(R.id.editTextRegisterPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutRegisterConfirmPassword);
        editTextConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword);
        buttonCompleteRegistration = findViewById(R.id.buttonCompleteRegistration);

        // Lấy dữ liệu từ Intent
        userEmail = getIntent().getStringExtra("email");
        // verificationToken = getIntent().getStringExtra("verification_token");

        // Kiểm tra xem có nhận được email không (rất quan trọng vì backend dựa vào nó)
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không có thông tin email để đặt mật khẩu.", Toast.LENGTH_LONG).show();
            // Có thể chuyển về màn hình trước đó hoặc đóng ứng dụng tùy logic
            finish();
            return;
        }

        // Xử lý sự kiện click nút Hoàn tất Đăng ký
        buttonCompleteRegistration.setOnClickListener(v -> {
            completeRegistration();
        });

    }

    private void completeRegistration() {
        // Xóa lỗi cũ
        textInputLayoutPassword.setError(null);
        textInputLayoutConfirmPassword.setError(null);

        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        // Validate mật khẩu
        boolean isValid = true;
        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) { // Ví dụ kiểm tra độ dài tối thiểu
            textInputLayoutPassword.setError(getString(R.string.error_password_length)); // Tạo string này
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            textInputLayoutConfirmPassword.setError(getString(R.string.error_confirm_password_required)); // Tạo string này
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            textInputLayoutConfirmPassword.setError(getString(R.string.error_password_mismatch)); // Tạo string này
            isValid = false;
        }

        if (!isValid) {
            return;
        }
        // Gọi API setPassword (dùng @Field)
        apiService.setPassword(userEmail, password, confirmPassword) // Truyền các tham số
                .enqueue(new Callback<ApiResponse<AuthResponse>>() { // Mong đợi AuthResponse trong data
                    @Override
                    public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<AuthResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                // Đăng ký thành công, nhận được token đăng nhập
                                AuthResponse authData = apiResponse.getData();
                                Toast.makeText(SetPasswordActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                // === LƯU TOKEN ===
                                tokenManager.saveTokens(authData.getToken());
                                // ==================

                                // Chuyển sang màn hình chính
                                Intent intent = new Intent(SetPasswordActivity.this, ProfileActivity.class);
                                // Xóa hết các activity cũ (Login, Register, Verify) khỏi stack
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish(); // Kết thúc SetPasswordActivity

                            } else {
                                // API trả về thành công nhưng logic backend báo lỗi
                                Toast.makeText(SetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Lỗi HTTP
                            String errorMessage = "Đăng ký thất bại.";
                            if (response.errorBody() != null) {
                                try {
                                    errorMessage = "Lỗi " + response.code() + ": " + response.message();
                                    // Parse errorBody nếu cần
                                } catch (Exception e) {
                                }
                            }
                            Toast.makeText(SetPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable throwable) {
                        Toast.makeText(SetPasswordActivity.this, "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        throwable.printStackTrace();

                    }
                });
    }
}