package com.example.chatty;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    //declare DB Stuff
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    //declare RecyclerView Stuff
    LinearLayoutManager linearLayoutManager;
    FirestoreRecyclerAdapter<FirebaseModel,NoteViewHolder> adapter ;
    RecyclerView searchRecyclerView;

    //declare contact profile stuff
    ImageView userPic;
    EditText searchBar;

    //declare popup stuff
    Dialog dialog;
    ImageView popupChatButton;
    ImageView popupVoiceButton;






    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        // Inflate the layout for this fragment
    View view =  inflater.inflate(R.layout.fragment_search, container, false);

        //add functionality to more button
       ImageView moreDots = (ImageView) view.findViewById(R.id.searchfragmore_id);

       moreDots.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getActivity(),MoreActivity.class));
           }
       });

        //message to icon in bottom bar
        ImageView icon = (ImageView) view.findViewById(R.id.searchfragicon_id);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Doesn't do anything yet..", Snackbar.LENGTH_LONG).show();
            }
        });


        //define
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        searchRecyclerView = view.findViewById(R.id.searchfragmentRecyclerView_id);

        searchBar = view.findViewById(R.id.searchFragSearchField);
        searchBar.setEnabled(false);

        dialog = new Dialog(getActivity());





        //name of collection is 'Users' ..... document is 'uid' ..... fields are image, fullName,status,uid

        Query query = firebaseFirestore.collection("Users") // if we stop here it gets all users in database
                .whereNotEqualTo("uid",firebaseAuth.getUid()); // here we exclude the user with field uid that matches our uid (we exclude ourself)

        FirestoreRecyclerOptions<FirebaseModel> allusernames;

        allusernames= new FirestoreRecyclerOptions.Builder<FirebaseModel>()
                .setQuery(query,FirebaseModel.class).build();


        adapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allusernames) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull FirebaseModel model) {
                //retrieve full name
                holder.individualUserName.setText(model.getName());

                //retrieve image uri token and load it with Picasso
                String uri = model.getImage();
                Picasso.get().load(uri).into(userPic);

                //retrieve status and assign color accordingly
                if(model.getStatus().equals("Online")){
                    holder.userStatus.setText(model.getStatus());
                    holder.userStatus.setTextColor(Color.parseColor("#00A300"));

                } else {
                    holder.userStatus.setText(model.getStatus());
                    holder.userStatus.setTextColor(Color.parseColor("#FF0000" ));

                }
                // what happens when we click on the contact item ...
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // Toast.makeText(getActivity(),(model.getName()+" item is clicked"),Toast.LENGTH_LONG).show();

                        dialog.setContentView(R.layout.popup);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        Window window = dialog.getWindow();
                        window.setGravity(Gravity.CENTER);
                        window.getAttributes().windowAnimations = R.style.DialogAnimation;



                        //put user's full name inside the popup dialog
                        TextView popupFullName = dialog.findViewById(R.id.popupfullname_id);
                        popupFullName.setText(model.getName());

                        //put user's picture inside the popup dialog
                        ImageView popupProfilePic = dialog.findViewById(R.id.popupimageview_id);
                        Picasso.get().load(uri).into(popupProfilePic);

                        //put user's username inside the popup dialog
                        TextView popupUserName = dialog.findViewById(R.id.popupusername_id);

                        DatabaseReference userNameRef = FirebaseDatabase.getInstance().getReference("Users").child(model.getUid()).child("username");
                        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String username = dataSnapshot.getValue().toString();
                                popupUserName.setText("@"+username);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });







                        //define popup call button and what it does
                        popupVoiceButton = dialog.findViewById(R.id.popupvoiceicon_id);
                        popupVoiceButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(getActivity().findViewById(android.R.id.content),
                                        "Doesn't do anything yet..", Snackbar.LENGTH_LONG).show();

                                //make user's voiced string = Yes
                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(model.getUid());
                                documentReference.update("voiced","YES");

                                //send to chatting Screen
                                Intent intent = new Intent(getActivity(),VoiceScreen.class);
                                intent.putExtra("name",model.getName());
                                intent.putExtra("recieveruid",model.getUid());
                                intent.putExtra("imageuri",model.getImage());

                                startActivity(intent);


                            }
                        });

                        //define popup chat button and what it does
                        popupChatButton = dialog.findViewById(R.id.popupchaticon_id);
                        popupChatButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //make user's Texted string = Yes
                                DocumentReference documentReference = firebaseFirestore.collection("Users").document(model.getUid());
                                documentReference.update("texted","YES");

                                //send to chatting Screen
                                Intent intent = new Intent(getActivity(),ChatScreen.class);
                                intent.putExtra("name",model.getName());
                                intent.putExtra("recieveruid",model.getUid());
                                intent.putExtra("imageuri",model.getImage());

                                startActivity(intent);




                            }
                        });


                        dialog.show();


                    }
                });

            }//end of onBindViewHolder

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchviewlayout,parent,false);
                return new NoteViewHolder(v);
            }
        };
        //Configuring the RecyclerView of our searchFragment  
        searchRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecyclerView.setAdapter(adapter);


       return view;

    }//end of onCreateView






    //inner class starts ... contact attributes name, status, picture are declared and defined
    public class NoteViewHolder extends RecyclerView.ViewHolder{

        private TextView individualUserName;
        private TextView userStatus;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            individualUserName = itemView.findViewById(R.id.searchviewlayoutnameofuser_id);
            userStatus = itemView.findViewById(R.id.searchviewlayoutstatusofuser_id);
            userPic = itemView.findViewById(R.id.searchviewlayoutuserimageview_id);




        }

    }//end of NoteViewHolder2 inner class




    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();


    }//end of onStart method

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.stopListening();
        }


    }//end of onStop method






}//end of fragment class