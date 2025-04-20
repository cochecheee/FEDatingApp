package com.example.fedatingapp.WebSocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.fedatingapp.entities.Message;

import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class WebSocketClient {
    private static final String TAG = "WebSocketClient";
    private Long currentUserId;
    private WebSocket webSocket;
    private OkHttpClient client;
    private MessageListener listener;
    public interface MessageListener {
        void onMessageReceived(Message message);
    }
    public WebSocketClient(Long currentUserId, MessageListener listener) {
        this.currentUserId = currentUserId;
        this.listener = listener;
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
    }

    public void connect() {
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8080/chat") // Thay bằng IP thực tế nếu cần
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "Kết nối thành công");
                // Gửi CONNECT frame
                String connectFrame = "CONNECT\naccept-version:1.2\nhost:10.0.2.2\n\n\0";
                webSocket.send(connectFrame);
                Log.d(TAG, "Đã gửi CONNECT frame: " + connectFrame);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Nhận được: " + text);

                // Kiểm tra CONNECTED frame
                if (text.startsWith("CONNECTED")) {
                    String privateChannel = getPrivateChannel(currentUserId);
                    String subscribeFrame = "SUBSCRIBE\nid:sub-" + currentUserId + "\ndestination:" + privateChannel + "\n\n\0";
                    webSocket.send(subscribeFrame);
                    Log.d(TAG, "Đã gửi SUBSCRIBE frame: " + subscribeFrame);
                }

                // Nếu là MESSAGE frame, xử lý tin nhắn
                if (text.contains("MESSAGE")) {
                    try {
                        String jsonString = text.split("\n\n")[1].trim().replace("\0", "");
                        JSONObject json = new JSONObject(jsonString);
                        Message message = new Message(
                                json.getLong("fromUser"),
                                json.getString("messageContent")
                        );
                        // Gọi listener trên main thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onMessageReceived(message);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi parsing JSON: " + e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "Lỗi: " + t.getMessage());
            }
        });
    }

    public void sendMessage(String content, Long receiverId) {
        try {
            JSONObject message = new JSONObject();
            message.put("fromUser", currentUserId);
            message.put("toUser", receiverId);
            message.put("messageContent", content);
            webSocket.send("SEND\ndestination:/app/sendPrivateMessage\n\n" + message.toString() + "\0");
            Log.d(TAG, "sendMessage: "+message.toString());
        } catch (Exception e) {
            Log.e(TAG, "Lỗi gửi tin nhắn: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Ngắt kết nối");
        }
    }

    private String getPrivateChannel(Long currentUserId) {
        return "/topic/private/" + currentUserId;
    }
}