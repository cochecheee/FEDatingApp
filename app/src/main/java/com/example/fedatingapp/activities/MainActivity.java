package com.example.fedatingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fedatingapp.R;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.adapters.ViewPagerAdapter;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.fragments.AccountFragment;
import com.example.fedatingapp.fragments.ActivityFragment;
import com.example.fedatingapp.fragments.ExploreFragment;
import com.example.fedatingapp.fragments.SwipeViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, WebSocketClient.MessageListener{

    private Context mContext;
    private ViewPager viewPager;
    WebSocketManager webSocketManager;
    private long userId = 1L;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webSocketManager = WebSocketManager.getInstance();
        webSocketManager.initialize(userId, this);
        mContext = this;

        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        ArrayList<Fragment> fragList = new ArrayList<>();
        fragList.add(AccountFragment.newInstance(userId));
        fragList.add(new SwipeViewFragment());
        fragList.add(new ActivityFragment(userId));
        fragList.add(new ExploreFragment());
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
        }else if (itemId == R.id.explore){
            viewPager.setCurrentItem(3);
        }
        return true; // Return true to indicate selection was handled
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onMessageReceived(Message message) {
        Toast.makeText(mContext, "Co tin nhan gui den", Toast.LENGTH_SHORT).show();
    }
}