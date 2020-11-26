package com.example.campusexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button startApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Show splash screen

        startApp = findViewById(R.id.button_start_app);
        startApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(currentUser != null) {   //User is logeged in
                //Move to menu
                Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
            }
            else { //User isn't signed in
                Toast.makeText(MainActivity.this, "Not Logged In", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
            }
        });
    }
}