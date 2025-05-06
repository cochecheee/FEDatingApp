package com.example.fedatingapp.Service;

import android.content.Context;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.fedatingapp.api.MessageAPI;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.models.MessageItem;
import com.example.fedatingapp.utils.TokenManager;

public class MessageService {
    private String token = "";
    public MessageService(String token){
        this.token = token;
        Log.d("MessageService", "MessageService: " + token);
    }
    private final MessageAPI messageAPI = RetrofitClient.getRetrofit().create(MessageAPI.class);

    // Lấy tin nhắn cuối cùng giữa 2 user
    public void getListMatch (Long user1, Callback<List<MessageItem>> callback) {
        Call<List<MessageItem>> call = messageAPI.getListMatch(token,user1);
        call.enqueue(new Callback<List<MessageItem>>() {
            @Override
            public void onResponse(Call<List<MessageItem>> call, Response<List<MessageItem>> response) {
                if (response.isSuccessful()) {
                    List<MessageItem> message = response.body();
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("MessageService", "Lấy tin nhắn thất bại: " + response.code());
                    callback.onResponse(call, response);
                }
            }
            @Override
            public void onFailure(Call<List<MessageItem>> call, Throwable t) {
                Log.e("MessageService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    public void getMessages(Long user1, Long user2, int limit, int offset, Callback<List<Message>> callback) {
        Call<List<Message>> call = messageAPI.getMessages(token,user1, user2, limit, offset);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful()) {
                    Log.d("MessageService", "Lấy danh sách tin nhắn thành công: " + response.body().size() + " messages");
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("MessageService", "Lấy danh sách tin nhắn thất bại: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e("MessageService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }
}

