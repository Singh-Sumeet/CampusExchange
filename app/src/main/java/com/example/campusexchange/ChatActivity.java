package com.example.campusexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Required.ChatViewAdapter;
import Useful.User;

public class ChatActivity extends AppCompatActivity {
    //Stuff to Send to adapter
    int noOfMessages;
    List<String> messages;
    List<String> dates;
    List<String> fromUs;
    String theirName;

    //Stuff Still Necessary
    String theirUID;

    FirebaseFirestore db;
    CollectionReference chats;
    DocumentReference chat;

    Intent fromIntent;
    RecyclerView chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fromIntent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        theirName = fromIntent.getStringExtra("UserName");
        theirUID = fromIntent.getStringExtra("UserID");

        messages = new ArrayList<>();
        dates = new ArrayList<>();
        fromUs = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        chats = db.collection("Chats");

        String chatName1 = User.getUID()+"_"+theirUID;
        String chatName2 = theirUID+"_"+User.getUID();
        final DocumentReference chat1 = chats.document(chatName1);
        final DocumentReference chat2 = chats.document(chatName2);

        chat1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                DocumentSnapshot theChat = task.getResult();
                if(theChat.exists()) getDataFromChat(theChat, true);
                else {
                    chat2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                            DocumentSnapshot theChat = task.getResult();
                            if(theChat.exists()) getDataFromChat(theChat, false);
                            else return;
                        }
                    });
                }
                return;
            }
         });

        ChatViewAdapter chatViewAdapter = new ChatViewAdapter(ChatActivity.this, noOfMessages, messages, dates, fromUs, theirName);
        chatView.setAdapter(chatViewAdapter);
        LinearLayoutManager chatViewAdapterLayoutManager = new LinearLayoutManager(ChatActivity.this);
        chatViewAdapterLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(chatViewAdapterLayoutManager);
    }

    void getDataFromChat(DocumentSnapshot theChat, boolean didWeStart) {
        noOfMessages = Integer.parseInt(theChat.get("ChatNumber").toString());
        Map<String, Object> chatMessages = (Map<String, Object>) theChat.get("ChatMessages");
        Map<String, Object> chatMessage;
        for(int i = 1; i <= noOfMessages; i++) {
            chatMessage = (Map<String, Object>) chatMessages.get(""+i);
            messages.add(chatMessage.get("Message").toString());
            dates.add(chatMessage.get("Date").toString());
            String sender = chatMessage.get("From").toString();
            if((sender.contentEquals("User1") && didWeStart) || (sender.contentEquals("User2") && !didWeStart)) fromUs.add("True");
            else fromUs.add("False");
        }
    }
}