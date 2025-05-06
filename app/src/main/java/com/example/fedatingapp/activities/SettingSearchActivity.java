package com.example.fedatingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.entities.SearchCriteria;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingSearchActivity extends AppCompatActivity {
    private UserService userService = new UserService(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_search);

        // Find UI elements
        TextInputEditText etDatingPurpose = findViewById(R.id.et_dating_purpose);
        TextInputEditText etMinAge = findViewById(R.id.et_min_age);
        TextInputEditText etMaxAge = findViewById(R.id.et_max_age);
        TextInputEditText etDistance = findViewById(R.id.et_distance);
        AutoCompleteTextView etInterests = findViewById(R.id.et_interests);
        AutoCompleteTextView dropdownZodiac = findViewById(R.id.dropdown_zodiac);
        AutoCompleteTextView etPersonalityType = findViewById(R.id.et_personality_type);
        RangeSlider ageRangeSlider = findViewById(R.id.age_range_slider);
        Slider distanceSlider = findViewById(R.id.distance_slider);
        MaterialButton btnCancel = findViewById(R.id.btn_cancel);
        MaterialButton btnSave = findViewById(R.id.btn_save);


        Intent intent = getIntent();
        Long userId = intent.getLongExtra("userId", 0L);

        // Set up Zodiac dropdown
        ArrayAdapter<String> zodiacAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.zodiac_sign_list));
        dropdownZodiac.setAdapter(zodiacAdapter);

        // Set up Zodiac dropdown
        ArrayAdapter<String> interest = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.common_interests));
        etInterests.setAdapter(interest);

        ArrayAdapter<String> personalType = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.mbti_types));
        etPersonalityType.setAdapter(personalType);

        // Call getSearch API to populate the UI
        userService.getSearch(userId, new Callback<SearchCriteria>() {
            @Override
            public void onResponse(Call<SearchCriteria> call, Response<SearchCriteria> response) {
                if (response.isSuccessful() && Objects.requireNonNull(response.body()).getId() != null) {
                    SearchCriteria criteria = response.body();

                    // Populate UI fields with API data
                    etDatingPurpose.setText(criteria.getDatingPurpose());
                    etMinAge.setText(String.valueOf(criteria.getMinAge()));
                    etMaxAge.setText(String.valueOf(criteria.getMaxAge()));
                    etDistance.setText(String.valueOf(criteria.getDistance()));
//                    etInterests.setText(criteria.getInterests());
                    setDropdownSelection(etInterests, getResources().getStringArray(R.array.common_interests), criteria.getInterests());
                    etPersonalityType.setText(criteria.getPersonalityType());

                    // Set Age Range Slider
                    ageRangeSlider.setValues((float) criteria.getMinAge(), (float) criteria.getMaxAge());

                    // Set Distance Slider
                    distanceSlider.setValue((float) criteria.getDistance());

                    // Set Zodiac dropdown
                    setDropdownSelection(dropdownZodiac, getResources().getStringArray(R.array.zodiac_sign_list), criteria.getZodiacSign());
                    
                }
            }

            @Override
            public void onFailure(Call<SearchCriteria> call, Throwable throwable) {
                // Handle API failure
                Log.d("SettingSearch", "onFailure: "+ throwable.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Sync sliders with text inputs
        ageRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            etMinAge.setText(String.valueOf((slider.getValues().get(0))));
            etMaxAge.setText(String.valueOf(slider.getValues().get(1)));
        });

        distanceSlider.addOnChangeListener((slider, value, fromUser) -> {
            etDistance.setText(String.valueOf((int) value));
        });

        // Sync text inputs with sliders
        etMinAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float minAge = Float.parseFloat(s.toString());
                    List<Float> values = ageRangeSlider.getValues();
                    ageRangeSlider.setValues(minAge, values.get(1));
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }
        });

        etMaxAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float maxAge = Float.parseFloat(s.toString());
                    List<Float> values = ageRangeSlider.getValues();
                    ageRangeSlider.setValues(values.get(0), maxAge);
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }
        });

        etDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float distance = Float.parseFloat(s.toString());
                    distanceSlider.setValue(distance);
                } catch (NumberFormatException e) {
                    // Ignore invalid input
                }
            }
        });

        // Handle Save button
        btnSave.setOnClickListener(v -> {
            String datingPurpose = etDatingPurpose.getText().toString();
            int minAge = etMinAge.getText().toString().isEmpty() ? 0 : Integer.parseInt(etMinAge.getText().toString());
            int maxAge = etMaxAge.getText().toString().isEmpty() ? 0 : Integer.parseInt(etMaxAge.getText().toString());
            int distance = etDistance.getText().toString().isEmpty() ? 0 : Integer.parseInt(etDistance.getText().toString());
            String interests = etInterests.getText().toString();
            String personalityType = etPersonalityType.getText().toString();
            String zodiacSign = dropdownZodiac.getText().toString();

            // Create Criteria object
            SearchCriteria newCriteria = new SearchCriteria(datingPurpose, minAge, maxAge, distance,
                    interests, zodiacSign, personalityType);

            newCriteria.setId(userId);
            userService.updateSearch(newCriteria);
            finish();
        });

        // Handle Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setDropdownSelection(AutoCompleteTextView dropdown, String[] items, String value) {
        if (value != null) {
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(value)) {
                    dropdown.setText(items[i], false);
                    break;
                }
            }
        }
    }
}
