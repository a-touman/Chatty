package com.example.chatty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    //declare
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ChatFragment chatFragment;
    private CallsFragment callsFragment;
    private SearchFragment searchFragment;

    // declare DB stuff for Status Control
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        //define DB stuff
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //define

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        //get instances of our Fragment Classes
        chatFragment = new ChatFragment();
        callsFragment = new CallsFragment();
        searchFragment = new SearchFragment();

        //link tabs with the fragment container
        tabLayout.setupWithViewPager(viewPager);

        //creating Adapter Class to pick fragments via ArrayLists
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),0);

        //Adding Fragments
        viewPagerAdapter.addFragment(chatFragment,"Chat");
        viewPagerAdapter.addFragment(callsFragment,"Voice");
        viewPagerAdapter.addFragment(searchFragment,"Search");

        //link fragment container with our Adapter
        viewPager.setAdapter(viewPagerAdapter);


        //extra Aesthetic related code
        tabLayout.setTabRippleColor(null);




    }//end of on create


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        //initialize lists
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();

        //new not deprecated constructor
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment ,String Title){
            fragments.add(fragment);
            fragmentTitles.add(Title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }//end of inner class




    //on stop of this Activity (or leaving application) make the user Offline in the database..
    @Override
    public void onStop() {
        super.onStop();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Offline");

    }

    //on start of this Activity (or leaving application) make the user Online in the database..
    @Override
    public void onStart() {
        super.onStart();

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","Online");
    }

    //blocking user from going back log in page/ setProfile page from home screen ..
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Can't Go Back From Here.. ",Toast.LENGTH_SHORT).show();
    }



}//end of class