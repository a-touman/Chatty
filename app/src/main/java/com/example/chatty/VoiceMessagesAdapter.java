package com.example.chatty;

import android.app.DownloadManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;


//declare



public class VoiceMessagesAdapter extends RecyclerView.Adapter {


    StorageReference ref;
    StorageReference storageReference;




    Context context;
    ArrayList<VoiceMessages> voiceMessagesArrayList;

    int ITEM_SEND = 1;
    int ITEM_RECEIVE= 2;

    //constructor
    public VoiceMessagesAdapter(Context context, ArrayList<VoiceMessages> voicemessagesArrayList) {
        this.context = context;
        this.voiceMessagesArrayList = voicemessagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate the sender or receiver voice message layout according to ITEM_SEND/ITEM_RECEIVE
        if(viewType == ITEM_SEND){

            View view = LayoutInflater.from(context).inflate(R.layout.sendervoicechatlayout,parent,false);
            return new SenderViewHolder(view);
        }else{

            View view = LayoutInflater.from(context).inflate(R.layout.receivevoicechatlayout,parent,false);
            return new ReceiverViewHolder(view);

        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        VoiceMessages voiceMessages = voiceMessagesArrayList.get(position);
        VoiceScreen vs = new VoiceScreen();

        if (holder.getClass() == SenderViewHolder.class){

            SenderViewHolder viewHolder = (SenderViewHolder) holder;
           // viewHolder.voicePlayerView.setAudio(vs.getRecordingFilePath()); // error likely to occur due to name and calling getting different system time
            viewHolder.voicePlayerView.setAudio(vs.filepath);
            viewHolder.textViewVoiceMessageTime.setText(voiceMessages.getCurrentTime());

        }else{


//
//            storageReference = FirebaseStorage.getInstance().getReference();
//            ref=storageReference.child("Voices").child(voiceMessages.getAudioName());
//
//            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//
//                    String url = uri.toString();
//
//                    downloadFile(context,voiceMessages.getAudioName(),".3gp",Environment.DIRECTORY_DOWNLOADS,url);
//
//
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context,"URl Fetch Fail",Toast.LENGTH_SHORT).show();
//
//                }
//            });
//
//
//
//            ContextWrapper contextWrapper = new ContextWrapper(context.getApplicationContext());
//            File downloadDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
//            File file = new File(downloadDirectory,voiceMessages.getAudioName());
//
//
//
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
//            viewHolder.voicePlayerView.setAudio(file.getPath());
            viewHolder.textViewVoiceMessageTime.setText(voiceMessages.getCurrentTime());















        }




    }

    //overridden manually from list
    @Override
    public int getItemViewType(int position) {

        VoiceMessages voiceMessages = voiceMessagesArrayList.get(position);
        //this checks if we are sending or receiving message by comparing senderId to our UID
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(voiceMessages.getSenderId())){
            return  ITEM_SEND;
        }else{
            return  ITEM_RECEIVE;
        }



    }//end of getItemViewType method


    @Override
    public int getItemCount() {
        return voiceMessagesArrayList.size();
    }







    class SenderViewHolder extends RecyclerView.ViewHolder{

        VoicePlayerView voicePlayerView;
        TextView textViewVoiceMessageTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

             voicePlayerView    = itemView.findViewById(R.id.voicePlayerView_id);
            textViewVoiceMessageTime = itemView.findViewById(R.id.timeofmessage_id);


        }

    }//end of Sender inner class



    class ReceiverViewHolder extends RecyclerView.ViewHolder{

        VoicePlayerView voicePlayerView;
        TextView textViewVoiceMessageTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);


            voicePlayerView = itemView.findViewById(R.id.voicePlayerView_id);
            textViewVoiceMessageTime = itemView.findViewById(R.id.timeofmessage_id);

        }

    }//end of Receiver inner class


//
//    public void downloadFile(Context context,String fileName,String fileExtention,String destinationDirectory,String url) {
//        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri = Uri.parse(url);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalFilesDir(context,destinationDirectory,fileName+fileExtention);
//
//        downloadManager.enqueue(request);
//
//
//    }
//
//
//





}//end of class
