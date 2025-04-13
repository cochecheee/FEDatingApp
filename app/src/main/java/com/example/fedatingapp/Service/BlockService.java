package vn.iotstar.dating_fe.Service;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.iotstar.dating_fe.API.BlockAPI;
import vn.iotstar.dating_fe.entities.MatchList;
import vn.iotstar.dating_fe.Retrofit.RetrofitClient;

public class BlockService {
    private final BlockAPI blockAPI = RetrofitClient.getRetrofit().create(BlockAPI.class);

    // Block a user
    public void blockUser(MatchList blockUser, Callback<Void> callback) {
        Call<Void> call = blockAPI.blockUser(blockUser);
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
        Call<List<MatchList>> call = blockAPI.getAllBlockUser(userId);
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
        Call<Void> call = blockAPI.unBlockUser(unBlockUser);
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
}