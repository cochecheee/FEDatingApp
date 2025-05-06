package com.example.fedatingapp.api;

import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.SearchCriteria;
import com.example.fedatingapp.entities.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface UsersAPI {
    @Headers({"Accept: application/json"})
    @POST("profile/get")
    Call<Users> getUserInfo(@Header("Authorization") String authToken,@Query("userId") Long userId);

    @Headers({"Accept: application/json"})
    @POST("profile/getUser")
    Call<Users> getUserInfo2(@Header("Authorization") String authToken);

    @Headers({"Accept: application/json"})
    @POST("profile/updateProfile")
    Call<Void> UpdateUserInfo(@Header("Authorization") String authToken,@Body Users userInfo);


    @Headers({"Accept: application/json"})
    @POST("profile/addImage")
    Call<Void> addImageProfile(@Header("Authorization") String authToken,@Body Image image);


    @Headers({"Accept: application/json"})
    @POST("profile/removeImage")
    Call<Void> delImageProfile(@Header("Authorization") String authToken,@Body Image image);


    @Headers({"Accept: application/json"})
    @POST("profile/getAllImage")
    Call<List<Image>> getAllImageProfile(@Header("Authorization") String authToken,@Query("userId") Long userId);


    @Headers({"Accept: application/json"})
    @GET("profile/getSearch")
    Call<SearchCriteria> getSearch(@Header("Authorization") String authToken,@Query("userId") Long userId);


    @Headers({"Accept: application/json"})
    @POST("profile/updateSearch")
    Call<Void> updateSearch(@Header("Authorization") String authToken,@Body SearchCriteria searchCriteria);

}
