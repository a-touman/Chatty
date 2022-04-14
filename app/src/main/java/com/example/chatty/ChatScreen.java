package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatScreen extends AppCompatActivity {

    //declare contact profile stuff
    private TextView chatScreenFullName;
    private ImageView chatScreenProfilePic;

    //declare messaging stuff
    EditText messageInputBar;
    ImageView sendMessageButton;


    private String enteredMessage;
    String mReceiverUID,mSenderUID;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseFirestore firebaseFirestore;
    String senderRoom, receiverRoom;

    RecyclerView messageRecyclerView;
    String currentTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);





        TextView backArrow = findViewById(R.id.chatscreenbackarrow_id);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),HomePage.class));
            }
        });

        ImageView messagePlusIcon = findViewById(R.id.chatscreenplusicon_id);
        messagePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageInputBar.requestFocus();
                Snackbar.make(findViewById(android.R.id.content), "Nothing Added Yet :(", Snackbar.LENGTH_LONG).show();
            }
        });




        //define
            chatScreenProfilePic = findViewById(R.id.chatscreenimageview_id);
            chatScreenFullName = findViewById(R.id.chatscreenname_id);
            Intent intent = getIntent();

        // change toolbar's contact info into the clicked user info
        chatScreenFullName.setText(intent.getStringExtra("name"));
        Picasso.get().load(intent.getStringExtra("imageuri")).into(chatScreenProfilePic);

        //define messaging stuff
        messageInputBar = findViewById(R.id.chatscreenedittext_id);


        sendMessageButton = findViewById(R.id.chatscreensendicon_id);

        messageRecyclerView = findViewById(R.id.chatscreenRecyclerView_id);
        messagesArrayList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(ChatScreen.this,messagesArrayList);
        messageRecyclerView.setAdapter(messagesAdapter);


        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a"); // a means 12 hour format


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        mSenderUID=firebaseAuth.getUid();
        mReceiverUID = intent.getStringExtra("recieveruid");

        senderRoom = mSenderUID + mReceiverUID;
        receiverRoom = mReceiverUID + mSenderUID;




        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
        messagesAdapter = new MessagesAdapter(ChatScreen.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren() ){

                    Messages messages = snapshot1.getValue(Messages.class);
                    messagesArrayList.add(messages);

                }

                messagesAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        //what happens when when send button is pressed
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredMessage = messageInputBar.getText().toString();

                if (enteredMessage.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Message",Toast.LENGTH_SHORT).show();

                }else{

                Date date =  new Date();
                currentTime = simpleDateFormat.format(calendar.getTime());

                Messages messages = new Messages(enteredMessage,mSenderUID,date.getTime(),currentTime);

                firebaseDatabase = FirebaseDatabase.getInstance();
                firebaseDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        firebaseDatabase.getReference()
                                .child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });

                // make input bar empty again
                messageInputBar.setText(null);


                }//end of else


            }
        });







    }//end of onCreate


    //handling back button to prevent app crashing
    @Override
    public void onBackPressed() {
        //goes back to home page
        startActivity(new Intent(ChatScreen.this,HomePage.class));

    }




    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();


        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online");


    }//end of onStart method

    @Override
    public void onStop() {
        super.onStop();
        if (messagesAdapter != null){
            messagesAdapter.notifyDataSetChanged();

            DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            documentReference.update("status","Offline");


        }


    }//end of onStop method




}//end of class