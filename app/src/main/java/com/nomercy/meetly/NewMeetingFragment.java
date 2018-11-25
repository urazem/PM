package com.nomercy.meetly;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nomercy.meetly.api.APIInterface;
import com.nomercy.meetly.api.Constants;
import com.nomercy.meetly.api.User;

import java.text.DateFormat;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NewMeetingFragment extends Fragment  {

    Button button5, createMeet, btnDate, btnTime, btnPlace, btnMembers;
    Retrofit retrofit;
    EditText name, description, dateInput, timeInput, placeInput, membersInput;
    String nameS, descriptionS, date, time;
    ProgressBar createMeetProgressBar;
    DBHelper mDbHelper;
    int id;
    int place_id = 1;
    String message;

    public interface onSomeEventListener {
        void someEvent(String s);
    }

    AuthorizationFragment.onSomeEventListener someEventListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                someEventListener = (AuthorizationFragment.onSomeEventListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v2 =  inflater.inflate(R.layout.fragment_new_meeting, container, false);

        button5 = v2.findViewById(R.id.button5);
        btnDate = v2.findViewById(R.id.btnDate);
        createMeet = v2.findViewById(R.id.btn_createMeet);
        btnTime = v2.findViewById(R.id.btnTime);
        btnPlace = v2.findViewById(R.id.btnPlace);
        btnMembers = v2.findViewById(R.id.btnMembers);
        name = v2.findViewById(R.id.nameInput);
        description = v2.findViewById(R.id.descriptionInput);
        dateInput = v2.findViewById(R.id.dateInput);
        timeInput = v2.findViewById(R.id.timeInput);
        placeInput = v2.findViewById(R.id.placeInput);
        membersInput = v2.findViewById(R.id.membersInput);
        createMeetProgressBar = v2.findViewById(R.id.createMeetProgressBar);
        createMeetProgressBar.setVisibility(View.GONE);
        mDbHelper = new DBHelper(getContext());
        dateInput.setTag(dateInput.getKeyListener());
        dateInput.setKeyListener(null);
        timeInput.setTag(timeInput.getKeyListener());
        timeInput.setKeyListener(null);
        membersInput.setTag(membersInput.getKeyListener());
        membersInput.setKeyListener(null);


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

        addListenerOnButton();
        return v2;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        if(requestCode ==1) {
            String name = data.getStringExtra("name");
            placeInput.setText(name);
            place_id = data.getIntExtra("id", 1);
            // Toast.makeText(getContext(), String.valueOf(place_id), Toast.LENGTH_LONG).show();
        } else if(requestCode ==2) {
            String names = data.getStringExtra("names");
            String edit = membersInput.getText().toString();
            if(edit.length() > 0) {
                membersInput.getText().replace(edit.length()-1, edit.length(), ",");
                membersInput.append(names);
            } else membersInput.append(names);

        }
    }

    // Сканер косаний:
    public void addListenerOnButton() {
        button5.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        someEventListener.someEvent("backToMain"); // глвынй экран
                    }
                }
        );

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTime();
            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), NewPlace.class);
                startActivityForResult(intent,1);
            }
        });

        btnMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MembersActivity.class);
                startActivityForResult(intent, 2);
            }
        });




        createMeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMeet();
            }
        });
    }


    public void chooseDate() {
        DatePickerFragment date = new DatePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            dateInput.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(dayOfMonth));
        }
    };

    public void chooseTime() {
        TimePickerFragment time = new TimePickerFragment();
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", calender.get(Calendar.HOUR));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        time.setArguments(args);

        time.show(getFragmentManager(),"time picker");
        TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                timeInput.setText(String.valueOf(hour) + ":"+ String.valueOf(minute));
            }
        };
        time.setCallBack(ontime);
    }



    public void createMeet() {
       APIInterface service = retrofit.create(APIInterface.class);
       nameS = name.getText().toString();
       date = dateInput.getText().toString();
       time = timeInput.getText().toString();
       descriptionS = description.getText().toString();
       id = mDbHelper.getId();

       if (nameS.equals("")) {
           name.setError("Название встречи должно быть заполнено");
           //Toast.makeText(getContext(), "Название встречи должно быть заполнено", Toast.LENGTH_LONG).show();
       } else if (date.equals("")) {
           dateInput.setError("Дата должна быть заполнена");
       }else if(time.equals("")) {
           timeInput.setError("Время должно быть заполнено");
       }else {
           name.setError(null);
           dateInput.setError(null);
           timeInput.setError(null);
           createMeetProgressBar.setVisibility(View.VISIBLE);
           createMeetProgressBar.setProgress(20);
           createMeetProgressBar.setMax(70);

       Call<User> call = service.post(nameS, date, time, descriptionS, "photo", place_id, new int[]{62, 63, 64}, id);
       call.enqueue(new Callback<User>() {
           @Override
           public void onResponse(Call<User> call, Response<User> response) {
               response.body();
             //  message = response.body().getMessage();
           //    Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
               someEventListener.someEvent("backToMain");
               Toast.makeText(getContext(), "Встреча успешно создана. Обновите страницу", Toast.LENGTH_LONG).show();
           }

           @Override
           public void onFailure(Call<User> call, Throwable t) {
           }
       });
   }


    }

}
