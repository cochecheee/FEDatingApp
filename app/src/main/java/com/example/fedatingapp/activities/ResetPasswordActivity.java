package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.api.request.ResetPasswordRequest;
import com.example.fedatingapp.databinding.ActivityResetPasswordBinding;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {
    // Khai báo
    private static final String TAG = "ResetPasswordActivity";
    private ActivityResetPasswordBinding binding;
    private ApiService apiService;
    private String identifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Lấy identifier từ Intent
        identifier = getIntent().getStringExtra("identifier");
        if (identifier == null || identifier.isEmpty()) {
            Log.e(TAG, "Identifier (email/phone) is missing. Cannot proceed.");
            Toast.makeText(this, "Lỗi: Thiếu thông tin định danh.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.d(TAG, "Identifier for password reset: " + identifier);

        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();

        // Thiết lập Listener cho các nút
        binding.buttonConfirmReset.setOnClickListener(v -> attemptResetPassword());
        binding.textViewResetResendOtp.setOnClickListener(v -> resendOtp());
    }

    private void attemptResetPassword() {
        String otp = binding.editTextResetOtp.getText().toString().trim();
        String newPassword = binding.editTextNewPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        // --- Validate Input ---
        if (TextUtils.isEmpty(otp)) {
            binding.textInputLayoutResetOtp.setError("Vui lòng nhập mã OTP"); // Giả sử có TextInputLayout
            binding.editTextResetOtp.requestFocus();
            return;
        } else {
            binding.textInputLayoutResetOtp.setError(null);
        }

        if (TextUtils.isEmpty(newPassword)) {
            binding.textInputLayoutNewPassword.setError("Vui lòng nhập mật khẩu mới");
            binding.editTextNewPassword.requestFocus();
            return;
        } else if (newPassword.length() < 6) { // Ví dụ: yêu cầu tối thiểu 6 ký tự
            binding.textInputLayoutNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            binding.editTextNewPassword.requestFocus();
            return;
        } else {
            binding.textInputLayoutNewPassword.setError(null);
        }


        if (TextUtils.isEmpty(confirmPassword)) {
            binding.textInputLayoutConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            binding.editTextConfirmPassword.requestFocus();
            return;
        } else {
            binding.textInputLayoutConfirmPassword.setError(null);
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.textInputLayoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            binding.editTextConfirmPassword.requestFocus();
            return;
        } else {
            binding.textInputLayoutConfirmPassword.setError(null);
        }

        // --- Input hợp lệ, gọi API ---
        if (apiService == null) {
            Toast.makeText(this, "Lỗi dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo DTO
        ResetPasswordRequest requestDto = new ResetPasswordRequest();
        requestDto.setIdentifier(identifier);
        requestDto.setOtp(otp);
        requestDto.setNewPassword(newPassword);
        requestDto.setConfirmPassword(confirmPassword);

        apiService.resetPassword(requestDto).enqueue(new Callback<ApiResponse<ResetPasswordRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ResetPasswordRequest>> call, Response<ApiResponse<ResetPasswordRequest>> response) {
                if (isFinishing() || isDestroyed()) return; // Kiểm tra Activity

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ResetPasswordRequest> apiResponse = response.body();
                    Log.i(TAG, "Password reset successful: " + apiResponse.getMessage());
                    Toast.makeText(ResetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();

                    // Chuyển về màn hình Login
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý lỗi từ server (vd: OTP sai, identifier không tồn tại)
                    String errorMessage = "Lỗi đặt lại mật khẩu (" + response.code() + ")";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorJson);
                            ApiResponse<?> errorApiResponse = new Gson().fromJson(errorJson, ApiResponse.class);
                            if (errorApiResponse != null && errorApiResponse.getMessage() != null) {
                                errorMessage = errorApiResponse.getMessage();
                            } else if (response.message() != null && !response.message().isEmpty()){
                                errorMessage = response.message();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing error body", e);
                        }
                    } else if (response.message() != null && !response.message().isEmpty()){
                        errorMessage = response.message();
                    }
                    Log.e(TAG, "Error resetting password: " + errorMessage);
                    Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    // Không điều hướng nếu lỗi, để user thử lại
                }

            }

            @Override
            public void onFailure(Call<ApiResponse<ResetPasswordRequest>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Log.e(TAG, "Network error resetting password", t);
                Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void resendOtp() {
        if (apiService == null) {
            Toast.makeText(this, "Lỗi dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Requesting to resend OTP for: " + identifier);
        apiService.requestRegistrationOtp(identifier).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage() != null ? response.body().getMessage() : "OTP đã được gửi lại.";
                    Log.i(TAG, "Resend OTP API success: " + message);
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    // Có thể thêm logic đếm ngược thời gian cho phép gửi lại OTP
                } else {
                    String errorMsg = "Lỗi gửi lại OTP: " + response.code();
                    Log.e(TAG, errorMsg + " - " + response.message());
                    Toast.makeText(ResetPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Log.e(TAG, "Network error resending OTP", t);
                Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm điều hướng về màn hình Login
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class); // ** Thay LoginActivity **
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity(); // Đóng tất cả các activity của task này
    }
}