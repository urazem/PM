package com.nomercy.meetly;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nomercy.meetly.api.APIInterface;
import com.nomercy.meetly.api.Constants;
import com.nomercy.meetly.api.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MeetlyApp extends AppCompatActivity implements AuthorizationFragment.onSomeEventListener,NavigationView.OnNavigationItemSelectedListener{

    // Инициализация переменных и объектов:
    FrameLayout frameHead, frameBody;
    RecyclerView feed;
    ConstraintLayout meetsScreen, newMeetBar;
    int id;
    Activity activity;

    TextView txtEmpty;
    Adapter adapter;
    LinearLayoutManager manager;
    private SwipeRefreshLayout swipeContainer;

    private ArrayList<Meet> meetList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MeetAdapter mAdapter;
    Retrofit retrofit;
    DBHelper mDBHelper;
    int auth;


    public static DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetly_app);
     //   txtEmpty = findViewById(R.id.txtMeetIsEmpty);



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
        mDBHelper = new DBHelper(this);
        // Определение переменных и объектов:
        frameHead = findViewById(R.id.frameHead);
        frameBody = findViewById(R.id.frameBody);
        meetsScreen = findViewById(R.id.meetsScreen);
        newMeetBar = findViewById(R.id.newMeetScreen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         drawer = (DrawerLayout) findViewById(R.id. );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getMeets();
        swipeContainer =  findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMeets();


            }
        });

        authorization();
        addListenerOnButton();


    }

    public void getMeets() {
        APIInterface service = retrofit.create(APIInterface.class);
        int id = mDBHelper.getId();
            Call<MeetList> call = service.get(id);
            call.enqueue(new Callback<MeetList>() {
                @Override
                public void onResponse(Call<MeetList> call, Response<MeetList> response) {
                  generateMeets(response.body().getMeetArrayList());

                }

                @Override
                public void onFailure(Call<MeetList> call, Throwable t) {
                    // handle execution failures like no internet connectivity
                }
            });

    }




    public void generateMeets (ArrayList<Meet> meetDataList) {
        recyclerView = findViewById(R.id.feed);
        mAdapter = new MeetAdapter(meetDataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        swipeContainer.setRefreshing(false);


    }

    // Сканер косаний:
    public void addListenerOnButton() {
    }


    // Fragment listener:
    @Override
    public void someEvent(String s) {
        switch(s) {
            case "toMain":
                // Переход к главному экрану:
                frameBody.setVisibility(View.GONE);
                frameHead.setVisibility(View.VISIBLE);
                meetsScreen.setVisibility(View.VISIBLE);


                HeadFragment headFragment = new HeadFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.frameHead, headFragment)
                        .commit();

                //feed();
                break;

            case "toNewMeeting":
                // Переход к созданию встречи:
                meetsScreen.setVisibility(View.GONE);


                NewMeetingFragment newMeetingFragment = new NewMeetingFragment();
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.frameBody, newMeetingFragment)
                        .commit();
                frameBody.setVisibility(View.VISIBLE);
                break;

            case "backToMain":
                frameBody.setVisibility(View.GONE);
                meetsScreen.setVisibility(View.VISIBLE);
        }
    }


    // Переход к авторизации:
    void authorization() {
        frameBody.setVisibility(View.VISIBLE);
        AuthorizationFragment authorizationFragment = new AuthorizationFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.frameBody, authorizationFragment)
                .commit();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btnMenu) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings) {
//            String activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName();
//            if(!activeFragment.equals(BookmarkFragment.class.getSimpleName())) {
//                goToFragment(bookmarkFragment, false);
//            }
        }

        if (id == R.id.nav_group) {
            Intent intent = new Intent(this, GroupsMain.class);
            startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_money) {

        } else if (id == R.id.nav_help) {

        }  else if (id == R.id.nav_share) {
            Intent myIntent = new Intent(Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String shareBody = "Зацени какое крутое приложение! Держи ссылку на скачивание: ...";
            String shareSub = "Yout Subject here";
            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
            myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
            startActivity(Intent.createChooser(myIntent,"Поделиться"));

        } else if (id == R.id.nav_friends) {
            meetsScreen.setVisibility(View.GONE);
            frameHead.setVisibility(View.GONE);
            frameBody.setVisibility(View.GONE);

            ContactsFragment contactsFragment = new ContactsFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            fragmentManager1.beginTransaction()
                    .replace(R.id.frameBody, contactsFragment)
                    .commit();
            frameBody.setVisibility(View.VISIBLE);

        }  else if (id == R.id.nav_exit) {
            APIInterface service = retrofit.create(APIInterface.class);
            final int user_id = mDBHelper.getId();

            Call<User> call = service.logout(id);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    mDBHelper.deleteUser(user_id);
                    finishAffinity();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    // handle execution failures like no internet connectivity
                }

            });



        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }









    // Переход к главному экрану:
//    void feed() {
//        meetsScreen.setVisibility(View.VISIBLE);
//        manager = new LinearLayoutManager(this);
//
//        final String[] a = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "12", "14", "15", "16", "17"};
//        final ArrayList<String> list = new ArrayList<>(Arrays.asList(a));
//
//        adapter = new Adapter(list, this);
//        feed.setAdapter(adapter);
//        feed.setLayoutManager(manager);
//
//        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                String name = list.get(position);
//                Toast.makeText(MeetlyApp.this, name + " was clicked!", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
