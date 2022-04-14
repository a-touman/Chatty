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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SetProfile extends AppCompatActivity {

    //Declare Image stuff
    private CardView mgetuserimageCardView;
    private ImageView mgetuserimageimageview;
    private static int PICK_IMAGE = 123;
    private Uri imagepath;
    private String ImageUriAccessToken;

    //Declare Name stuff
    private Button profileSaveBtn;
    private EditText mgetfirstname;
    private EditText mgetlastname;
    private String fullName;

    //Declare Cloud & DB stuff
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        //Define
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mgetfirstname = findViewById(R.id.profile_firstnameid);
        mgetlastname = findViewById(R.id.profile_lastnameid);
        mgetuserimageimageview = findViewById(R.id.morepageimageview_id);
        mgetuserimageCardView =findViewById(R.id.morepagecardview_id);

        //After Clicking on profile pic cardView .. open Gallery Intent
        mgetuserimageCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent,PICK_IMAGE);
            }
        });


        //Continue Button
        profileSaveBtn = (Button) findViewById(R.id.Profile_savebtnid);
        profileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //retrieve name from EditText in Profile page
                fullName = mgetfirstname.getText().toString().trim() + mgetlastname.getText().toString().trim();
                if (fullName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Name is Empty",Toast.LENGTH_LONG).show();
                }else if(imagepath==null){ // user does not select an image
                    Toast.makeText(getApplicationContext(),"Image is Empty",Toast.LENGTH_LONG).show();
                }else{

                    //start sending picture to DB


                    SendDataForNewUser();


                    //after all is finished go to next page
                   startActivity(new Intent(SetProfile.this, HomePage.class));
                    finish();
                }


            }
        });




    }//end of on create method

    private void SendDataForNewUser() {

        sendDataToRealTimeDatabase();
    }

    //send Data to RTDB
    private void sendDataToRealTimeDatabase() {

        fullName = mgetfirstname.getText().toString().trim() + " " + mgetlastname.getText().toString().trim();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        //instance object of UserProfile Class to pass to DB
        UserProfile userprofile = new UserProfile(fullName,firebaseAuth.getUid());
        databaseReference.setValue(userprofile);

        Toast.makeText(getApplicationContext(),"Profile Added Successfully",Toast.LENGTH_SHORT).show();

        sendImageToStorage();



    }//end of send RTDB function

    private void sendImageToStorage() {

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

                        sendDataToCloudFirestore();


                    }
                }).addOnFailureListener(new OnFailureListener() {    // onFail of URI imageref
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"URI get FAILED",Toast.LENGTH_LONG).show();
                    }
                });

                // onSuccess of uploadTask

                Toast.makeText(getApplicationContext(),"Image uploaded",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {  // onFail of uploadTask
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Image not Uploaded",Toast.LENGTH_LONG).show();
            }
        });


    }//end of sendImageToStorage method


    private void sendDataToCloudFirestore() {

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String,Object> userdata = new HashMap<>();
        userdata.put("name",fullName);
        userdata.put("image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");
        userdata.put("texted","NO");
        userdata.put("voiced","NO");



        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Data sent to Cloud Firestore Successfully",Toast.LENGTH_SHORT).show();
            }
        });


    }//end of sendDataToCloudFirestore method



    // Added for Deprecated function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if user selects image
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imagepath = data.getData();
            mgetuserimageimageview.setImageURI(imagepath);  }

        super.onActivityResult(requestCode, resultCode, data);

    }//end of onActivityResult Method



    //blocking user from clicking back button on this page ..
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Can't use back button here..",Toast.LENGTH_SHORT).show();
    }




}//end of class