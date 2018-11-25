package com.nomercy.meetly;

import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MeetAdapter extends RecyclerView.Adapter<MeetAdapter.MyViewHolder> {
        private ArrayList<Meet> meetList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameMeet, descriptionMeet, dateMeet;

        public MyViewHolder(View view) {
            super(view);
            nameMeet = view.findViewById(R.id.nameMeet);
            descriptionMeet =view.findViewById(R.id.descriptionMeet);
            dateMeet = view.findViewById(R.id.dataMeet);
        }
    }

    public MeetAdapter(ArrayList<Meet> meetList) {
        this.meetList = meetList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Meet meet = meetList.get(position);
        holder.nameMeet.setText(meet.getName());
        holder.descriptionMeet.setText(meet.getDescription());
        holder.dateMeet.setText(meet.getDate());
    }

    @Override
    public int getItemCount() {
        return meetList.size();
    }

    public void clear() {
        meetList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(ArrayList<Meet> list) {
        meetList.addAll(list);
        notifyDataSetChanged();
    }
}
