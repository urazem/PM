package com.nomercy.meetly;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nomercy.meetly.api.APIInterface;
import com.nomercy.meetly.api.Constants;
import com.nomercy.meetly.api.User;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupsMain extends AppCompatActivity {
    private List<Groups> groupsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private GroupsAdapter mAdapter;
    FloatingActionButton fab;
    DBHelper mDbHelper;
    Retrofit retrofit;
    String groupName;
    int user_id;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_groups);
        mDbHelper = new DBHelper(this);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        //The logging interceptor will be added to the http client
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BaseUrl)
                .build();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerGroups);

//        mAdapter = new GroupsAdapter(groupsList);
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
        swipeContainer =  findViewById(R.id.swipeContainer);


        preparePlaceData();
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
             preparePlaceData();


            }
        });
        fab = (FloatingActionButton) findViewById(R.id.fabBtn);

//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                Groups groups = groupsList.get(position);
//                Toast.makeText(getApplicationContext(), groups.getGroupName() + " is selected!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsMain.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
                final EditText nameGroup = mView.findViewById(R.id.inputNameGroup);
                Button ok = mView.findViewById(R.id.btnOk);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupName = nameGroup.getText().toString();
                        user_id = mDbHelper.getId();
                        if(groupName.isEmpty()) {
                            nameGroup.setError("Название группы должно быть заполнено");
                        } else {
                            nameGroup.setError(null);
                            APIInterface service = retrofit.create(APIInterface.class);
                            Call<Groups> call = service.post(groupName, user_id);
                            call.enqueue(new Callback<Groups>() {
                                @Override
                                public void onResponse(Call<Groups> call, Response<Groups> response) {
                                    response.body();
                                    Toast.makeText(GroupsMain.this, "Группа успешно создана. Обновите страницу", Toast.LENGTH_LONG).show();
                                }
                                @Override
                                public void onFailure(Call<Groups> call, Throwable t) {
                                }
                            });

                        }
                    }
                });
                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }



    private void preparePlaceData() {
        APIInterface service = retrofit.create(APIInterface.class);
        int id = mDbHelper.getId();
        Call<GroupList> call = service.getGroup(id);
        call.enqueue(new Callback<GroupList>() {
            @Override
            public void onResponse(Call<GroupList> call, Response<GroupList> response) {
                generateGroup(response.body().getGroupArrayList());

            }

            @Override
            public void onFailure(Call<GroupList> call, Throwable t) {
                // handle execution failures like no internet connectivity
            }
        });

//        Groups groups = new Groups("Family", R.drawable.ic_people_black_24dp);
//        groupsList.add(groups);
//        groups = new Groups("Friend", R.drawable.ic_people_black_24dp);
//        groupsList.add(groups);
//        groups = new Groups("Work", R.drawable.ic_people_black_24dp);
//        groupsList.add(groups);
    }

    public void generateGroup(ArrayList<Groups> groupDataList) {
        recyclerView = findViewById(R.id.recyclerGroups);
        mAdapter = new GroupsAdapter(groupDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        swipeContainer.setRefreshing(false);

    }
}