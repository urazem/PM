package com.nomercy.meetly;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nomercy.meetly.api.User;

import java.util.ArrayList;
import java.util.Objects;

public class MembersActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;
    RecyclerView members;
    ArrayList<String> contacts = new ArrayList<String>();
    ArrayList<String> tell = new ArrayList<String>();
    UserAdapter adapter;
    User user;
   ArrayList<User> users = new ArrayList<>();
    ImageButton btnDone;
    StringBuilder stringBuilder = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        //getSupportLoaderManager().initLoader(1, null, this);
        members = findViewById(R.id.membersList);
        btnDone = findViewById(R.id.btnDone);
        int hasReadContactPermission = ContextCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.READ_CONTACTS);
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;

        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new
                    String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);

        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            getContacts();
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuilder = new StringBuilder();
                int i=0;
                char ch = ',';
                do{
                    User user = adapter.checkedUsers.get(i);
                    if(i == adapter.checkedUsers.size()-1) ch = '.';
                    stringBuilder.append(user.getName() + ch);
                    if(i != adapter.checkedUsers.size() -1) {
                        stringBuilder.append("\n");
                    }
                    i++;

                } while (i < adapter.checkedUsers.size());
                if(adapter.checkedUsers.size() > 0) {
                    Intent intent = new Intent();
                    intent.putExtra("names", stringBuilder.toString());
                    setResult(RESULT_OK, intent);
                    finish();


                } else {
                    Toast.makeText(MembersActivity.this, "Пожалуйста, выберите друзей", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    READ_CONTACTS_GRANTED = true;

                }
        }
        if (READ_CONTACTS_GRANTED) {
            getContacts();
        }
        else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }

    }

    public void getContacts() {
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI =
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID =
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = Objects.requireNonNull(this).getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        //Запускаем цикл обработчик для каждого контакта:
        if (cursor.getCount() > 0) {
//Если значение имени и номера контакта больше 0 (то есть они существуют) выбираем
            //их значения в приложение привязываем с соответствующие поля "Имя" и "Номер":
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
//Получаем имя:
                if (hasPhoneNumber > 0) {
                    contacts.add(name);
//contacts.add("\n Имя: " + name);
                    Cursor phoneCursor =
                            contentResolver.query(PhoneCONTENT_URI, null,
                                    Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        tell.add(phoneNumber);
                    }
                }
            }
        }
//        Toast.makeText(this, "size: " + tell.size(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "size con: " + contacts.size(), Toast.LENGTH_SHORT).show();
        if(tell.size() > contacts.size()) {
            for(int i=0; i <contacts.size(); i++) {
                user = new User(tell.get(i), contacts.get(i));
                users.add(user);
            }
        } else {
            for(int i=0; i < tell.size(); i++) {
                user = new User(tell.get(i), contacts.get(i));
                users.add(user);
            }
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapter = new UserAdapter(users, this);
        members.setLayoutManager(mLayoutManager);
        members.setItemAnimator(new DefaultItemAnimator());
        members.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        members.setAdapter(adapter);
        //contactList.setAdapter(adapter2);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
