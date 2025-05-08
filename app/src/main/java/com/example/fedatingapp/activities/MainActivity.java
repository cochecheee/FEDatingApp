package com.example.fedatingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.fragments.AccountFragment;
import com.example.fedatingapp.fragments.ActivityFragment;
import com.example.fedatingapp.fragments.ExploreFragment;
import com.example.fedatingapp.fragments.SwipeViewFragment;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, WebSocketClient.Listener, WebSocketClient.MessageListener {

    private Context mContext;
    private WebSocketManager webSocketManager;
    private TokenManager tokenManager;
    private UserService userService;
    private static long userId;
    private BottomNavigationView bottomNavigationView;
    private int currentFragmentId = -1; // Track current fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        tokenManager = new TokenManager(this);
        userService = new UserService("Bearer " + tokenManager.getAccessToken());
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getUserId(() -> {
            webSocketManager = WebSocketManager.getInstance(getApplicationContext());
            webSocketManager.initialize(userId, this, this);

            // Load initial fragment
            bottomNavigationView.setSelectedItemId(R.id.account);
        });
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
}