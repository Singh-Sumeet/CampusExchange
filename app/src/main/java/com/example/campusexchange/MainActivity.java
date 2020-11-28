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


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Insert time delay here
        try {
            Thread.sleep(1000);
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }

        if(currentUser != null) {   //User is logeged in
            //Move to menu
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