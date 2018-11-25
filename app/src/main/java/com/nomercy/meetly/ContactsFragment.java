package com.nomercy.meetly;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;


public class ContactsFragment extends Fragment {

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;
    Intent intent;
    ArrayList<String> contacts = new ArrayList<String>();
    ArrayList<String> tell = new ArrayList<String>();
    ListView contactList;
    Button btnExitFromFriends;

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
        View v3 = inflater.inflate(R.layout.fragment_contacts, container, false);

        //Связываемся с нашим элементом TextView:
        contactList = v3.findViewById(R.id.server);

        int hasReadContactPermission = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_CONTACTS);
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;

        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new
                    String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);

        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            getContacts();
        }
        //Метод получения контактных данных
        //getContacts()

        btnExitFromFriends = v3.findViewById(R.id.btnExitFromFriends);
        addListenerOnButton();
        return v3;
    }

    // Сканер косаний:
    public void addListenerOnButton() {
        btnExitFromFriends.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MeetlyApp.class);
                        startActivity(intent);
                        someEventListener.someEvent("backToMain"); // глвынй экран
                    }
                }
        );
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
            Toast.makeText(getContext(), "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }

    }
    //Описываем метод:
    public void getContacts() {
        String phoneNumber = null;
//Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
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
        ContentResolver contentResolver = Objects.requireNonNull(getActivity()).getContentResolver();
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
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                            tell.add(phoneNumber);
                        }
                    }
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),android.R.layout.simple_list_item_1, contacts);
            //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tell);
            //Полученные данные отображаем с созданном элементе TextView:
            contactList.setAdapter(adapter);
            contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                    Toast.makeText(getContext(), tell.get(pos), Toast.LENGTH_SHORT).show();
                }
            });
            //contactList.setAdapter(adapter2);

    }
}
