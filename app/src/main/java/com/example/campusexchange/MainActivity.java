package com.example.campusexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import Useful.User;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        final Handler handler = new Handler();

        if(currentUser != null) {   //User is logeged in
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    User.setUID(currentUser.getUid());
                    User.setName(currentUser.getDisplayName());
                    User.setRegId(currentUser.getEmail().substring(0, 11));
                    //TODO:Still have to get current user's profile pic
                    //TODO:So instead of from here, we should probably get it from the collection "Users" which I am leaving for now

                    Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(homeIntent);
                    finish();

                }
            }, 3000);
        }
        else { //User isn't signed in
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();

                }
            }, 3000);
        }
    }
}