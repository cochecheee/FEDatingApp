package com.example.fedatingapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.BlockService;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.entities.MatchList;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.utils.TokenManager;

import java.time.LocalDate;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {
    private TokenManager tokenManager;
    private ApiService apiService;
    private BlockService blockService;
    private ImageButton btnBack;
    private ImageView imgProfile;
    private TextView tvNameAge, tvJob, tvLocation, tvDistance, tvAboutMe, tvZodiac, tvEducation, tvLookingFor;
    private Button btnBlock;
    String bearerToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile); // Đổi tên file XML tương ứng

        // Ánh xạ view
        btnBack = findViewById(R.id.btnBack);
        imgProfile = findViewById(R.id.imgProfile);
        tvNameAge = findViewById(R.id.tvNameAge);
        tvJob = findViewById(R.id.tvJob);
        tvLocation = findViewById(R.id.tvLocation);
        tvDistance = findViewById(R.id.tvDistance);
        tvAboutMe = findViewById(R.id.tvAboutMe);
        tvZodiac = findViewById(R.id.tvZodiac);
        tvEducation = findViewById(R.id.tvEducation);
        tvLookingFor = findViewById(R.id.tvLookingFor);
        btnBlock = findViewById(R.id.btnBlock);

        Intent intent = getIntent();
        Long userId = intent.getLongExtra("UserId", 5L);
        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getApiService(this);
        if (apiService == null || tokenManager == null) { // ** Thêm kiểm tra tokenManager **
            Toast.makeText(getApplicationContext(), "Lỗi: ", Toast.LENGTH_LONG).show();
            return;
        }

        // ** Lấy accessToken từ TokenManager **
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Lỗi: ", Toast.LENGTH_LONG).show();
            return;
        }
        bearerToken = "Bearer " + accessToken;
        blockService = new BlockService(bearerToken);

        LoadUserInfo(userId);
        // Xử lý sự kiện
        btnBack.setOnClickListener(v -> finish());

        btnBlock.setOnClickListener(v ->{
            MatchList block = new MatchList();
            block.setUser2(userId);
            block.setStatus("BLOCK");
            blockService.blockUser(block, new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful())
                    {
                        Toast.makeText(UserProfileActivity.this, "Block user success ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable throwable) {

                }
            });
        });
    }
    private void LoadUserInfo(Long userId) {

        apiService.profile(userId,bearerToken).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful())
                {
                    Users user = response.body();
                    // Gán dữ liệu mẫu (hoặc bạn có thể truyền từ Intent nếu cần)
                    tvNameAge.setText(user.getName());
                    tvJob.setText(user.getJob());
                    tvLocation.setText(user.getAddress());
                    tvDistance.setText(user.getAddress());
                    tvAboutMe.setText(user.getBiography());
                    tvZodiac.setText("Cung hoàng đạo: " + user.getZodiacSign());
                    tvEducation.setText(user.getPersonalityType());
                    tvLookingFor.setText(user.getInterests());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Lỗi: ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "Lỗi: "+throwable.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
