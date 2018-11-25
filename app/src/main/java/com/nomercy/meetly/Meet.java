package com.nomercy.meetly;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Meet implements Serializable {
    @SerializedName("meet_id")
    public int meet_id;
    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
    @SerializedName("date")
    public String date;
    @SerializedName("time")
    public String time;
    @SerializedName("photo")
    public String photo;
    @SerializedName("place_id")
    public int place_id;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public Meet(String name, String description, String date) {
        this.name = name;
        this.description = description;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
