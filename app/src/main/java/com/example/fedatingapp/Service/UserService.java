package com.example.fedatingapp.Service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.fedatingapp.api.ImgurAPI;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.api.UsersAPI;
import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.SearchCriteria;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.models.ImgurResponse;
import com.example.fedatingapp.utils.TokenManager;

public class UserService {

    private String token = "";
    private final ImgurAPI imgurApi;
    private static final String IMGUR_CLIENT_ID = "b79cf480c808337";

    public UserService(String token){
        this.token = token;
        Log.d("UserService", "UserService: " + this.token);
        // Tạo Retrofit instance cho Imgur
        Retrofit imgurRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        imgurApi = imgurRetrofit.create(ImgurAPI.class);
    }


    private final UsersAPI userAPI = RetrofitClient.getRetrofit().create(UsersAPI.class);

    public void getUserInfo(Long userId, Callback<Users> callback) {
        Log.d("UserService", "Ham UserService: " + this.token);
        Call<Users> call = userAPI.getUserInfo(token,userId);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    Log.d("Oke", "Truy van thanh cong" + response.toString() + response.body());
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.d("Oke", "Truy van that bai " + token);
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
        Call<Void> call = userAPI.UpdateUserInfo(token,userInfo);
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
        Call<List<Image>> call = userAPI.getAllImageProfile(token,userid);
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
        Call<Void> call = userAPI.addImageProfile(token,image);
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
        Call<Void> call = userAPI.delImageProfile(token,image);
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

    public void uploadImageToImgur(File imageFile, Callback<ImgurResponse> callback) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "image");
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), "Simple upload");
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "This is a simple image upload in Imgur");

        Call<ImgurResponse> call = imgurApi.uploadImage(
                "Client-ID " + IMGUR_CLIENT_ID,
                imagePart,
                type,
                title,
                description
        );

        call.enqueue(new Callback<ImgurResponse>() {
            @Override
            public void onResponse(Call<ImgurResponse> call, Response<ImgurResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("UserService", "Imgur upload success: " + response.body().data.link);
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("UserService", "Imgur upload failed: " + response.message());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<ImgurResponse> call, Throwable t) {
                Log.e("UserService", "Imgur upload error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    public void getSearch(Long userid, Callback<SearchCriteria> callback){
        Call<SearchCriteria> call = userAPI.getSearch(token,userid);
        call.enqueue(new Callback<SearchCriteria>() {
            @Override
            public void onResponse(Call<SearchCriteria> call, Response<SearchCriteria> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<SearchCriteria> call, Throwable throwable) {
                callback.onFailure(call, throwable);
            }
        });
    }

    public void updateSearch(SearchCriteria searchCriteria)
    {
        Call<Void> call = userAPI.updateSearch(token,searchCriteria);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                {
                    Log.d("updateSearch", "onResponse: updatesearch thanh cong");
                }
                else
                {
                    Log.d("updateSearch", "onResponse: updatesearch that bai");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.d("updateSearch", "onResponse: updatesearch that bai");
            }
        });
    }

}
