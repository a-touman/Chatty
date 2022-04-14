package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

public class SignInPage extends AppCompatActivity implements View.OnClickListener {

    // declare objs
    private TextView Signinpage_backarrow;
    private EditText Signin_emailfield,Signin_pass;
    private Button Signin_continue;
    private ProgressBar Signin_progress;

    private FirebaseAuth mAuth;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_page);







        //defining Firebase Authentication object inside (onCreate) method
        mAuth = FirebaseAuth.getInstance();

        //define & action listener for back button
        Signinpage_backarrow = (TextView) findViewById(R.id.go_back_Signin);
        Signinpage_backarrow.setOnClickListener(this);

        //define & action listener for (Continue) button
        Signin_continue = (Button) findViewById(R.id.signin_btn);
        Signin_continue.setOnClickListener(this);

        //defining objects of our page
        Signin_emailfield = (EditText) findViewById(R.id.emailfield_Signin);
        Signin_pass = (EditText) findViewById(R.id.passw_Signin);

        Signin_progress = (ProgressBar) findViewById(R.id.progressBar_Signin);




    }// end on create method


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.go_back_Signin: // what to do when back button is pressed
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.signin_btn: // what to do when (Continue) button is pressed
                signInUser();
                break;


    }//end of switch case



}// end of on click

    private void signInUser() {

        //creating strings out of edit texts

        String loginmail = Signin_emailfield.getText().toString().trim();
        String loginpass= Signin_pass.getText().toString().trim();

        //checking if empty
        if(loginmail.isEmpty()){
            Signin_emailfield.setError("Email is Required");
            Signin_emailfield.requestFocus();
            return; }
        if(loginpass.isEmpty()){
            Signin_pass.setError("Password is Required");
            Signin_pass.requestFocus();
            return; }
        // if mail does NOT match standard email patterns ...
        if(!Patterns.EMAIL_ADDRESS.matcher(loginmail).matches()){
            Signin_emailfield.setError("Please enter a valid email Address");
            Signin_emailfield.requestFocus();
            return;
        }
        //check for password length
        if(loginpass.length() < 6){
            Signin_pass.setError("Minimum number of characters is 6 ... Please Try Again");
            Signin_pass.requestFocus();
            return;
        }

        //start spinning progress bar

        Signin_progress.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(loginmail,loginpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    sendVerifyLink(user);


                }else{
                    Toast.makeText(SignInPage.this,"Failed to Log in, Try Again..",Toast.LENGTH_LONG).show();
                    Signin_progress.setVisibility(View.GONE);
                }


            }
        });


    }//end of SignInUser

    boolean new_User; // checks user's novelty

    private void sendVerifyLink(FirebaseUser user){

        if(user.isEmailVerified()){
            //send to setProfile Activity if new , Home page Activity if not
            if (new_User)
            startActivity(new Intent(SignInPage.this,SetProfile.class));
            else
                startActivity(new Intent(SignInPage.this,HomePage.class));

            Toast.makeText(SignInPage.this,"Logged into Account",Toast.LENGTH_SHORT).show();

        }
        else{
            //send link to non verified user
            new_User = true;
            user.sendEmailVerification();
            Signin_progress.setVisibility(View.GONE);
            Toast.makeText(SignInPage.this,"Check Your Inbox to Verify Your Email",Toast.LENGTH_LONG).show();
        }

    }//end of verification method



    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }



}//end of class

