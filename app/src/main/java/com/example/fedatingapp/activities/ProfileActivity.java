package com.example.fedatingapp.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.adapters.ImageAdapter;
import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.utils.TokenManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements ImageAdapter.OnImageDeleteListener{

    private UserService userService;
    private TokenManager tokenManager;
    private Long userId;
    private Users user;
    private List<Image> userImages = new ArrayList<>();

    private ImageButton imbtn_thoat;
    private RecyclerView recyclerViewImages;
    private EditText editTextHoten, editTextPhone,  editTextBiography;
    private EditText editTextHeight, editTextWeight, editTextPersonalityType, editTextInterests, editTextAddress, editTextJob;
    private Spinner spinnerGender, spinnerSexualOrientation, spinnerZodiacSign;
    private Button btnSave;
    private TextView editTextBirthday;
    private  boolean isNew = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        tokenManager = new TokenManager(getApplicationContext());
        userService = new UserService("Bearer " + tokenManager.getAccessToken());
        userId = getIntent().getLongExtra("userId",0L);

        getUserImages();
        getUserInfo();
        binding();
        imbtn_thoat.setOnClickListener(v -> openMainActivity());
        btnSave.setOnClickListener(v -> saveUserInfo());
        editTextBirthday.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        editTextBirthday.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }
    private void openMainActivity(){
        if (isNew){
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            // Xóa các activity cũ khỏi stack để không back lại màn hình login
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else
        {
            finish();
        }
    }
    private void getUserId(Runnable callback) {
        isNew = true;
        userService.getUserInfo2(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().getId();
                    user = new Users();
                    user.setId(userId);
                    Log.d("profile", "getUserId: " + userId);
                    callback.run(); // Trigger the callback to proceed with saveUserInfo
                } else {
                    Log.e("profile", "Failed to get user ID: " + response.message());
                    Toast.makeText(ProfileActivity.this, "Failed to load user ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable throwable) {
                Log.e("profile", "Error fetching user ID", throwable);
                Toast.makeText(ProfileActivity.this, "Error fetching user ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void binding(){
        // Khởi tạo các view
        recyclerViewImages = findViewById(R.id.rcl_anh);
        editTextHoten = findViewById(R.id.editTextHoten);
        editTextPhone = findViewById(R.id.editTextPhone);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerSexualOrientation = findViewById(R.id.spinnerSexualOrientation);
        editTextBirthday = findViewById(R.id.editTextBirthday);
        editTextBiography = findViewById(R.id.editTextBiography);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        spinnerZodiacSign = findViewById(R.id.spinnerZodiacSign);
        editTextPersonalityType = findViewById(R.id.editTextPersonalityType);
        editTextInterests = findViewById(R.id.editTextInterests);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextJob = findViewById(R.id.editTextJob);
        btnSave = findViewById(R.id.btnSave);
        imbtn_thoat = findViewById(R.id.btnBack);
    }

    private void getUserInfo() {
        userService.getUserInfo(userId, new Callback<Users>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Users> call,@NonNull  Response<Users> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    if (user != null) {
                        if (user.getBirthday() != null){
                            int year = user.getBirthday().getYear();
                            int month = user.getBirthday().getMonth();
                            int date = user.getBirthday().getDate();
                            editTextBirthday.setText(date + "/" +
                                    month + "/" + year);
                        }
                        // Hiển thị thông tin người dùng
                        editTextHoten.setText(user.getName());
                        editTextPhone.setText(user.getPhone());

                        editTextBiography.setText(user.getBiography());
                        editTextHeight.setText(String.valueOf(user.getHeight()));
                        editTextWeight.setText(String.valueOf(user.getWeight()));
                        editTextPersonalityType.setText(user.getPersonalityType());
                        editTextInterests.setText(user.getInterests());
                        editTextAddress.setText(user.getAddress());
                        editTextJob.setText(user.getJob());

                        // Thiết lập Spinner
                        setSpinnerSelection(spinnerGender, getResources().getStringArray(R.array.gender_list), user.getGender());
                        setSpinnerSelection(spinnerSexualOrientation, getResources().getStringArray(R.array.sexual_orientation_list), user.getSexualOrientation());
                        setSpinnerSelection(spinnerZodiacSign, getResources().getStringArray(R.array.zodiac_sign_list), user.getZodiacSign());
                    }
                } else {
                    Log.d("ProfileActivity", "GetUser failed: " + response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call,@NonNull  Throwable t) {
                Log.d("ProfileActivity", "GetUser error: " + t.getMessage());
            }
        });
    }

    private void getUserImages() {
        userService.getAllUserImage(userId, new Callback<List<Image>>() {
            @Override
            public void onResponse(@NonNull Call<List<Image>> call,@NonNull  Response<List<Image>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userImages.clear();
                    userImages.addAll(response.body());
                    ImageAdapter imageAdapter = new ImageAdapter(ProfileActivity.this,userImages, ProfileActivity.this);
                    Log.d("anh", "onResponse: "+userImages.size());
                    recyclerViewImages.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                    // Thiết lập RecyclerView
                    recyclerViewImages.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 3));
                } else {
                    Log.d("ProfileActivity", "GetUserImages failed: " + response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Image>> call,@NonNull  Throwable t) {
                Log.d("ProfileActivity", "GetUserImages error: " + t.getMessage());
            }
        });
    }

    private void saveUserInfo() {
        if (userId == 0) {
            getUserId(() -> {
                // This callback runs after userId and user are set
                updateUserInfo();
            });
        } else {
            if (user == null) {
                user = new Users();
                user.setId(userId);
            }
            updateUserInfo();
        }
    }

    private void updateUserInfo() {
        if (user == null) {
            Log.e("ProfileActivity", "User object is null");
            Toast.makeText(this, "Error: User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            user.setName(editTextHoten.getText().toString());
            user.setPhone(editTextPhone.getText().toString());
            String[] parts = editTextBirthday.getText().toString().split("/");

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);
            Toast.makeText(this, "sinh nhat " + day + " " + month + " " + year + " userId " + userId, Toast.LENGTH_SHORT).show();
            user.setBirthday(new Date(year, month, day));
            user.setBiography(editTextBiography.getText().toString());
            user.setHeight(Float.parseFloat(editTextHeight.getText().toString().isEmpty() ? "0" : editTextHeight.getText().toString()));
            user.setWeight(Integer.parseInt(editTextWeight.getText().toString().isEmpty() ? "0" : editTextWeight.getText().toString()));
            user.setPersonalityType(editTextPersonalityType.getText().toString());
            user.setInterests(editTextInterests.getText().toString());
            user.setAddress(editTextAddress.getText().toString());
            user.setJob(editTextJob.getText().toString());
            user.setGender(spinnerGender.getSelectedItem().toString());
            user.setSexualOrientation(spinnerSexualOrientation.getSelectedItem().toString());
            user.setZodiacSign(spinnerZodiacSign.getSelectedItem().toString());

            // Gọi API để lưu thông tin
            userService.UpdateUserInfo(user);
            Toast.makeText(this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("ProfileActivity", "Error updating user info", e);
            Toast.makeText(this, "Error updating user info", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String[] items, String value) {
        if (value != null) {
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    protected void onPause() {
        Glide.with(this).pauseRequests();
        super.onPause();
    }

    @Override
    protected void onResume() {
        Glide.with(this).resumeRequests();
        super.onResume();
    }

    @Override
    public void onImageDelete(Image image) {
        userService.delUserImage(image);
        Toast.makeText(this, "Xoa thanh cong", Toast.LENGTH_SHORT).show();
        userImages.remove(image);
        ImageAdapter imageAdapter = new ImageAdapter(ProfileActivity.this,userImages, ProfileActivity.this);
        Log.d("anh", "onResponse: "+userImages.size());
        recyclerViewImages.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        // Thiết lập RecyclerView
        recyclerViewImages.setLayoutManager(new GridLayoutManager(ProfileActivity.this, 3));
    }
}