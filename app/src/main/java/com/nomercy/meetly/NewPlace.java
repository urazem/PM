package com.nomercy.meetly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewPlace extends AppCompatActivity {
    private List<Place> placeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PlaceAdapter mAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetly_app);



        recyclerView = (RecyclerView) findViewById(R.id.feed);

        mAdapter = new PlaceAdapter(placeList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
       // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        swipeContainer =  findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);

            }
        });
        preparePlaceData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Place place = placeList.get(position);
                Intent intent = new Intent();
                intent.putExtra("name", place.getPlaceName());
                intent.putExtra("id", place.getId());
                setResult(RESULT_OK, intent);
                finish();
                //Toast.makeText(getApplicationContext(), place.getPlaceName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }



    private void preparePlaceData() {
        Place place = new Place(1,"Merry Berry", "ул. Горького, 5а, Симферополь", R.drawable.merry_berry_place);
        placeList.add(place);
        place = new Place(2,"ШашлыкоFF", "Симферополь, просп. Кирова 24", R.drawable.shashlik);
        placeList.add(place);
        place = new Place(3,"Garry's", "ул. Карла Маркса, Симферополь", R.drawable.garry);
        placeList.add(place);
        place = new Place(4,"Ресторан Мартини", "ул. Ушинского, 4, Симферополь", R.drawable.martini);
        placeList.add(place);
        place = new Place(5,"Shen", "ул. Генерала Васильева, 40, Симферополь", R.drawable.shen);
        placeList.add(place);
        place = new Place(6,"Кофейня Rabbit Burrow", "просп. Кирова, 11, Симферополь", R.drawable.rabbit);
        placeList.add(place);

    }
}