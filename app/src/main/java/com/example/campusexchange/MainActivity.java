package com.example.campusexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        try {
            Thread.sleep(1000);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }

        if(currentUser != null) {   //User is logeged in
            User.setUID(currentUser.getUid());
            User.setName(currentUser.getDisplayName());
            User.setRegId(currentUser.getEmail().substring(0, 11));
            //TODO:Still have to get current user's profile pic
            //TODO:So instead of from here, we should probably get it from the collection "Users" which I am leaving for now

            Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
        }
        else { //User isn't signed in
            Toast.makeText(MainActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        finish();
    }
}