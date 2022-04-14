package com.example.chatty;

import java.util.HashMap;
import java.util.Map;

public class FirebaseModel {

    // From setProfile Class Hashmap ...
//    Map<String,Object> userdata = new HashMap<>();
//        userdata.put("name",fullName);
//        userdata.put("image",ImageUriAccessToken);
//        userdata.put("uid",firebaseAuth.getUid());
//        userdata.put("status","Online");
//        userdata.put("texted","NO");
//        userdata.put("voiced","NO");


    //attributes
    String name;
    String image;
    String uid;
    String status;
    String texted;
    String voiced;


//  --------------------------- Constructors ------------------------

    //empty
    public FirebaseModel() {
    }

    //parametrized
    public FirebaseModel(String name, String image, String uid, String status, String texted,String voiced) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
        this.texted = texted;
        this.voiced = voiced;

    }

    // --------------------------Getters & Setters ---------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getTexted() {
        return texted;
    }

    public void setTexted(String texted) {
        this.texted = texted;
    }

    public String getVoiced() {
        return voiced;
    }

    public void setVoiced(String voiced) {
        this.voiced = voiced;
    }

}//end of class
