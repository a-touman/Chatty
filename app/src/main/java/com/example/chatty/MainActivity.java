package com.example.chatty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   //  startActivity(new Intent(this,HomePage.class));

    }
    //Sign Up Button Handler
    public void launchCreateAccount(View v){

        Intent i = new Intent(this,SignUpPage.class);
        startActivity(i);

    }
    //Sign In Button Handler
    public void launchSignInPage(View v){

        Intent inte1 = new Intent(this,SignInPage.class);
        startActivity(inte1);

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }




}//end of class