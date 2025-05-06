package com.example.fedatingapp.WebSocket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.TokenManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class WebSocketClient {
    private Context context;
    private static final String TAG = "WebSocketClient";
    private Long currentUserId;
    private WebSocket webSocket;
    private OkHttpClient client;
    private MessageListener listener;
    private Listener listenerNotification;
    public interface MessageListener {
        void onMessageReceived(Message message);
    }
    public interface Listener {
        void onNotifyReceived(Notification notification);
    }

    public WebSocketClient(Long currentUserId,Context context) {
        this.currentUserId = currentUserId;
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();
        this.context = context;
    }

    public void setMessageListener(MessageListener listener)
    {
        this.listener = listener;
    }
    public void setListenerNotification(Listener listenerNotification)
    {
        this.listenerNotification = listenerNotification;
    }

    public MessageListener getMessageListener() {
        return this.listener;
    }

    public Listener getNotificationListener() {
        return this.listenerNotification;
    }

    public void connect() {
        Request request = new Request.Builder()
                .url("ws:// 192.168.0.139:8080/chat")
                .addHeader("Authorization", "Bearer " + new TokenManager(context).getAccessToken())
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "Kết nối thành công");
                // Gửi CONNECT frame
                String connectFrame = "CONNECT\naccept-version:1.2\nhost: 192.168.0.139\n\n\0";
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
                if (text.contains("MESSAGE") ) {
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

                if (text.contains("NOTIFY")) {
                    try {
                        // Tìm và phân tích phần JSON sau NOTIFY
                        int notifyIndex = text.indexOf("NOTIFY\n\n");
                        if (notifyIndex != -1) {
                            String jsonString = text.substring(notifyIndex + "NOTIFY\n\n".length()).trim();
                            // Loại bỏ ký tự null terminator và ký tự thừa
                            jsonString = jsonString.replace("\0", "").replaceAll("[^\\x20-\\x7E\\n\\r\\t]*", "");
                            Log.d(TAG, "Extracted NOTIFY JSON: " + jsonString);

                            try {
                                JSONObject json = new JSONObject(jsonString);
                                Notification notification = new Notification(
                                        json.getString("notifyContent"),
                                        json.getString("notifyType")
                                );
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (listenerNotification != null) {
                                        listenerNotification.onNotifyReceived(notification);
                                    } else {
                                        Log.w(TAG, "NotificationListener là null");
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e(TAG, "Lỗi phân tích JSON thông báo: " + e.getMessage());
                            }
                        }
                        return; // Đã xử lý tin nhắn NOTIFY, thoát phương thức
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi xử lý thông báo NOTIFY: " + e.getMessage());
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

            // Thêm header content-type để server biết đây là JSON
            webSocket.send("SEND\ndestination:/app/sendPrivateMessage\ncontent-type:application/json\n\n" + message.toString() + "\0");
            Log.d(TAG, "sendMessage: " + message.toString());
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