package com.example.campusexchange;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import HomeNavigation.ChatFragment;
import HomeNavigation.HomeFragment;
import HomeNavigation.SellFragment;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView homeNavigation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeNavigation = findViewById(R.id.homeNavigationView);
        homeNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch(item.getItemId()) {
                    case R.id.menuHome:
                        selectedFragment = new HomeFragment();
                    break;
                    case R.id.menuChats:
                        selectedFragment = new ChatFragment();
                    break;
                    case R.id.menuSellItem:
                        selectedFragment = new SellFragment();
                    break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.homeFrame, selectedFragment).commit();
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.homeFrame, new HomeFragment()).commit();
    }
}