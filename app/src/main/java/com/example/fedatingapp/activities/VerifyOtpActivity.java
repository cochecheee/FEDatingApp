package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class VerifyOtpActivity extends AppCompatActivity {
    // Khai báo Views
    private TextView textViewVerifyInstruction;
    private TextInputLayout textInputLayoutOtp;
    private TextInputEditText editTextOtp;
    private MaterialButton buttonVerifyOtp;
    private TextView textViewResendOtp;
    private TextView textViewChangeIdentifier; // Link đổi email

    // Khai báo biến
    private ApiService apiService;
    private String userEmail; // Để lưu email nhận từ Intent
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_otp);

        apiService = RetrofitClient.getApiService();

        // Ánh xạ Views
        textViewVerifyInstruction = findViewById(R.id.textViewVerifyInstruction);
        textInputLayoutOtp = findViewById(R.id.textInputLayoutOtp);
        editTextOtp = findViewById(R.id.editTextOtp);
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);
        textViewResendOtp = findViewById(R.id.textViewResendOtp);
        textViewChangeIdentifier = findViewById(R.id.textViewChangeIdentifier);

        // Lấy email từ Intent gửi từ RegisterActivity
        userEmail = getIntent().getStringExtra("email");

        // Hiển thị email trên TextView hướng dẫn (nếu email tồn tại)
        if (userEmail != null && !userEmail.isEmpty()) {
            textViewVerifyInstruction.setText(getString(R.string.verify_otp_instruction_dynamic, userEmail));
            // Cần tạo string resource "verify_otp_instruction_dynamic"
            // Ví dụ: <string name="verify_otp_instruction_dynamic">Nhập mã gồm 6 chữ số đã gửi đến %s</string>
        } else {
            // Xử lý trường hợp không nhận được email (ví dụ: quay lại màn hình trước)
            Toast.makeText(this, "Lỗi: Không nhận được thông tin email.", Toast.LENGTH_LONG).show();
            finish(); // Đóng activity này
            return; // Dừng thực thi onCreate
        }
        // Xử lý sự kiện click nút Xác thực
        buttonVerifyOtp.setOnClickListener(v -> {
            verifyOtp();
        });

        // Xử lý sự kiện click link Gửi lại mã OTP
        textViewResendOtp.setOnClickListener(v -> {
            resendOtp();
        });

        // Xử lý sự kiện click link Thay đổi email
        textViewChangeIdentifier.setOnClickListener(v -> {
            // Quay lại màn hình RegisterActivity
            // Có thể dùng finish() hoặc tạo Intent mới tùy thuộc vào cách quản lý back stack
            finish(); // Đơn giản là đóng màn hình hiện tại để quay lại màn hình trước đó (Register)
        });
    }

    private void verifyOtp() {
        String otp = editTextOtp.getText().toString().trim();

        // Validate OTP
        if (TextUtils.isEmpty(otp)) {
            textInputLayoutOtp.setError(getString(R.string.error_otp_required)); // Tạo string này
            return;
        }
        if (otp.length() != 6) {
            textInputLayoutOtp.setError(getString(R.string.error_otp_length)); // Tạo string này
            return;
        }
        // Xóa lỗi cũ
        textInputLayoutOtp.setError(null);

        // Gọi
        apiService.verifyRegistrationOtp(userEmail,otp).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {

                        Toast.makeText(VerifyOtpActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Chuyển sang màn hình SetPasswordActivity
                        Intent intent = new Intent(VerifyOtpActivity.this, SetPasswordActivity.class); // <<== TẠO ACTIVITY NÀY
                        intent.putExtra("email", userEmail);
                        startActivity(intent);
                        // Kết thúc cả Register và VerifyOtp để không back lại được
                        // (Cần cơ chế quản lý activity tốt hơn nếu muốn luồng phức tạp)
                        setResult(RESULT_OK); // Đánh dấu thành công để RegisterActivity có thể finish() nếu cần
                        finish();

                    } else {
                        // API trả về thành công nhưng logic backend báo lỗi (OTP sai/hết hạn)
                        Toast.makeText(VerifyOtpActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        // Có thể clear ô OTP để người dùng nhập lại
                        editTextOtp.setText("");
                    }
                } else {
                    // Lỗi HTTP hoặc API trả về lỗi không theo cấu trúc ApiResponse
                    String errorMessage = "Xác thực OTP thất bại.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = "Lỗi " + response.code() + ": " + response.message();
                            // Parse error body nếu cần
                        } catch (Exception e) { }
                    }
                    Toast.makeText(VerifyOtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });

    }

    private void resendOtp() {
        String otp = editTextOtp.getText().toString().trim();
        apiService.verifyRegistrationOtp(userEmail,otp).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {

                        Toast.makeText(VerifyOtpActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Chuyển sang màn hình SetPasswordActivity
                        Intent intent = new Intent(VerifyOtpActivity.this, SetPasswordActivity.class); // <<== TẠO ACTIVITY NÀY
                        intent.putExtra("email", userEmail);
                        startActivity(intent);
                        // Kết thúc cả Register và VerifyOtp để không back lại được
                        // (Cần cơ chế quản lý activity tốt hơn nếu muốn luồng phức tạp)
                        setResult(RESULT_OK); // Đánh dấu thành công để RegisterActivity có thể finish() nếu cần
                        finish();

                    } else {
                        // API trả về thành công nhưng logic backend báo lỗi (OTP sai/hết hạn)
                        Toast.makeText(VerifyOtpActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                        // Có thể clear ô OTP để người dùng nhập lại
                        editTextOtp.setText("");
                    }
                } else {
                    // Lỗi HTTP hoặc API trả về lỗi không theo cấu trúc ApiResponse
                    String errorMessage = "Xác thực OTP thất bại.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = "Lỗi " + response.code() + ": " + response.message();
                            // Parse error body nếu cần
                        } catch (Exception e) { }
                    }
                    Toast.makeText(VerifyOtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}