package com.nomercy.meetly;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nomercy.meetly.api.APIInterface;
import com.nomercy.meetly.api.Constants;
import com.nomercy.meetly.api.User;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class AuthorizationFragment extends Fragment {

    ConstraintLayout logoScreen, authorizationHeadScreen, authorizationScreen, codeInputScreen,
            registrationAccountScreen;
    EditText numberInput; // Переменная содержащая номер пользователя.
                        // Получение статуса в checkNumberLogic().
    EditText codeInput;
    EditText nameInput, surnameInput; //Переменные содержат информацию об имени и фамилии.
    Button checkNumber, nextEvent, createAccount;
    TextView authorizationType, authorizationHelp, nextEventInfo;

    String code,token,message,telephone,name,surname,photo;
    boolean responceAuth,auth;
    int id,user_id;
    Retrofit retrofit;
    ProgressBar authProgressBar, registerProgressBar, createAccountProgressBar;
    private DBHelper mDBHelper;
    String formattedTelephone;

    String userStatus;
    public boolean numberStatus; // Переменная содержащая статус номера.
                                // true - зарегистрирован. false - не зарегистрирован.
                                // Получение статуса в checkNumberLogic().

    public String codeStatus = "123"; // Переменная содержащая код подтверждения.
                            // Получение статуса в checkCodeLogic().

    public interface onSomeEventListener {
        void someEvent(String s);
    }

    onSomeEventListener someEventListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;

        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                someEventListener = (onSomeEventListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authorization, container, false);

        logoScreen = v.findViewById(R.id.logoScreen);
        authorizationHeadScreen = v.findViewById(R.id.authorizationHeadScreen);
        authorizationScreen = v.findViewById(R.id.authorizationScreen);
        codeInputScreen = v.findViewById(R.id.codeInputScreen);
        registrationAccountScreen = v.findViewById(R.id.registrationAccountScreen);

        authorizationType = v.findViewById(R.id.authorizationType);
        authorizationHelp = v.findViewById(R.id.authorizationHelp);
        nextEventInfo = v.findViewById(R.id.nextEventInfo);

        numberInput = v.findViewById(R.id.numberInput);
        codeInput = v.findViewById(R.id.codeInput);
        nameInput = v.findViewById(R.id.nameInput);
        surnameInput = v.findViewById(R.id.surnameInput);

        checkNumber = v.findViewById(R.id.checkNumber);
        nextEvent = v.findViewById(R.id.nextEvent);
        createAccount = v.findViewById(R.id.createAccount);
        authProgressBar = v.findViewById(R.id.authProgressBar);
        registerProgressBar = v.findViewById(R.id.registerProgressBar);
        createAccountProgressBar = v.findViewById(R.id.createAccProgressBar);
        authProgressBar.setVisibility(View.GONE);
        registerProgressBar.setVisibility(View.GONE);
        createAccountProgressBar.setVisibility(View.GONE);

         mDBHelper = new DBHelper(getContext());
       //  mDBHelper.deleteUser();


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

        startSystem();
        addListenerOnButton();
        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("+_ ___ ___ __ __");
        FormatWatcher formatWatcher = new MaskFormatWatcher( // форматировать текст будет вот он
                MaskImpl.createTerminated(slots)
        );
        formatWatcher.installOn(numberInput);
        return v;
    }


    // Сканер косаний:
    public void addListenerOnButton() {
        checkNumber.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkNumberLogic();
                    }
                }
        );

        // Открытие режимма:
        nextEvent.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkCodeLogic();
                    }
                }
        );
        createAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createAccountLogic();
                    }
                }
        );
    }

    void startSystem() {
        logoScreen.setVisibility(View.VISIBLE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Ожидание перехода следующий раздел приложения:
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                logoScreen.setVisibility(View.GONE);
                if(mDBHelper.isEmpty()) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    authorizationHeadScreen.setVisibility(View.VISIBLE);
                    authorizationScreen.setVisibility(View.VISIBLE);
                } else if(mDBHelper.getAuth() == 0) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    authorizationHeadScreen.setVisibility(View.VISIBLE);
                    authorizationScreen.setVisibility(View.VISIBLE);
                } else if (mDBHelper.getAuth() == 1){
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    logoScreen.setVisibility(View.GONE);
                    authorizationHeadScreen.setVisibility(View.GONE);
                    authorizationScreen.setVisibility(View.GONE);
                    codeInputScreen.setVisibility(View.GONE);
                    registrationAccountScreen.setVisibility(View.GONE);
                    someEventListener.someEvent("toMain");
                }
            }
        }, 2500);
    }

    // Проверяем номер на наличие в системе:
    void checkNumberLogic() {
        APIInterface service = retrofit.create(APIInterface.class);
        telephone = numberInput.getText().toString();
         formattedTelephone = telephone.replaceAll("\\s", "");

        if (telephone.length() < 16) {
            Toast.makeText(getContext(), "Телефон введен не верно! ", Toast.LENGTH_LONG).show();

        } else {
            authProgressBar.setVisibility(View.VISIBLE);
            authProgressBar.setProgress(20);
            authProgressBar.setMax(70);

        Call<User> call = service.post(formattedTelephone);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                response.body();
                code = response.body().getMessage();
                numberStatus = response.body().getAuth();
                Toast.makeText(getContext(), code, Toast.LENGTH_LONG);
                if (code != null)
                    codeInput.setText(code, TextView.BufferType.EDITABLE);
                else Toast.makeText(getContext(), "Ошибка на сервере", Toast.LENGTH_LONG);
                authorizationHelp.setVisibility(View.GONE);

                if (numberStatus) {
                    Toast.makeText(getContext(), String.valueOf(numberStatus), Toast.LENGTH_LONG);
                    userStatus = "user";
                    authorizationType.setText("Вход");
                    nextEvent.setText("Вход");
                    nextEventInfo.setVisibility(View.GONE);
                } else if (!numberStatus) {
                    Toast.makeText(getContext(), String.valueOf(numberStatus), Toast.LENGTH_LONG);
                    userStatus = "newUser";
                    authorizationType.setText("Регистрация");
                    nextEvent.setText("Регистрация");
                }
                authorizationScreen.setVisibility(View.GONE);
                codeInputScreen.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // handle execution failures like no internet connectivity
            }
        });
    }
        /*
        // Считывать numberInput ниеже:

        // Инициализировать numberStatus ниеже:

        // Коррекция элементов:
        authorizationHelp.setVisibility(View.GONE);

        if(numberStatus.equals("true")) {
            userStatus = "user";

            authorizationType.setText("Вход");
            nextEvent.setText("Вход");
            nextEventInfo.setVisibility(View.GONE);
        }
        else if(numberStatus.equals("false")) {
            userStatus = "newUser";

            authorizationType.setText("Регистрация");
            nextEvent.setText("Регистрация");
        }

        authorizationScreen.setVisibility(View.GONE);
        codeInputScreen.setVisibility(View.VISIBLE); */
    }


    void checkCodeLogic() {
        APIInterface service = retrofit.create(APIInterface.class);
        registerProgressBar.setVisibility(View.VISIBLE);
        registerProgressBar.setProgress(20);
        registerProgressBar.setMax(70);
        if(numberStatus) {
            final Call<User> call2 = service.postlogin(codeInput.getText().toString(), formattedTelephone);
            call2.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    response.body();
                    auth = response.body().getAuth();
                    token = response.body().getToken();
                    id = response.body().getId();
                    message = response.body().getMessage();
                    mDBHelper.addUser(id, token, 1);
                    //Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                    if (auth) {
                        Toast.makeText(getContext(), "Вход выполнен успешно!", Toast.LENGTH_LONG).show();
                        someEventListener.someEvent("toMain"); // глвынй экран
                    } else {
                        Toast.makeText(getContext(), "Неверный код", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }else {
            Call<User> call3 = service.post(codeInput.getText().toString(),formattedTelephone);
            call3.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    response.body();
                    auth = response.body().getAuth();
                    token = response.body().getToken();
                    id = response.body().getId();
                    message = response.body().getMessage();
                    mDBHelper.addUser(id, token, 1);
                    //  Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    //Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                    if(auth){
                        //Toast.makeText(getContext(), "Вы успешно зарегистрированы", Toast.LENGTH_LONG).show();
                        authorizationType.setText("Создание аккаунта");
                        codeInputScreen.setVisibility(View.GONE);
                        registrationAccountScreen.setVisibility(View.VISIBLE);

                    }else{
                        Toast.makeText(getContext(), "Неверный код", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
        /*
        // Инициализация перемнной codeStatus ниже:

        if(codeStatus.equals(codeInput.getText().toString())) {
            if(userStatus.equals("user")) {
                someEventListener.someEvent("toMain");
            }
            else if(userStatus.equals("newUser")) {
                authorizationType.setText("Создание аккаунта");

                codeInputScreen.setVisibility(View.GONE);
                registrationAccountScreen.setVisibility(View.VISIBLE);
            }
        }
        else {
            authorizationHelp.setText("Неверный код");
            authorizationHelp.setVisibility(View.VISIBLE);
        } */
    }

    void createAccountLogic() {
        APIInterface service = retrofit.create(APIInterface.class);
        createAccountProgressBar.setVisibility(View.VISIBLE);
        createAccountProgressBar.setProgress(20);
        createAccountProgressBar.setMax(70);
        name = nameInput.getText().toString();
        surname = surnameInput.getText().toString();
        user_id = id;

        final Call<User> call4 = service.post(name,surname,"LLL",user_id);
        call4.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                response.body();
                message = response.body().getMessage();
                //Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                //Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                Toast.makeText(getContext(), "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
                logoScreen.setVisibility(View.GONE);
                authorizationHeadScreen.setVisibility(View.GONE);
                authorizationScreen.setVisibility(View.GONE);
                codeInputScreen.setVisibility(View.GONE);
                registrationAccountScreen.setVisibility(View.GONE);
                someEventListener.someEvent("toMain");
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
        /*
        // окончание регистрации нового пользователя, передача имени и фамилии на сервер.
        // информаци считывать из полей nameInput и surnameInput.
        // Код взаимодествия писать ниже:

        someEventListener.someEvent("toMain");*/
    }
}

