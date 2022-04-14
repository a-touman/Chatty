package com.example.chatty;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CallsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CallsFragment extends Fragment {

    //declare DB Stuff
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    //declare RecyclerView Stuff
    LinearLayoutManager linearLayoutManager;
    FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder3> adapter ;
    RecyclerView voiceRecyclerView;

    //declare contact profile stuff
    ImageView userPic;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CallsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CallsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CallsFragment newInstance(String param1, String param2) {
        CallsFragment fragment = new CallsFragment();
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
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        ImageView moreDots = (ImageView) view.findViewById(R.id.callfragmore_id);

        moreDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MoreActivity.class));
            }
        });


        //message to icon in bottom bar
        ImageView icon = (ImageView) view.findViewById(R.id.callfragicon_id);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Doesn't do anything yet..", Snackbar.LENGTH_LONG).show();
            }
        });


        //message to icon in bottom bar
        ImageView voiceIcon = (ImageView) view.findViewById(R.id.callfragicon_id);

        voiceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Doesn't do anything yet..", Snackbar.LENGTH_LONG).show();
            }
        });

        //define
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        voiceRecyclerView = view.findViewById(R.id.voicefragmentRecyclerView_id);


        //name of collection is 'Users' ..... document is 'uid' ..... fields are image, fullName,status,uid

        Query query = firebaseFirestore.collection("Users") // if we stop here it gets all users in database
                .whereNotEqualTo("uid",firebaseAuth.getUid())// here we exclude the user with field uid that matches our uid (we exclude ourself)
                .whereEqualTo("voiced","YES"); //here we only get the contacts that we voiced before to place them in the Voice (old Calls) fragment

        FirestoreRecyclerOptions<FirebaseModel> allusernames;

        allusernames= new FirestoreRecyclerOptions.Builder<FirebaseModel>()
                .setQuery(query,FirebaseModel.class).build();

        adapter = new FirestoreRecyclerAdapter<FirebaseModel, CallsFragment.NoteViewHolder3>(allusernames) {
            @Override
            protected void onBindViewHolder(@NonNull CallsFragment.NoteViewHolder3 holder, int position, @NonNull FirebaseModel model) {
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
                //what happens when you click on a chat contact
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(),(model.getName()+" item is clicked"),Toast.LENGTH_LONG).show();

                        // go to chat screen and send receiver data along side with it
                        Intent intent = new Intent(getActivity(),VoiceScreen.class);

                        intent.putExtra("name",model.getName());
                        intent.putExtra("recieveruid",model.getUid());
                        intent.putExtra("imageuri",model.getImage());
                        startActivity(intent);




                    }
                });


            }//end of onBindViewHolder

            @NonNull
            @Override
            public NoteViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchviewlayout,parent,false);
                return new NoteViewHolder3(v);
            }
        };




        //Configuring the RecyclerView of our searchFragment
        voiceRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        voiceRecyclerView.setLayoutManager(linearLayoutManager);
        voiceRecyclerView.setAdapter(adapter);










        return view;
    }//end of onCreateView





    //inner class starts ... contact attributes name, status, picture are declared and defined
    public class NoteViewHolder3 extends RecyclerView.ViewHolder{

        private TextView individualUserName;
        private TextView userStatus;

        public NoteViewHolder3(@NonNull View itemView) {
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





}//end of Call Fragment