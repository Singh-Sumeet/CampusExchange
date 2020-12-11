package com.example.campusexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Useful.User;

public class ItemActivity extends AppCompatActivity {

    ImageView itemImage;
    TextView itemName, itemDescription, itemCategory, itemPrice, itemPersonName, itemDate;
    ImageButton itemChatButton;

    String imageUri, name, description, category, price, personName, date;
    Intent fromHome;

    FirebaseFirestore db;
    CollectionReference chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fromHome = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        itemImage = findViewById(R.id.itemImage);
        itemName = findViewById(R.id.itemName);
        itemDescription = findViewById(R.id.itemDescription);
        itemCategory = findViewById(R.id.itemCategory);
        itemPrice = findViewById(R.id.itemPrice);
        itemPersonName = findViewById(R.id.itemPersonName);
        itemDate = findViewById(R.id.itemDate);
        itemChatButton = findViewById(R.id.itemChatButton);

        getItemData();
        setItemData();

        itemChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChat();
            }
        });
    }

    private void getItemData() {
        if(!(   //If the intent has no extra data
                fromHome.hasExtra("Name")
                && fromHome.hasExtra("Description")
                && fromHome.hasExtra("Category")
                && fromHome.hasExtra("Price")
                && fromHome.hasExtra("Image")
                && fromHome.hasExtra("Date")
                && fromHome.hasExtra("PersonName")
                && fromHome.hasExtra("PersonUID")
        )) {
            Toast.makeText(ItemActivity.this, "No Item data available", Toast.LENGTH_SHORT).show();
            return;
        }

        name = fromHome.getStringExtra("Name");
        description = fromHome.getStringExtra("Description");
        category = fromHome.getStringExtra("Category");
        price = fromHome.getStringExtra("Price");
        imageUri = fromHome.getStringExtra("Image");
        personName = fromHome.getStringExtra("PersonName");
        date = fromHome.getStringExtra("Date");
    }

    private void setItemData() {
        Glide.with(ItemActivity.this).load(imageUri).centerCrop().into(itemImage);
        itemName.setText(name);
        itemDescription.setText(description);
        itemCategory.setText("> "+ category);
        itemPrice.setText("Rs. " + price);
        itemPersonName.setText(personName);
        itemDate.setText(date);
    }

    private void goToChat() {
        Intent chatIntent = new Intent(ItemActivity.this, ChatActivity.class);
        chatIntent.putExtra("UserName", fromHome.getStringExtra("PersonName"));
        chatIntent.putExtra("UserID", fromHome.getStringExtra("PersonUID"));
        startActivity(chatIntent);
        finish();
    }
}