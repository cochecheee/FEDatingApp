package com.example.fedatingapp.Service;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.fedatingapp.API.SearchCardAPI;
import com.example.fedatingapp.Retrofit.RetrofitClient;
import com.example.fedatingapp.entities.Users;

public class SearchCardService {
    private final SearchCardAPI searchCardAPI = RetrofitClient.getRetrofit().create(SearchCardAPI.class);

    // Tìm user theo Sexual Orientation
    public void findBySexualOrientation(String sexualOrientation, Callback<List<Users>> callback) {
        Call<List<Users>> call = searchCardAPI.findBySexualOrientation(sexualOrientation);
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    Log.d("SearchCardService", "Tìm theo Sexual Orientation thành công: " + response.body().size() + " users");
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("SearchCardService", "Tìm theo Sexual Orientation thất bại: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Log.e("SearchCardService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    // Tìm user theo Interests
    public void findByInterests(String interests, Callback<List<Users>> callback) {
        Call<List<Users>> call = searchCardAPI.findByInterests(interests);
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    Log.d("SearchCardService", "Tìm theo Interests thành công: " + response.body().size() + " users");
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("SearchCardService", "Tìm theo Interests thất bại: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Log.e("SearchCardService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    // Tìm user theo Zodiac Sign
    public void findByZodiacSign(String zodiacSign, Callback<List<Users>> callback) {
        Call<List<Users>> call = searchCardAPI.findByZodiacSign(zodiacSign);
        call.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                if (response.isSuccessful()) {
                    Log.d("SearchCardService", "Tìm theo Zodiac Sign thành công: " + response.body().size() + " users");
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("SearchCardService", "Tìm theo Zodiac Sign thất bại: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<List<Users>> call, Throwable t) {
                Log.e("SearchCardService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }
}
