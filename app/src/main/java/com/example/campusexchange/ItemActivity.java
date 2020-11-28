package com.example.campusexchange;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ItemActivity extends AppCompatActivity {

    ImageView itemImage;
    TextView itemName, itemDescription, itemCategory, itemPrice, itemPersonName, itemDate;

    String imageUri, name, description, category, price, personName, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
    }

    private void getItemData() {
        Intent fromHome = getIntent();
        if(!(   //If the intent has no extra data
                fromHome.hasExtra("Name")
                && fromHome.hasExtra("Description")
                && fromHome.hasExtra("Category")
                && fromHome.hasExtra("Price")
                && fromHome.hasExtra("Image")
                && fromHome.hasExtra("Date")
                && fromHome.hasExtra("Person")
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
        itemCategory.setText(category);
        itemPrice.setText("Rs. " + price);
        itemPersonName.setText(personName);
        itemDate.setText(date);
    }
}