package com.example.fedatingapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.fedatingapp.R;
import com.example.fedatingapp.adapters.ViewPagerAdapter;
import com.example.fedatingapp.fragments.AccountFragment;
import com.example.fedatingapp.fragments.ActivityFragment;
import com.example.fedatingapp.fragments.SwipeViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Context mContext;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);

        ArrayList<Fragment> fragList = new ArrayList<>();
        fragList.add(new AccountFragment());
        fragList.add(new SwipeViewFragment());
        fragList.add(new ActivityFragment());
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
        }
        return true; // Return true to indicate selection was handled
    }
}