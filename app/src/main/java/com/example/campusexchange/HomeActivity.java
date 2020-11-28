package com.example.campusexchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import Required.FeedViewAdapter;

public class HomeActivity extends AppCompatActivity {

    List<String> names;
    List<String> descriptions;
    List<String> categories;
    List<String> prices;
    List<String> imagesUrl;
    List<Date> dates;
    List<Map> users;

    FirebaseFirestore db;
    CollectionReference items;

    RecyclerView feedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        feedView = findViewById(R.id.feedView);
        names = new ArrayList<>();
        descriptions = new ArrayList<>();
        categories = new ArrayList<>();
        prices = new ArrayList<>();
        imagesUrl = new ArrayList<>();
        dates = new ArrayList<>();
        users = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        items = db.collection("Items");
        Query getItems = items.orderBy("Date", Query.Direction.DESCENDING);

        getItems.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }
                        for(QueryDocumentSnapshot item: task.getResult()) {
                            names.add(item.getString("Name"));
                            descriptions.add(item.getString("Description"));
                            categories.add(item.getString("Category"));
                            prices.add(item.getString("Price"));
                            imagesUrl.add(item.getString("Image"));
                            dates.add(item.getDate("Date"));
                            users.add((Map)item.get("User"));
                        }
                        FeedViewAdapter feedViewAdapter = new FeedViewAdapter(HomeActivity.this, names, descriptions, categories, prices, imagesUrl, dates, users);
                        feedView.setAdapter(feedViewAdapter);
                        feedView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    }
                });



    }
}