package com.example.chatty;

public class User {


    //declare attributes
    private String username;
    private String userEmail;

    //                                                              Class Constructors


    //Default constructor
    public User(){

    }
    //Parameterized constructor
    public User(String username,String userEmail){
        this.username = username;
        this.userEmail = userEmail;
    }

    //                                                                  Setters & Getters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }




}//end of User Class
