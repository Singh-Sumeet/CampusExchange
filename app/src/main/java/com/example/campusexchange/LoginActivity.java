package com.example.campusexchange;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.LoginFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Useful.User;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;         //Firebase Authorisation
    FirebaseUser currentUser;   //Used for current firebase user's user id
    FirebaseFirestore db;       //Firebase
    CollectionReference users;  //For refering directly to users collection
    EditText uname;
    EditText pass;
    Button login;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        users = db.collection("Users");

        uname = findViewById(R.id.login_id);
        pass = findViewById(R.id.login_pass);
        login = findViewById(R.id.buttonSubmitLogin);
        register = findViewById(R.id.reg_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Boolean[] failure = {false};
                String reg_id = uname.getText().toString();
                String passText = pass.getText().toString();

                if(reg_id.contentEquals("") || passText.contentEquals("")) {
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                    return;
                }

                String email = reg_id + "@pict.college";

                mAuth.signInWithEmailAndPassword(email, passText)  //Sign in happening
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {   //if sign in execution complete...
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {    //this function after sign in execution
                                if(task.isSuccessful()) {   //if signed in
                                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    currentUser = task.getResult().getUser();
                                    String uid = currentUser.getUid();
                                    Query qUser = users.whereEqualTo("UID", uid);   //query created, not executed
                                    qUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { //Query executed
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {     //After query completes execution
                                            if(!task.isSuccessful()) {  //if query failed
                                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                failure[0] = true;
                                                return;
                                            }
                                            else {  //if query passed
                                               for(QueryDocumentSnapshot u: task.getResult()) { //task contains only one result
                                                   User.setUID(u.get("UID").toString());
                                                   User.setName(u.get("Name").toString());
                                                   User.setRegId(u.get("RegID").toString());
                                                   User.setProfilePic(u.get("ProfilePic").toString());
                                               }
                                               if(task.getResult().isEmpty()) { //If no result comes, it means user exists without data.
                                                   currentUser.delete(); //If result is empty the user exists but has no data. So we must delete the user
                                                   Toast.makeText(LoginActivity.this, "Try registering again", Toast.LENGTH_SHORT).show();
                                               }
                                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                                startActivity(homeIntent);
                                                finish();
                                            }
                                        }
                                    });
                                    if(failure[0]) return;
                                }
                                else {  //If not signed in
                                    Toast.makeText(LoginActivity.this, "Can't Log In"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

    }
}
