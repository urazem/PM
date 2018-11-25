package com.nomercy.meetly;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GroupsAdapter  extends RecyclerView.Adapter <GroupsAdapter.GroupsViewHolder>{
    private List<Groups> groupsList;

    public class GroupsViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;
        public ImageView groupImage;

        public GroupsViewHolder(View view) {
            super(view);
            groupName = (TextView) view.findViewById(R.id.nameGroup);
            groupImage = (ImageView) view.findViewById(R.id.icon_group);
        }
    }


    public GroupsAdapter(List<Groups> groupsList) {
        this.groupsList = groupsList;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_group, parent, false);

        return new GroupsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        Groups groups = groupsList.get(position);
        holder.groupName.setText(groups.getGroupName());
        holder.groupImage.setImageResource(groups.getgroupImage());
        //holder.groupImage.setText(groups.getgroupImage());
    }


    @Override
    public int getItemCount() {
        return groupsList.size();
    }
}

