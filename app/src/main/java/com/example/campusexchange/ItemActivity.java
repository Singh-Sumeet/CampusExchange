package com.example.campusexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    Button itemChatButton;

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

        getItemData();
        setItemData();

        itemChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] docExists = new boolean[1];
                db = FirebaseFirestore.getInstance();
                chats = db.collection("Chats");
                DocumentReference chat1 = chats.document(User.getUID()+"_"+fromHome.getStringExtra("PersonUID"));
                DocumentReference chat2 = chats.document(fromHome.getStringExtra("PersonUID")+"_"+User.getUID());
                chat1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()) goToChat();
                    }
                });
                chat2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()) goToChat();
                    }
                });

                final Map<String, Object> theChat = new HashMap<>();         //The Entire Chat Information (The Users and The Chat Messages)
                final Map<String, Object> chatMessages = new HashMap<>(); //The Entire Chat Messages
                final Map<String, Object> chatMessage = new HashMap<>();  //A Single Chat Message
                final Map<String, Object> user1 = new HashMap<>();        //A Chat Participant
                final Map<String, Object> user2 = new HashMap<>();        //Another Chat Participant

                theChat.put("ChatNumber", 1);

                user1.put("Name", User.getName());
                user1.put("UID", User.getUID());
                theChat.put("User1", user1);

                user2.put("Name", fromHome.getStringExtra("PersonName"));
                user2.put("UID", fromHome.getStringExtra("PersonUID"));
                theChat.put("User2", user2);

                chatMessage.put("Date", Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTime());
                chatMessage.put("From", "User1");
                chatMessage.put("To", "User2");
                chatMessage.put("Message", "");
                chatMessages.put("0", chatMessage);
                theChat.put("ChatMessages", chatMessages);

                chats.document(User.getUID()+"_"+fromHome.getStringExtra("PersonUID"))
                        .set(theChat)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                theChat.clear();
                                chatMessages.clear();
                                chatMessage.clear();
                                user1.clear();
                                user2.clear();
                                if(task.isSuccessful()) {
                                    goToChat();
                                }
                                else {
                                    Toast.makeText(ItemActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
        personName = fromHome.getStringExtra("Person");
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