package com.example.fedatingapp.api;

import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.models.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface SearchCardAPI {
    // POST /searchCard/find/SexualOrientation - Tìm theo Sexual Orientation
    @FormUrlEncoded
    @POST("searchCard/find/SexualOrientation")
    Call<List<Users>> findBySexualOrientation(@Field("SexualOrientation") String sexualOrientation);

    // POST /searchCard/find/interests - Tìm theo Interests
    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("searchCard/find/interests")
    Call<List<Profile>> findByInterests(@Header("Authorization") String authToken, @Field("interests") String interests);

    // POST /searchCard/find/zodiacSign - Tìm theo Zodiac Sign
    @FormUrlEncoded
    @POST("searchCard/find/zodiacSign")
    Call<List<Users>> findByZodiacSign(@Field("zodiacSign") String zodiacSign);
}
