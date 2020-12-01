package com.example.campusexchange;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import Useful.User;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        final Handler handler = new Handler();

        if(currentUser != null) {   //User is logeged in
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    db.collection("Users").whereEqualTo("UID", currentUser.getUid()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    for(QueryDocumentSnapshot result: task.getResult()) {
                                        User.setUID(result.getString("UID"));
                                        User.setName(result.getString("Name"));
                                        User.setRegId(result.getString("RegID"));
                                        User.setProfilePic(result.getString("ProfilePic"));
                                    }
                                    if(task.getResult().isEmpty()) {
                                        //TODO:why is create user not null but there is no user in database, because database was handled manually.
                                        return;
                                    }
                                    Toast.makeText(MainActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                }
                            });

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