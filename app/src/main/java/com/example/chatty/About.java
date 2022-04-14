package com.example.chatty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

public class About extends AppCompatActivity {



    ImageView mrBit;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        mrBit = findViewById(R.id.mrbitimage_id);

        mrBit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(findViewById(android.R.id.content), " :)", Snackbar.LENGTH_LONG).show();
            }
        });




    }//end of on Create




}//end of class