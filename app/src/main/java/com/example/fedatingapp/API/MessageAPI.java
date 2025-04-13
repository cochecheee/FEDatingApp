package com.example.fedatingapp.API;

import com.example.fedatingapp.entities.Message;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MessageAPI {
    // GET /api/message/getLast - Lấy tin nhắn cuối cùng giữa 2 user
    @GET("/api/message/getLast")
    Call<Optional<Message>> getLastMessage(
            @Query("user1") Long user1,
            @Query("user2") Long user2
    );


    @GET("/api/message/getMessages")
    Call<List<Message>> getMessages(
            @Query("user1") Long user1,
            @Query("user2") Long user2,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
