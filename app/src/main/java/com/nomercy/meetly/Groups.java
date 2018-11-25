package com.nomercy.meetly;

import com.google.gson.annotations.SerializedName;

public class Groups {


    @SerializedName("group_id")
    public int group_id;

    @SerializedName("name")
    private String groupName;

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    private int groupImage;

    public Groups(String groupName,  int groupImage){

        this.groupName=groupName;
        this.groupImage = groupImage;
}

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getgroupImage() {
        return this.groupImage;
    }

    public void setgroupImage(int groupImage) {
        this.groupImage = groupImage;
    }


}
