package com.example.fedatingapp.api;

import com.example.fedatingapp.entities.MatchList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface BlockAPI {
    // POST /block/ - Block user
    @Headers({"Accept: application/json"})
    @POST("block/")
    Call<Void> blockUser(@Header("Authorization") String authToken,@Body MatchList blockUser);

    // POST /block/getAll - Get all blocked users
    @Headers({"Accept: application/json"})
    @POST("block/getAll")
    Call<List<MatchList>> getAllBlockUser(@Header("Authorization") String authToken,@Query("userId") Long userId);

    // POST /block/delete - Unblock user
    @Headers({"Accept: application/json"})
    @POST("block/delete")
    Call<Void> unBlockUser(@Header("Authorization") String authToken, @Body MatchList unBlockUser);


    @Headers({"Accept: application/json"})
    @POST("users/unMatch/{userId}")
    Call<Void> unMatch(@Header("Authorization") String authToken, @Path("userId") Long userId);
}
