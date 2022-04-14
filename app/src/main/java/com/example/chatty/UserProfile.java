package com.example.chatty;

public class UserProfile {

    //class attributes

    private String fullName;
    private String userUID;


    //                                                              Class Constructors

    //default
    public UserProfile() { }

    //parametrized Constructor
    public UserProfile(String fullName, String userUID) {
        this.fullName = fullName;
        this.userUID = userUID;
    }





//                                                                  Setters & Getters



    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }


}//end of class
