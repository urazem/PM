package com.nomercy.meetly;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroupList {
    @SerializedName("groups")
    private ArrayList<Groups> groupList;

    public ArrayList<Groups> getGroupArrayList() {
        return groupList;
    }

    public void setGroupArrayList(ArrayList<Groups> groupArrayList) {
        this.groupList = groupArrayList;
    }
}
