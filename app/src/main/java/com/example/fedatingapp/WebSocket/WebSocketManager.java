package com.example.fedatingapp.WebSocket;

import android.util.Log;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private WebSocketClient client;
    private Long currentUserId;
    private WebSocketClient.MessageListener messageListener;
    private WebSocketClient.Listener listener;
    // Private constructor
    private WebSocketManager() {
        // Khởi tạo rỗng
    }

    // Singleton pattern
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    // Khởi tạo WebSocketClient
    public void initialize(Long currentUserId, WebSocketClient.MessageListener listener, WebSocketClient.Listener listenerNotify) {
        this.currentUserId = currentUserId;
        this.messageListener = listener;

        if (client != null) {
            client.disconnect(); // Ngắt kết nối cũ nếu có
        }

        client = new WebSocketClient(currentUserId);
        client.connect();
        Log.d(TAG, "WebSocketClient đã được khởi tạo với userId: " + currentUserId);
    }

    // Gửi tin nhắn
    public void sendMessage(String content, Long receiverId) {
        if (client != null) {
            client.sendMessage(content, receiverId);
        } else {
            Log.e(TAG, "WebSocketClient chưa được khởi tạo");
        }
    }

    public void setMessageListener(WebSocketClient.MessageListener listener)
    {
        client.setMessageListener(listener);
    }

    public void removeMessageListener()
    {
        client.setMessageListener(null);
    }

    public void setListenerNotify(WebSocketClient.Listener listenerNotify){
        client.setListenerNotification(listenerNotify);
    }
    public void removeListenerNotify(WebSocketClient.Listener listenerNotify){
        client.setListenerNotification(null);
    }


    // Kiểm tra trạng thái kết nối
    public boolean isInitialized() {
        return client != null;
    }

    // Ngắt kết nối
    public void disconnect() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    // Getter cho userId hiện tại
    public Long getCurrentUserId() {
        return currentUserId;
    }
}