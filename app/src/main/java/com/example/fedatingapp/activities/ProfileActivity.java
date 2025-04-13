package com.example.fedatingapp.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.adapters.ImageAdapter;
import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.Users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private UserService userService = new UserService();
    private Long userId;
    private Users user;
    private List<Image> userImages;

    private RecyclerView recyclerViewImages;
    private EditText editTextHoten, editTextPhone, editTextBirthday, editTextBiography;
    private EditText editTextHeight, editTextWeight, editTextPersonalityType, editTextInterests, editTextAddress, editTextJob;
    private Spinner spinnerGender, spinnerSexualOrientation, spinnerZodiacSign;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        userId = getIntent().getLongExtra("userId", 1L);

        binding();

        getUserInfo();
        getUserImages();

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

    }

    private void getUserInfo() {
        userService.getUserInfo(userId, new Callback<Users>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Users> call,@NonNull  Response<Users> response) {
                if (response.isSuccessful()) {
                    user = response.body();
                    if (user != null) {
                        int year = user.getBirthday().getYear();
                        int month = user.getBirthday().getMonth();
                        // Hiển thị thông tin người dùng
                        editTextHoten.setText(user.getName());
                        editTextPhone.setText(user.getPhone());
                        editTextBirthday.setText(user.getBirthday().getDate() + "/" +
                                month + "/" + year);
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
                    ImageAdapter imageAdapter = new ImageAdapter(ProfileActivity.this,userImages);
                    Log.d("anh", "onResponse: "+userImages.size());
                    recyclerViewImages.setAdapter(imageAdapter);
                    //imageAdapter.notifyDataSetChanged();
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
        // Cập nhật thông tin người dùng
        if (user != null) {
            user.setName(editTextHoten.getText().toString());
            user.setPhone(editTextPhone.getText().toString());
            String[] parts = editTextBirthday.getText().toString().split("/");

            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            user.setBirthday(new Date(year,month,day));
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
            userService.UpdateUserInfo( user);
            Toast.makeText(this, "Cập nhật thông tin thannh công.", Toast.LENGTH_SHORT).show();
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
}