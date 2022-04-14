package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class MoreActivity extends AppCompatActivity {



    // declare DB stuff
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    //declare Image stuff
    ImageView mviewuserimage;
    String ImageURIAccessToken;

    //declare name and email stuff
    TextView fullNameTextView;
    TextView userNameTextView;
    TextView emailAddressTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);



        //define name and email stuff
        fullNameTextView = findViewById(R.id.morepagefullname_id);
        userNameTextView = findViewById(R.id.morepageusername_id);
        emailAddressTextView = findViewById(R.id.morepageemailadress_id);

        //define DB and image stuff
        mviewuserimage = findViewById(R.id.morepageimageview_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //get image file location and pass to ImageView via Picasso Library
        storageReference = firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageURIAccessToken = uri.toString();
                Picasso.get().load(uri).into(mviewuserimage);
            }
        });

        //get profile names from Real Time Database through  UserProfile java class's getter method
        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //get Full Name
                UserProfile u1 = snapshot.getValue(UserProfile.class);
                fullNameTextView.setText(u1.getFullName());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


                Toast.makeText(getApplicationContext(),"Failed To Fetch FullName",Toast.LENGTH_SHORT).show();

            }
        });
        //get profile names from Real Time Database through User  java class's getter methods
        DatabaseReference dr= firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User u = snapshot.getValue(User.class);

                //get user name and place '@' before it
                userNameTextView.setText("@" + u.getUsername());

                //get user email address
                emailAddressTextView.setText(u.getUserEmail());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Failed To Fetch Username, Email",Toast.LENGTH_SHORT).show();
            }
        });


        TextView backbtn = (TextView) findViewById(R.id.morepagebackarrow_id);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomePage.class));
            }
        });

        //if profile screen section is clicked do the following .. take info and send it to ChangeProfile Activity
        Button changeProfile = (Button) findViewById(R.id.morepagechangeprofilebtn_id);
      //  changeProfile.setVisibility(View.);
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChangeProfile.class);
                intent.putExtra("fullname",fullNameTextView.getText().toString());
                intent.putExtra("username",userNameTextView.getText().toString());
                intent.putExtra("email",emailAddressTextView.getText().toString());
                intent.putExtra("image",ImageURIAccessToken);
                startActivity(intent);
            }
        });


        //if button icons is clicked take the user to About page
        Button aboutButton = (Button) findViewById(R.id.morepageaboutbtn_id);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),About.class));
            }
        });



    }//end of onCreate

    //on stop of this Activity (or leaving application) make the user Offline in the database..
    @Override
    protected void onStop() {
        super.onStop();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline");



    }

    //on start of this Activity (or leaving application) make the user Online in the database..
    @Override
    protected void onStart() {
        super.onStart();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online");
    }


    //handling back button IndexOutOfBound error by blocking it ..
    @Override
    public void onBackPressed() {
       Toast.makeText(this,"Can't use back button here, Use back arrow on the top instead",Toast.LENGTH_SHORT).show();
    }


}//end of class