package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

public class VoiceScreen extends AppCompatActivity {


    StorageReference ref;
    StorageReference storageReference;



    //declare contact profile stuff
    private TextView voiceScreenFullName;
    private ImageView voiceScreenProfilePic;

    //declare DB stuff
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;


    //declare recording stuff

    String filepath;

    ImageView recordImage;
    ProgressBar progressBar;
    MediaRecorder recorder;
   // StorageReference storageReference;
    FirebaseStorage storage;
    String nameOfAudioFile;
    private static final String LOG_TAG = "Record_log";

    //declare chatting stuff

    Uri fileuri;

    RecyclerView messageRecyclerView;
    ArrayList<VoiceMessages> voiceMessagesArrayList;
    VoiceMessagesAdapter voiceMessagesAdapter;
    String senderRoom, receiverRoom;
    String mReceiverName,mSenderName,mReceiverUID,mSenderUID;
    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_screen);

        //define
        Intent intent = getIntent();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();


        voiceScreenProfilePic = findViewById(R.id.voicescreenimageview_id);
        voiceScreenFullName = findViewById(R.id.voicescreenname_id);


        //define recording stuff
        recordImage = findViewById(R.id.voicescreenrecordicon_id);
        progressBar = findViewById(R.id.recordprogressBar_id);
        progressBar.setVisibility(View.INVISIBLE);

        storage = FirebaseStorage.getInstance();

        //define chatting stuff

        messageRecyclerView = findViewById(R.id.voicescreenRecyclerView_id);
        voiceMessagesArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        voiceMessagesAdapter = new VoiceMessagesAdapter(VoiceScreen.this,voiceMessagesArrayList);
        messageRecyclerView.setAdapter(voiceMessagesAdapter);


        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a"); // a means 12 hour format


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mSenderUID=firebaseAuth.getUid();
        mReceiverUID = intent.getStringExtra("recieveruid");

        senderRoom = mSenderUID + mReceiverUID;
        receiverRoom = mReceiverUID + mSenderUID;


        filepath = getRecordingFilePath();
        nameOfAudioFile = getName();




        // change toolbar's contact info into the clicked user info

        voiceScreenFullName.setText(intent.getStringExtra("name"));
        Picasso.get().load(intent.getStringExtra("imageuri")).into(voiceScreenProfilePic);

        TextView backArrow = findViewById(R.id.voicescreenbackarrow_id);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomePage.class));
            }
        });





        DatabaseReference databaseReference = firebaseDatabase.getReference().child("voice_chats").child(senderRoom).child("messages");
        voiceMessagesAdapter = new VoiceMessagesAdapter(VoiceScreen.this,voiceMessagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voiceMessagesArrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren() ){

                    VoiceMessages voiceMessages = snapshot1.getValue(VoiceMessages.class);
                    voiceMessagesArrayList.add(voiceMessages);

                }

                voiceMessagesAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        recordImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();
                    progressBar.setVisibility(View.VISIBLE);


                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();
                    progressBar.setVisibility(View.INVISIBLE);

                    // Wait for 2 seconds
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //
                uploadVoice();



                        }
                    }, 2000);   //2 seconds


                    // Wait for 5 seconds
                    Handler handler5 = new Handler();
                    handler5.postDelayed(new Runnable() {
                        public void run() {


                         //   sendtoDB();



                        }
                    }, 5000);   //5 seconds



                }


                return true;
            }
        });












    }//end of on Create


    public String getName(){
        String gtName = makeName();
      return  gtName;


    }


    private String makeName(){
        String mkName = "Audio" +"_"+firebaseAuth.getUid()+"_"+System.currentTimeMillis();
        return mkName ;

    }



    protected String getRecordingFilePath(){

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory,nameOfAudioFile+".3gp");
        return file.getPath();

    }









    private boolean checkPermissions(){


        int first = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int second =ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return first == PackageManager.PERMISSION_GRANTED && second == PackageManager.PERMISSION_GRANTED;

    }


    private void startRecording() {

        if (checkPermissions()){



            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(filepath);

            try {
                recorder.prepare();
                recorder.start();
                Toast.makeText(VoiceScreen.this,"Recording started",Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "prepare() "+" file name :  "+getRecordingFilePath());
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed"+" file name :  "+getRecordingFilePath());
                System.out.println(""+e);    //to display the error
            }



        }else{

            ActivityCompat.requestPermissions(VoiceScreen.this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        }


    }//end of start Recording


    private void stopRecording() {

        recorder.stop();
        recorder.release();
        Toast.makeText(VoiceScreen.this,"Recording stopped",Toast.LENGTH_SHORT).show();
        recorder = null;






    }//end of stop Recording




    private void uploadVoice(){

        StorageReference storageRef = storage.getReference();

        Toast.makeText(VoiceScreen.this,"Uploading Voice to Storage",Toast.LENGTH_SHORT).show();


        fileuri = Uri.fromFile(new File(filepath));
        StorageReference AudioRef = storageRef.child("Voices").child(nameOfAudioFile);
        UploadTask uploadTask = AudioRef.putFile(fileuri);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(VoiceScreen.this,"Uploading to Storage Done",Toast.LENGTH_SHORT).show();
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VoiceScreen.this,"Uploading to Storage Failed",Toast.LENGTH_SHORT).show();
            }
        });


    }//end of upload voice























    //handling back button to prevent app crashing
    @Override
    public void onBackPressed() {
        //goes back to home page
        startActivity(new Intent(VoiceScreen.this,HomePage.class));
    }




    @Override
    public void onStart() {
        super.onStart();
        voiceMessagesAdapter.notifyDataSetChanged();


        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online");


    }//end of onStart method

    @Override
    public void onStop() {
        super.onStop();

        if (voiceMessagesAdapter != null) {
            voiceMessagesAdapter.notifyDataSetChanged();
        }


            DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            documentReference.update("status", "Offline");




    }//end of on stop




    private void sendtoDB(){



        Date date =  new Date();
        currentTime = simpleDateFormat.format(calendar.getTime());

        VoiceMessages voiceMessages = new VoiceMessages(nameOfAudioFile,fileuri,mSenderUID,date.getTime(),currentTime);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("voice_chats")
                .child(senderRoom)
                .child("messages")
                .push().setValue(voiceMessages).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseDatabase.getReference()
                        .child("voice_chats")
                        .child(receiverRoom)
                        .child("messages")
                        .push().setValue(voiceMessages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });



    }//end of sendtoDB


    public void downloadFile(Context context, String fileName, String fileExtention, String destinationDirectory, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtention);

        downloadManager.enqueue(request);


    }







}//end of class