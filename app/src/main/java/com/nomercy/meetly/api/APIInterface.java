package com.nomercy.meetly.api;

import com.nomercy.meetly.GroupList;
import com.nomercy.meetly.Groups;
import com.nomercy.meetly.MeetList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIInterface {
    @FormUrlEncoded
    @POST("/api/auth/authenticate")
    Call<User>post(
            @Field("telephone") String method
    );
    @FormUrlEncoded
    @POST("/api/auth/registerConfirmSms")
    Call<User>post(
            @Field("code") String code,
            @Field("telephone") String telephone
    );
    @FormUrlEncoded
    @POST("/api/auth/loginConfirmSms")
    Call<User>postlogin(
            @Field("code") String code,
            @Field("telephone") String telephone
    );
    @FormUrlEncoded
    @POST("/api/auth/add_register")
    Call<User>post(
            @Field("name") String name,
            @Field("surname") String surname,
            @Field("photo") String photo,
            @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("/api/meets/createMeet")
    Call<User>post(
            @Field("name") String name,
            @Field("date") String date,
            @Field("time") String time,
            @Field("description") String description,
            @Field("photo") String photo,
            @Field("place_id") int place,
            @Field("user_array") int[] participant,
            @Field("user_id") int user_id

    );

    @GET("/api/meets/getMeets/{id}")
    Call<MeetList>get(
            @Path("id") int id
    );

    @GET("/api/auth/logout/{id}")
    Call<User>logout(
            @Path("id") int id
    );

    @FormUrlEncoded
    @POST("/api/groups/createGroup")
    Call<Groups>post(
            @Field("name") String name,
            @Field("user_id") int user_id
    );

    @GET("/api/groups/getGroups/{id}")
    Call<GroupList>getGroup(
            @Path("id") int id
    );



}

