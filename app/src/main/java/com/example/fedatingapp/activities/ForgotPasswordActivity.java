package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.databinding.ActivityForgotPasswordBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private ActivityForgotPasswordBinding binding; // ** Sử dụng ViewBinding **
    private ApiService apiService;
    private String identifier = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        // ** Khởi tạo ViewBinding **
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nếu dùng ImageView làm nút back:
        binding.imageViewBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        // Khởi tạo ApiService
        apiService = RetrofitClient.getApiService();
        // Thiết lập Listener cho nút gửi
        binding.buttonSendResetCode.setOnClickListener(v -> attemptSendOtp());
    }
    private void attemptSendOtp() {
        // Lấy identifier từ TextInputEditText

        if (binding.textInputLayoutIdentifier.getEditText() != null) {
            identifier = binding.textInputLayoutIdentifier.getEditText().getText().toString().trim();
        }

        // Validate input
        if (TextUtils.isEmpty(identifier)) {
            binding.textInputLayoutIdentifier.setError("Vui lòng nhập email hoặc số điện thoại");
            binding.textInputLayoutIdentifier.requestFocus();
            return;
        } else {
            binding.textInputLayoutIdentifier.setError(null); // Xóa lỗi nếu đã nhập
        }
//        RequestResetPasswordRequest requestDto = new RequestResetPasswordRequest(identifier);
        Log.d(TAG, "Requesting password reset OTP for: " + identifier);

        apiService.requestPasswordResetOtp(identifier).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Request OTP processed (backend always returns OK). Message: " + response.body().getMessage());
                    Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("identifier", identifier);
                    startActivity(intent);
                } else {
                    String errorMsg = "Lỗi gửi yêu cầu: " + response.code();
                    Log.e(TAG, errorMsg + " - " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorBodyStr = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBodyStr);
                        } catch (Exception e) { /* ... */ }
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                };
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Network error requesting OTP", t);
                Toast.makeText(ForgotPasswordActivity.this, "Lỗi mạng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}