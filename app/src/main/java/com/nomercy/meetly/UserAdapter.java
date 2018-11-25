package com.nomercy.meetly;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nomercy.meetly.api.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder>  {

    private Context context;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<User> checkedUsers = new ArrayList<>();

    public UserAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);

        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final User user = users.get(position);
        holder.memberName.setText(user.getName());
        holder.checkBox.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkBox.setChecked(user.isSelected);

        holder.setItemClickListener( new ItemClickListener(){
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox checkBox = (CheckBox)v;
                User member = users.get(pos);

                if(checkBox.isChecked()) {
                    member.setSelected(true);
                    checkedUsers.add(member);

                } else if(!checkBox.isChecked()) {
                    member.setSelected(false);
                    checkedUsers.remove(member);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView memberName;
        public CheckBox checkBox;

        ItemClickListener itemClickListener;

        public MyViewHolder (View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.membersName);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnClickListener(this);
        }
        public void setItemClickListener (ItemClickListener ic) {
            this.itemClickListener = ic;
        }
        @Override
        public void onClick (View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }

    }








}
