package com.example.fedatingapp.api;

import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.models.MessageItem;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface MessageAPI {

    // GET /api/message/getLast - Lấy tin nhắn cuối cùng giữa 2 user
    @Headers({"Accept: application/json"})
    @GET("/api/message/getListMatch")
    Call<List<MessageItem>> getListMatch(
            @Header("Authorization") String authToken,
            @Query("user1") Long user1
    );

    @Headers({"Accept: application/json"})
    @GET("/api/message/getMessages")
    Call<List<Message>> getMessages(
            @Header("Authorization") String authToken,
            @Query("user1") Long user1,
            @Query("user2") Long user2,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
