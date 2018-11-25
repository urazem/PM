package com.nomercy.meetly;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;


public class HeadFragment extends Fragment {

    ImageButton button3;
    ImageButton btnMenu;

    public interface onSomeEventListener {
        public void someEvent(String s);
    }

    AuthorizationFragment.onSomeEventListener someEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            someEventListener = (AuthorizationFragment.onSomeEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v1 =  inflater.inflate(R.layout.fragment_head, container, false);

        button3 = v1.findViewById(R.id.button3);
        btnMenu = v1.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeetlyApp.drawer.openDrawer(GravityCompat.START);
            }
        });
        addListenerOnButton();
        return v1;
    }

    // Сканер косаний:
    public void addListenerOnButton() {

        button3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        someEventListener.someEvent("toNewMeeting");
                    }
                }
        );
    }
}
