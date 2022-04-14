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
import com.google.firebase.database.FirebaseDatabase;


public class SignUpPage extends AppCompatActivity implements View.OnClickListener {

    //declaring object for firebase authentication

    private FirebaseAuth mAuth;



    //declare objects in the SignUp page
    private TextView back_Arrow;
    private EditText editTextName,editTextEmail,editTextPassword;
    private Button continue_Btn;
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        //defining Firebase Authentication object inside (onCreate) method
        mAuth = FirebaseAuth.getInstance();



        //action listener for back button
        back_Arrow = (TextView) findViewById(R.id.go_back);
        back_Arrow.setOnClickListener(this);
        //action listener for (Continue) button
        continue_Btn = (Button) findViewById(R.id.signup_btn);
        continue_Btn.setOnClickListener(this)
        ;
        //defining objects of our page
        editTextName = (EditText) findViewById(R.id.Namefield);
        editTextEmail= (EditText) findViewById(R.id.Emailfield);
        editTextPassword = (EditText) findViewById(R.id.Passw);

        progressBar = (ProgressBar) findViewById(R.id.SignUpProgressBar);


    }//end of on create


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.go_back: // what to do when back button is pressed
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.signup_btn: // what to do when (Continue) button is pressed
                signUpUser();
                break;


        }//end of switch case
        
    }//end of click func


    // below function is initiated when (Continue) button is pressed
    private void signUpUser() {
        //creating strings out of edit texts
        String name = editTextName.getText().toString().trim();
        String mail = editTextEmail.getText().toString().trim();
        String pass = editTextPassword.getText().toString().trim();




        //checking if empty
        if(name.isEmpty()){
            editTextName.setError("Name is Required");
            editTextName.requestFocus();
            return; }
        if(mail.isEmpty()){
            editTextEmail.setError("Email is Required");
            editTextEmail.requestFocus();
            return; }
        if(pass.isEmpty()){
            editTextPassword.setError("Password is Required");
            editTextPassword.requestFocus();
            return; }

        // if mail does NOT match standard email patterns ...
        if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            editTextEmail.setError("Please enter a valid email Address");
            editTextEmail.requestFocus();
            return;
        }
        //check for password length
        if(pass.length() < 6){
            editTextPassword.setError("Minimum number of characters is 6 ... Please Try Again");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(name,mail);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                     Toast.makeText(SignUpPage.this,"User has been registered successfully",Toast.LENGTH_LONG).show();
                                     progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(SignUpPage.this,SignInPage.class)); // When Sign Up is Successful go to Sign in Page

                                 } else {
                                     Toast.makeText(SignUpPage.this,"Failed to register",Toast.LENGTH_LONG).show();
                                     progressBar.setVisibility(View.GONE);
                                 }
                                }
                            });
                        }else{

                            Toast.makeText(SignUpPage.this,"Failed to register",Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);

                        }

                    }
                });



    }//end of register func

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }



}//end of class