package com.example.fedatingapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.databinding.ActivitySettingsBinding;
import com.example.fedatingapp.models.UserSettings;
import com.example.fedatingapp.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding; // Use ViewBinding
    private ApiService apiService;
    private TokenManager tokenManager;
    private static final String TAG = "SettingsActivity";
    private Long currentUserId = 1L; // --- TODO: Replace with actual logged-in user ID ---

    // Define arrays for spinners (ensure these exist in res/values/strings.xml)
    private String[] datingPurposesArray;
    private String[] zodiacSignsArray;
    private String[] personalityTypesArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize TokenManager FIRST
        tokenManager = new TokenManager(this);

        // Initialize ApiService
        apiService = RetrofitClient.getApiService();

        // --- TODO: Get the actual user ID ---
        // Replace '1L' with the real ID, maybe from SharedPreferences or Intent extras
        // currentUserId = getLoggedInUserId();

        loadSpinnerData(); // Load data into arrays
        setupSpinners();
        setupSeekBarListener();
        setupButtonClickListeners();

        fetchUserSettings(); // Load existing settings when the activity starts
    }

    private void loadSpinnerData() {
        datingPurposesArray = getResources().getStringArray(R.array.dating_purposes);
        zodiacSignsArray = getResources().getStringArray(R.array.zodiac_signs);
        personalityTypesArray = getResources().getStringArray(R.array.personality_types);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> datingAdapter = ArrayAdapter.createFromResource(this,
                R.array.dating_purposes, android.R.layout.simple_spinner_item);
        datingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDatingPurpose.setAdapter(datingAdapter);

        ArrayAdapter<CharSequence> zodiacAdapter = ArrayAdapter.createFromResource(this,
                R.array.zodiac_signs, android.R.layout.simple_spinner_item);
        zodiacAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerZodiacSign.setAdapter(zodiacAdapter);

        ArrayAdapter<CharSequence> personalityAdapter = ArrayAdapter.createFromResource(this,
                R.array.personality_types, android.R.layout.simple_spinner_item);
        personalityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPersonalityType.setAdapter(personalityAdapter);
    }

    private void setupSeekBarListener() {
        binding.seekbarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.textDistanceValue.setText(progress + " km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void setupButtonClickListeners() {
        binding.buttonBack.setOnClickListener(v -> finish()); // Go back
        binding.buttonSave.setOnClickListener(v -> saveUserSettings()); // Save settings
    }

    private void saveUserSettings() {
        // ** Lấy accessToken từ TokenManager **
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e(TAG, "Auth token is missing.");
            // Điều hướng về Login?
            return;
        }
        // ** Tạo chuỗi Bearer Token **
        String bearerToken = "Bearer " + accessToken;

        // --- Basic Validation (Keep or improve your existing validation) ---
        String minAgeStr = binding.editMinAge.getText().toString().trim();
        String maxAgeStr = binding.editMaxAge.getText().toString().trim();
        int minAge = 0, maxAge = 100;

        if (minAgeStr.isEmpty() || maxAgeStr.isEmpty()) {
            showError("Please enter Min and Max age."); return;
        }
        try {
            minAge = Integer.parseInt(minAgeStr);
            maxAge = Integer.parseInt(maxAgeStr);
            if (minAge < 18 || maxAge < minAge) {
                showError("Invalid age range (Min age >= 18, Max age >= Min age)."); return;
            }
        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for age."); return;
        }
        // --- Add other validations as needed ---


        // --- Create UserSettings object ---
        UserSettings settingsToSave = new UserSettings();
        settingsToSave.setDatingPurpose(binding.spinnerDatingPurpose.getSelectedItem().toString());
        settingsToSave.setZodiacSign(binding.spinnerZodiacSign.getSelectedItem().toString());
        settingsToSave.setPersonalityType(binding.spinnerPersonalityType.getSelectedItem().toString());
        settingsToSave.setMinAge(minAge);
        settingsToSave.setMaxAge(maxAge);
        settingsToSave.setDistance((double) binding.seekbarDistance.getProgress());
        settingsToSave.setInterests(binding.editInterests.getText().toString().trim());

        // TODO: Show loading indicator
        binding.buttonSave.setEnabled(false); // Prevent double clicks

        apiService.saveSettings(settingsToSave,bearerToken).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // TODO: Hide loading indicator
                binding.buttonSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                    Log.d(TAG, "Settings saved successfully.");
                    Toast.makeText(SettingsActivity.this, "Settings Saved!", Toast.LENGTH_SHORT).show();
                    finish(); // Optionally close activity
                } else if (response.code() == 401 || response.code() == 403) {
                    Log.e(TAG, "Authentication error saving settings: " + response.code());
                    showError("Session expired. Please log in again.");
                    // TODO: Handle token refresh or redirect to Login
                    tokenManager.clearTokens();
                    finish();
                } else {
                    // Try to get error message from standard ApiResponse or error body
                    String errorMsg = "Failed to save settings.";
                    try {
                        if (response.body() != null && response.body().getMessage() != null) {
                            errorMsg = response.body().getMessage();
                        } else if (response.errorBody() != null) {
                            // You might need a Gson parser to properly decode the error JSON
                            errorMsg = "Error: " + response.code() + " ("+ response.errorBody().string() +")";
                        } else {
                            errorMsg = "Failed to save settings (Code: " + response.code() + ")";
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body", e);
                        errorMsg = "Failed to save settings (Code: " + response.code() + ")";
                    }
                    Log.e(TAG, "Failed to save settings: " + errorMsg);
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                // TODO: Hide loading indicator
                binding.buttonSave.setEnabled(true);
                Log.e(TAG, "Error saving settings", t);
                showError("Network Error: " + t.getMessage());
            }
        });
    }
    private void fetchUserSettings() {
        // ** Lấy accessToken từ TokenManager **
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e(TAG, "Auth token is missing.");
            // Điều hướng về Login?
            return;
        }
        // ** Tạo chuỗi Bearer Token **
        String bearerToken = "Bearer " + accessToken;

        // TODO: Show loading indicator
        apiService.getSettings(bearerToken).enqueue(new Callback<ApiResponse<UserSettings>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserSettings>> call, Response<ApiResponse<UserSettings>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Log.d(TAG, "Settings fetched successfully.");
                    populateUi(response.body().getData());
                } else if (response.code() == 401 || response.code() == 403) { // Unauthorized or Forbidden
                    Log.e(TAG, "Authentication error fetching settings: " + response.code());
                    showError("Session expired. Please log in again.");
                    // TODO: Handle token refresh or redirect to Login
                    tokenManager.clearTokens(); // Clear invalid tokens
                    finish();
                } else {
                    Log.e(TAG, "Failed to fetch settings: " + response.code() + " - " + response.message());
                    showError("Failed to load settings (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserSettings>> call, Throwable t) {
                binding.buttonSave.setEnabled(true);
                Log.e(TAG, "Error saving settings", t);
                showError("Network Error: " + t.getMessage());
            }
        });
    }
    private void populateUi(UserSettings settings) {
        if (settings == null) return;

        // Set Spinners
        setSpinnerSelection(binding.spinnerDatingPurpose, datingPurposesArray, settings.getDatingPurpose());
        setSpinnerSelection(binding.spinnerZodiacSign, zodiacSignsArray, settings.getZodiacSign());
        setSpinnerSelection(binding.spinnerPersonalityType, personalityTypesArray, settings.getPersonalityType());

        // Set Age Range
        binding.editMinAge.setText(settings.getMinAge() != null ? String.valueOf(settings.getMinAge()) : "");
        binding.editMaxAge.setText(settings.getMaxAge() != null ? String.valueOf(settings.getMaxAge()) : "");

        // Set Distance SeekBar
        if (settings.getDistance() != null) {
            int distanceProgress = (int) Math.round(settings.getDistance());
            int maxSeekBar = binding.seekbarDistance.getMax();
            distanceProgress = Math.min(Math.max(distanceProgress, 0), maxSeekBar);

            binding.seekbarDistance.setProgress(distanceProgress);
            binding.textDistanceValue.setText(distanceProgress + " km");
        } else {
            binding.seekbarDistance.setProgress(50); // Default
            binding.textDistanceValue.setText(binding.seekbarDistance.getProgress() + " km");
        }


        // Set Interests
        binding.editInterests.setText(settings.getInterests() != null ? settings.getInterests() : "");
    }
    private void setSpinnerSelection(android.widget.Spinner spinner, String[] dataArray, String valueToSelect) {
        if (valueToSelect != null && dataArray != null) {
            for (int i = 0; i < dataArray.length; i++) {
                // Use equalsIgnoreCase for robustness
                if (dataArray[i].equalsIgnoreCase(valueToSelect)) {
                    spinner.setSelection(i);
                    return;
                }
            }
            Log.w(TAG,"Value '"+ valueToSelect +"' not found in spinner array, setting default.");
        }
        spinner.setSelection(0); // Default to first item if null or not found
    }

    private void showError(String message) {
        // Simple Toast for error messages
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        // You could replace this with a Snackbar or a dedicated error TextView
    }
}