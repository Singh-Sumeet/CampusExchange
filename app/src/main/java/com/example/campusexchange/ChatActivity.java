package com.example.campusexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Required.ChatViewAdapter;
import Useful.ChatData;
import Useful.User;

public class ChatActivity extends AppCompatActivity {
    //Stuff to Send to adapter
    int noOfMessages;
    List<ChatData> chatData = new ArrayList<>();
    String theirName;

    //Stuff Still Necessary
    String theirUID;
    ImageButton sendMessage;
    TextView textMessage;

    FirebaseFirestore db;
    CollectionReference chats;
    DocumentSnapshot chatDocumentSnapshot;

    Intent fromIntent;
    RecyclerView chatView;
    ChatViewAdapter chatViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final boolean[] failure = {false};
        fromIntent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        theirName = fromIntent.getStringExtra("UserName");
        theirUID = fromIntent.getStringExtra("UserID");
        chatView = findViewById(R.id.chatView);
        sendMessage = findViewById(R.id.chatSendButton);
        textMessage = findViewById(R.id.chatText);

        db = FirebaseFirestore.getInstance();
        chats = db.collection("Chats");

        final Query chatQuery1 = chats.whereEqualTo("User1ID", User.getUID()).whereEqualTo("User2ID", theirUID);    //Query to find chat
        final Query chatQuery2 = chats.whereEqualTo("User2ID", theirUID).whereEqualTo("User1ID", User.getUID()); //Query to find chat

        chatQuery1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() { //Query1 executed
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //If query 1 is complete
                failure[0] = false;
                if(task.isSuccessful()) {   //If query 1 is successful
                    if(task.getResult().isEmpty()){ //If query 1 returned empty set
                        chatQuery2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {    //Query 2 is executed
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) { //If query 2 is complete
                                if(task.isSuccessful()) {   //If query2 is successful
                                    if(task.getResult().isEmpty()) {    //If Query 2 too is empty, no chats present between the users.
                                        createChat();
                                        getDataFromChat();
                                        return;
                                    }
                                    for(DocumentSnapshot doc: task.getResult()) {chatDocumentSnapshot = doc; getDataFromChat();} //Document reference of chat
                                    return;
                                }
                                else { //If query 2 is not successful
                                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    failure[0] = true;
                                    return;
                                }
                            }
                        });
                        return; //No need to go further, since both queries are now done
                    }
                    for(DocumentSnapshot doc: task.getResult()) {chatDocumentSnapshot = doc; getDataFromChat();} //Document reference of chat
                    return;
                }
                else {  //If query 1 not successful
                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    failure[0] = true;
                    return;
                }
            }
        });

        if(failure[0] == true) {
            //TODO:Manage this failure;
            return;
        }

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Sending Message", Toast.LENGTH_SHORT).show();
                final String chatMessage = textMessage.getText().toString();
                if(chatMessage.matches("^[ ]+$")) {
                    Toast.makeText(ChatActivity.this, "Invalid String", Toast.LENGTH_SHORT).show();
                    return;
                }
                final DocumentReference theChatDoc = chatDocumentSnapshot.getReference();
                CollectionReference chatMessages = theChatDoc.collection("ChatMessages");
                final int numberChats = noOfMessages + 1;
                Map<String, Object> message = new HashMap<>();
                message.put("Message", chatMessage);
                message.put("From", User.getUID());
                message.put("Date", Calendar.getInstance().getTime());
                chatMessages.document(""+numberChats).set(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                theChatDoc.update("ChatNumber", numberChats);
                                noOfMessages = numberChats;
                                chatData.add(new ChatData(chatMessage, Calendar.getInstance().getTime().toString(), "True"));
                                chatViewAdapter.notifyItemInserted(numberChats);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatActivity.this, "Message not sent: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        });
            }
        });
    }

    void getDataFromChat() {
        //Toast.makeText(ChatActivity.this, "Function: GetDataFromChat", Toast.LENGTH_LONG).show();
        if(chatDocumentSnapshot == null) {
            Toast.makeText(ChatActivity.this, "No Chat Error", Toast.LENGTH_LONG).show();
            noOfMessages = 0;
            return;
        }
        DocumentSnapshot theChat = chatDocumentSnapshot;
        DocumentReference chatRef = theChat.getReference();

        noOfMessages = Integer.parseInt(theChat.get("ChatNumber").toString());
        chatViewAdapter = new ChatViewAdapter(ChatActivity.this, chatData, theirName);
        chatView.setAdapter(chatViewAdapter);
        LinearLayoutManager chatViewAdapterLayoutManager = new LinearLayoutManager(ChatActivity.this);
        chatViewAdapterLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(chatViewAdapterLayoutManager);

        CollectionReference chatCollection = chatRef.collection("ChatMessages");
        DocumentReference oneChatDoc;
        final int[] pos = {0};
        for(int i = 1; i <= noOfMessages; i++) {
            oneChatDoc = chatCollection.document("" + i);
            oneChatDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    pos[0]++;
                    if (!task.isSuccessful()) {  //Some error occurred
                        chatData.add(new ChatData("Error Retriving Message!", "!", "True"));
                        chatViewAdapter.notifyItemInserted(pos[0]);
                        return;
                    }
                    DocumentSnapshot oneChatDocSnap = task.getResult();
                    chatData.add(new ChatData(oneChatDocSnap.get("Message").toString(), oneChatDocSnap.get("Date").toString(), oneChatDocSnap.get("From").toString().equals(User.getUID()) ? "True" : "False"));
                    chatViewAdapter.notifyItemInserted(pos[0]);
                }
            });
        }
    }

    void createChat() {
        //Toast.makeText(ChatActivity.this, "Function: Create Chat", Toast.LENGTH_LONG).show();
        noOfMessages = 0;
        Map<String, Object> chatMetaData = new HashMap<>();
        chatMetaData.put("ChatNumber", 0);
        chatMetaData.put("User1Name", User.getName());
        chatMetaData.put("User1ID", User.getUID());
        chatMetaData.put("User2Name", theirName);
        chatMetaData.put("User2ID", theirUID);
        chats.add(chatMetaData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(!task.isSuccessful()) { //Chat not created
                    Toast.makeText(ChatActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                DocumentReference chatDocumentReference = task.getResult();
                chatDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        chatDocumentSnapshot = task.getResult();
                    }
                });
                Map<String, Object> zerothChat = new HashMap<>();
                zerothChat.put("Required", "Required");
                chatDocumentReference.collection("ChatMessages").document("0").set(zerothChat);
            }
        });
    }
}