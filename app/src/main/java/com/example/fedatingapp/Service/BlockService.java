package com.example.fedatingapp.Service;

import android.content.Context;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.fedatingapp.api.BlockAPI;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.entities.MatchList;
import com.example.fedatingapp.utils.TokenManager;

public class BlockService {
    private String token = "";
    public BlockService(String token){
        this.token = token;
        Log.d("BlockService", "BlockService: " + token);
    }
    private final BlockAPI blockAPI = RetrofitClient.getRetrofit().create(BlockAPI.class);

    // Block a user
    public void blockUser(MatchList blockUser, Callback<Void> callback) {
        Call<Void> call = blockAPI.blockUser(token,blockUser);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("BlockService", "Block user thanh cong: " + response.body());
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("BlockService", "Block user that bai: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("BlockService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    // Get all blocked users
    public void getAllBlockUser(Long userId, Callback<List<MatchList>> callback) {
        Call<List<MatchList>> call = blockAPI.getAllBlockUser(token,userId);
        call.enqueue(new Callback<List<MatchList>>() {
            @Override
            public void onResponse(Call<List<MatchList>> call, Response<List<MatchList>> response) {
                if (response.isSuccessful()) {
                    Log.d("BlockService", "Get all block users thanh cong: " + response.body().size() + " items");
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("BlockService", "Get all block users that bai: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<MatchList>> call, Throwable t) {
                Log.e("BlockService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    // Unblock a user
    public void unBlockUser(MatchList unBlockUser, Callback<Void> callback) {
        Call<Void> call = blockAPI.unBlockUser(token,unBlockUser);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("BlockService", "Unblock user thanh cong: " + response.body());
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("BlockService", "Unblock user that bai: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("BlockService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    public void unMatch(Long userId, Callback<Void> callback)
    {
        Call<Void> call = blockAPI.unMatch(token, userId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                callback.onResponse(call, Response.success(response.body()));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });
    }

}