package com.example.fedatingapp.Service;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.fedatingapp.API.UsersAPI;
import com.example.fedatingapp.Retrofit.RetrofitClient;
import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.Users;

public class UserService {
    private final UsersAPI userAPI = RetrofitClient.getRetrofit().create(UsersAPI.class);

    public void getUserInfo(Long userId, Callback<Users> callback) {

        Call<Users> call = userAPI.getUserInfo(userId);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Log.d("Oke", "Truy van thanh cong" + response.toString() + response.body());
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.d("Oke", "Truy van that bai" + response.toString());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.d("Fail", "Truy van that bai" + t.toString());
                callback.onFailure(call, t);
            }
        });
    }

    public void UpdateUserInfo(Users userInfo)    {
        Call<Void> call = userAPI.UpdateUserInfo(userInfo);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                {
                    Log.d("update user", "onResponse: Update user thanh cong");
                }
                else
                {
                    Log.d("update user", "onResponse: Update user thanh cong" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("update user", "onResponse: Update user khong thanh cong" + t.toString());
            }
        });
    }

    public void getAllUserImage(Long userid, Callback<List<Image>> callback){
        Call<List<Image>> call = userAPI.getAllImageProfile(userid);
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                if (response.isSuccessful())
                {
                    callback.onResponse(call,Response.success(response.body()));
                }
                else
                {
                    callback.onResponse(call,response);
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                Log.d("getListImage", "onFailure: "+t.toString());
            }
        });
    }

    public void addUserImage(Image image){
        Call<Void> call = userAPI.addImageProfile(image);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                {
                    Log.d("addImage", "onResponse: Them thanh cong");
                }
                else
                {
                    Log.d("addImage", "onResponse: "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("addImage", "onResponse: "+t.toString());
            }
        });
    }

    public void delUserImage(Image image){
        Call<Void> call = userAPI.delImageProfile(image);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                {
                    Log.d("delImage", "onResponse: Xoa thanh cong");
                }
                else
                {
                    Log.d("delImage", "onResponse: "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("delImage", "onResponse: "+t.toString());
            }
        });
    }


}
