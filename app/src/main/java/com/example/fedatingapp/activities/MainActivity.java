package com.example.fedatingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.adapters.ViewPagerAdapter;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.fragments.AccountFragment;
import com.example.fedatingapp.fragments.ActivityFragment;
import com.example.fedatingapp.fragments.ExploreFragment;
import com.example.fedatingapp.fragments.SwipeViewFragment;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.NotificationUtils;
import com.example.fedatingapp.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, WebSocketClient.Listener, WebSocketClient.MessageListener {

    private Context mContext;
    private ViewPager viewPager;
    private WebSocketManager webSocketManager;
    private TokenManager tokenManager;
    private UserService userService;
    private static long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        tokenManager = new TokenManager(this);
        userService = new UserService("Bearer " + tokenManager.getAccessToken());

        getUserId(() -> {
            webSocketManager = WebSocketManager.getInstance(getApplicationContext());
            webSocketManager.initialize(userId, this, this);
            setupViewPagerAndNavigation();
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

    private void setupViewPagerAndNavigation() {
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        ArrayList<Fragment> fragList = new ArrayList<>();
        fragList.add(AccountFragment.newInstance(userId));
        fragList.add(new SwipeViewFragment());
        fragList.add(new ActivityFragment(userId));
        fragList.add(new ExploreFragment(userId));

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(fragList, getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        bnv.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.account) {
            viewPager.setCurrentItem(0);
        } else if (itemId == R.id.fire) {
            viewPager.setCurrentItem(1);
        } else if (itemId == R.id.chat) {
            viewPager.setCurrentItem(2);
        } else if (itemId == R.id.explore) {
            viewPager.setCurrentItem(3);
        }
        return true;
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