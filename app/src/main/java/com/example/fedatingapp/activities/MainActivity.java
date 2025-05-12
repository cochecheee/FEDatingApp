package com.example.fedatingapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.fragments.AccountFragment;
import com.example.fedatingapp.fragments.ActivityFragment;
import com.example.fedatingapp.fragments.ExploreFragment;
import com.example.fedatingapp.fragments.SwipeViewFragment;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.TokenManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, WebSocketClient.Listener, WebSocketClient.MessageListener {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private WebSocketManager webSocketManager;
    private TokenManager tokenManager;
    private UserService userService;
    private static long userId;
    private BottomNavigationView bottomNavigationView;
    private int currentFragmentId = -1; // Track current fragment

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // Services & Utils
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getApiService();
        userService = new UserService("Bearer " + tokenManager.getAccessToken());
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        getUserId(() -> {
            webSocketManager = WebSocketManager.getInstance(getApplicationContext());
            webSocketManager.initialize(userId, this, this);

            // Load initial fragment
            bottomNavigationView.setSelectedItemId(R.id.account);
        });

        // Khởi tạo FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Kiểm tra và yêu cầu quyền
        checkLocationPermission();
    }

    private void getUserId(Runnable onUserIdFetched) {
        userService.getUserInfo2(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    userId = response.body().getId();
                    Log.d("main", "onResponse: " + userId);
                    onUserIdFetched.run(); // Proceed with dependent operations
                } else {
                    Log.e("main", "Failed to fetch userId: " + response.code());
                    Toast.makeText(mContext, "Failed to fetch user info", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable throwable) {
                Log.e("main", "Error fetching userId: " + throwable.getMessage());
                Toast.makeText(mContext, "Error fetching user info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        // Always create a new fragment, even if the same tab is selected
        if (itemId == R.id.account) {
            loadFragment(AccountFragment.newInstance(userId));
            currentFragmentId = R.id.account;
        } else if (itemId == R.id.fire) {
            loadFragment(new SwipeViewFragment());
            currentFragmentId = R.id.fire;
        } else if (itemId == R.id.chat) {
            loadFragment(new ActivityFragment(userId));
            currentFragmentId = R.id.chat;
        } else if (itemId == R.id.explore) {
            loadFragment(new ExploreFragment(userId));
            currentFragmentId = R.id.explore;
        }

        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Clear back stack if needed
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
            fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onNotifyReceived(Notification notification) {
        // Handle notification
    }

    @Override
    public void onMessageReceived(Message message) {
        // Handle message
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Quyền đã được cấp, lấy vị trí
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, lấy vị trí
                getLastLocation();
            } else {
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Toast.makeText(MainActivity.this,
                                        "toa do " + latitude, Toast.LENGTH_SHORT).show();
                                updateUserLocationOnServer(latitude,longitude);
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Không thể lấy vị trí", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(MainActivity.this,
                                    "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void updateUserLocationOnServer(double latitude, double longitude) {
        if (apiService == null || mContext == null) return;
        Log.d(TAG, "Updating user location on server: Lat=" + latitude + ", Long=" + longitude);

        // ** LẤY TOKEN VÀ TẠO BEARER TOKEN **
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            Log.e(TAG, "Auth token is missing for updating location.");
            return;
        }
        String bearerToken = "Bearer " + accessToken;

        Log.d(TAG, "Updating user location on server...");
        apiService.updateUserLocation(latitude,longitude,bearerToken).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) { // Giả sử OK là 200
                    Log.i(TAG, "User location updated successfully on server.");
                } else {
                    Log.e(TAG, "Error updating user location: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Network error updating location", t);
            }
        });
    }
}