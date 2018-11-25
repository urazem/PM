package com.nomercy.meetly.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {

    @SerializedName("telephone")
    public String telephone;

    @SerializedName("auth")
    public boolean auth;

    @SerializedName("message")
    private String message;

    @SerializedName("token")
    public String token;
    @SerializedName("name")
    public String name;
    @SerializedName("surname")
    public String surname;
    @SerializedName("photo")
    public String photo;

    @SerializedName("id")
    public int id;

    @SerializedName("user_id")
    private String user_id;

    public boolean isSelected;

    public User(String telephone, String name) {
        this.telephone = telephone;
        this.name = name;
    }

    public void setSelected (boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected () {
        return isSelected;
    }

//    public User(int id, String token) {
//        this.id = id;
//        this.token = token;
//    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }





    public User() {
    }

    public void setTelephone(String telephone){
        this.telephone = telephone;
    }
    public String getTelephone(){
        return telephone;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }
}
