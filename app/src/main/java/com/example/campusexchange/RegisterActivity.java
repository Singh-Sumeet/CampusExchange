package com.example.campusexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import Useful.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editRegId;
    private EditText editPass;
    private EditText editConfPass;
    private Button buttonSubmit;

    private FirebaseFirestore db;           //Firebase
    private FirebaseAuth mAuth;             //Firebase Authorisation
    private FirebaseUser currentUser;       //Used for current firebase user's user id
    private CollectionReference users;      //For refering directly to users collection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.reg_name);
        editRegId = findViewById(R.id.reg_id);
        editPass = findViewById(R.id.reg_pass);
        editConfPass = findViewById(R.id.reg_conf_pass);
        buttonSubmit = findViewById(R.id.buttonSubmitReg);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        users = db.collection("Users");

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Boolean[] failure = {false};

                final String name = editName.getText().toString();
                final String regId = editRegId.getText().toString();
                String pass = editPass.getText().toString();
                String confPass = editConfPass.getText().toString();

                if(!regId.matches("^[C|E|I]2K[1|2][0-9][0-9]{6}$")) {   //matches colege's reg id
                    Toast.makeText(RegisterActivity.this, "Invalid Registration ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pass.matches(confPass)) {   //passwords must match
                    Toast.makeText(RegisterActivity.this, "The passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = regId + "@pict.college";     //college id isn't in email format
                mAuth.createUserWithEmailAndPassword(email, pass)   //Registration with email and password
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {    //After registration execution completes
                                if(task.isSuccessful()) {   //if registration successful
                                    currentUser = mAuth.getCurrentUser();
                                    Map<String, Object> newUser = new HashMap<>();
                                    User.setUID(currentUser.getUid());  newUser.put("UID", currentUser.getUid());
                                    User.setName(name);                 newUser.put("Name", name);
                                    User.setRegId(regId);               newUser.put("RegID", regId);
                                    users.add(newUser).addOnFailureListener(new OnFailureListener() {   //Add the new user to Users collection
                                        @Override
                                        public void onFailure(@NonNull Exception e) {   //If users can't be added
                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            failure[0] = true;
                                            currentUser.delete();   //have to delete auth
                                        }
                                    });
                                }
                                else {  //If registration fails
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    failure[0] = true;
                                }
                            }
                        });

                if(failure[0]) return;
                //Move to main page
            }
        });
    }
}