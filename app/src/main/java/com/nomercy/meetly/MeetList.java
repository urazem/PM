package com.nomercy.meetly;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MeetList implements Serializable {
    @SerializedName("meets")
    private ArrayList<Meet> meetList;

    public ArrayList<Meet> getMeetArrayList() {
        return meetList;
    }

    public void setMeetArrayList(ArrayList<Meet> meetArrayList) {
        this.meetList = meetArrayList;
    }
}
