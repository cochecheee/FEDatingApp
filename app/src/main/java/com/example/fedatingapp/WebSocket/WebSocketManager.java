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

        client = new WebSocketClient(currentUserId, listener, listenerNotify);
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

    // Cập nhật listener (nếu cần thay đổi listener, ví dụ khi chuyển activity)
    public void updateMessageListener(WebSocketClient.MessageListener listener, WebSocketClient.Listener listenerNotify) {
        this.messageListener = listener;
        if (client != null) {
            // Tạo client mới với listener mới
            Long id = this.currentUserId;
            client.disconnect();
            client = new WebSocketClient(id, listener,listenerNotify );
            client.connect();
        }
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