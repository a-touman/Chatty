package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChangeProfile extends AppCompatActivity {

    // declare DB stuff
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    //declare activity objs
    Button apply;
    EditText firstName;
    EditText lastName;
    TextView email;
    TextView username;
    Button logOutBtn;

    //declare image stuff
    private static final int PICK_IMAGE=123;
    private ImageView newProfilePic;
    private CardView mgetuserimageCardView;
    private Uri imagepath;
    private String ImageUriAccessToken;

    //declare
    String newFirstName;
    String newLastName;
    String newFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile);



        // define DB stuff
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //define activity objs
        newProfilePic = findViewById(R.id.changeprofileimageview_id);
        mgetuserimageCardView =findViewById(R.id.changeprofilecardview_id);
        firstName = findViewById(R.id.changeprofile_firstname_id);
        lastName = findViewById(R.id.changeprofile_lastname_id);
        email = findViewById(R.id.changeprofileuseremail_id);
        username = findViewById(R.id.changeprofileusername_id);
        logOutBtn = findViewById(R.id.changeprofilelogoutbtn_id);
        apply = findViewById(R.id.changeprofileapplybtn_id);

        Intent intent = getIntent();

        DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        //extract first name and last name from full name
        String getName = intent.getStringExtra("fullname");
        int idx = getName.lastIndexOf(' ');
        if (idx == -1){
            Toast.makeText(this,"only a single name",Toast.LENGTH_SHORT).show();
            idx = getName.length();
        }

        String fn = getName.substring(0,idx);
        String ln = getName.substring(idx + 1);

        //set first name and last name texts in edit texts
        firstName.setHint(fn);
        lastName.setHint(ln);

        //set email and user name TextViews ..
        email.setText(intent.getStringExtra("email"));
        username.setText(intent.getStringExtra("username"));

        //show old user's profile picture
        storageReference= firebaseStorage.getReference();
        storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                ImageUriAccessToken =uri.toString();
                Picasso.get().load(uri).into(newProfilePic);
            }
        });





//        newFirstName = firstName.getText().toString().trim();
//        newLastName  = lastName.getText().toString().trim();
//        newFullName  = newFirstName +" "+newLastName;

        TextView backbtn = (TextView) findViewById(R.id.changeprofilebackarrow_id);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MoreActivity.class));
                finish();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                newFirstName = firstName.getText().toString().trim();
                newLastName  = lastName.getText().toString().trim();
                newFullName  = newFirstName +" "+newLastName;



                if(newFirstName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"First Name is Empty",Toast.LENGTH_SHORT).show();
                }
                else if (newLastName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Last Name is Empty",Toast.LENGTH_SHORT).show();
                }
                else if (imagepath != null){

                    UserProfile userProfile = new UserProfile(newFullName,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);

                    updateImageToStorage();

                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();

                }else{

                    UserProfile userProfile = new UserProfile(newFullName,firebaseAuth.getUid());
                    databaseReference.setValue(userProfile);
                    updateNameToCloudFirestore();
                    Toast.makeText(getApplicationContext(),"Updated",Toast.LENGTH_SHORT).show();


                }



            }
        });








        //After Clicking on profile pic cardView .. open Gallery Intent
        mgetuserimageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });







    }//end of onCreate









    private void updateNameToCloudFirestore() {

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("name",newFullName);
        userdata.put("image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Data sent to Cloud Firestore Successfully",Toast.LENGTH_SHORT).show();
            }
        });


    }//end of update name

    private void updateImageToStorage() {
        StorageReference imageref = storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        // Compress Image to Smaller Size

        Bitmap bitmap = null;
        try{
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] ourData = byteArrayOutputStream.toByteArray();  // stores our Compressed Image
// -----------------------------Compress Finished --------------------------------------------

        //Storing Access Token and calling sendToCloud

        UploadTask uploadTask = imageref.putBytes(ourData);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {   // onSuccess of URI imageref

                        ImageUriAccessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"get URI success",Toast.LENGTH_SHORT).show();

                        updateNameToCloudFirestore();


                    }
                }).addOnFailureListener(new OnFailureListener() {    // onFail of URI imageref
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"URI get FAILED",Toast.LENGTH_LONG).show();
                    }
                });

                // onSuccess of uploadTask

                Toast.makeText(getApplicationContext(),"Image updated",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {  // onFail of uploadTask
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not Updated",Toast.LENGTH_LONG).show();
            }
        });


    }//end of update image


    // Added for Deprecated function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if user selects image
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imagepath = data.getData();
            newProfilePic.setImageURI(imagepath);  }

        super.onActivityResult(requestCode, resultCode, data);

    }//end of onActivityResult Method


    //handling back button to prevent app crashing
    @Override
    public void onBackPressed() {
        //goes back to More Activity
        startActivity(new Intent(ChangeProfile.this,MoreActivity.class));

    }

    //on stop of this Activity (or leaving application) make the user Offline in the database..
    @Override
    protected void onStop() {
        super.onStop();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline");
        finish();
    }

    //on start of this Activity (or leaving application) make the user Online in the database..
    @Override
    protected void onStart() {
        super.onStart();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online");
    }






}//end of class