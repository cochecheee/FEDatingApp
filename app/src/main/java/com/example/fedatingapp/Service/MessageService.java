package vn.iotstar.dating_fe.Service;

import android.util.Log;

import java.util.List;
import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.iotstar.dating_fe.API.MessageAPI;
import vn.iotstar.dating_fe.entities.Message;
import vn.iotstar.dating_fe.Retrofit.RetrofitClient;

public class MessageService {
    private final MessageAPI messageAPI = RetrofitClient.getRetrofit().create(MessageAPI.class);

    // Lấy tin nhắn cuối cùng giữa 2 user
    public void getLastMessage(Long user1, Long user2, Callback<Optional<Message>> callback) {
        Call<Optional<Message>> call = messageAPI.getLastMessage(user1, user2);
        call.enqueue(new Callback<Optional<Message>>() {
            @Override
            public void onResponse(Call<Optional<Message>> call, Response<Optional<Message>> response) {
                if (response.isSuccessful()) {
                    Optional<Message> message = response.body();
                    if (message != null && message.isPresent()) {
                        Log.d("MessageService", "Lấy tin nhắn cuối cùng thành công: " + message.get().toString());
                    } else {
                        Log.d("MessageService", "Không có tin nhắn nào giữa 2 user");
                    }
                    callback.onResponse(call, Response.success(response.body()));
                } else {
                    Log.e("MessageService", "Lấy tin nhắn thất bại: " + response.code());
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<Optional<Message>> call, Throwable t) {
                Log.e("MessageService", "Error: " + t.getMessage());
                callback.onFailure(call, t);
            }
        });
    }

    public void getMessages(Long user1, Long user2, int limit, int offset, Callback<List<Message>> callback) {
        Call<List<Message>> call = messageAPI.getMessages(user1, user2, limit, offset);
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

