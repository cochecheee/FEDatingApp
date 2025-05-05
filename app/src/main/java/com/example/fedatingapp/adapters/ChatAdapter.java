package com.example.fedatingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.databinding.ChatItemRecieveBinding;
import com.example.fedatingapp.databinding.ChatItemSendBinding;
import com.example.fedatingapp.entities.Message;


import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private final Context context;
    private final List<Message> messageList;
    private final Long currentUserId;

    public ChatAdapter(Context context, List<Message> messageList, Long currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            ChatItemSendBinding binding = ChatItemSendBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new SentMessageHolder(binding);
        } else {
            ChatItemRecieveBinding binding = ChatItemRecieveBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ReceivedMessageHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageHolder) holder).bind(message);
        } else {
            ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getFromUser().equals(currentUserId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // ViewHolder cho tin nhắn gửi đi
    static class SentMessageHolder extends RecyclerView.ViewHolder {
        private final ChatItemSendBinding binding;
        private final Context context;

        SentMessageHolder(ChatItemSendBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
        }

        void bind(Message message) {
            String content = message.getMessageContent();

            // Kiểm tra nếu nội dung là URL ảnh
            if (isImageUrl(content)) {
                // Hiển thị ảnh, ẩn text
                binding.tvMessageContent.setVisibility(View.GONE);
                binding.imgMessage.setVisibility(View.VISIBLE);

                // Load ảnh với Glide
                Glide.with(context)
                        .load(content)
                        .into(binding.imgMessage);
            } else {
                // Hiển thị text, ẩn ảnh
                binding.tvMessageContent.setVisibility(View.VISIBLE);
                binding.imgMessage.setVisibility(View.GONE);
                binding.tvMessageContent.setText(content);
            }

            // Format thời gian
            if (message.getSentAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                binding.tvTime.setText(sdf.format(message.getSentAt()));
            } else {
                binding.tvTime.setText("Đang gửi...");
            }
        }

        // Phương thức kiểm tra URL ảnh đơn giản
        private boolean isImageUrl(String url) {
            if (url == null || url.isEmpty()) {
                return false;
            }

            // Kiểm tra xem URL có phải là URL hợp lệ không
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                return false;
            }

            // Kiểm tra xem URL có phải định dạng ảnh phổ biến không
            return url.toLowerCase().endsWith(".jpg") ||
                    url.toLowerCase().endsWith(".jpeg") ||
                    url.toLowerCase().endsWith(".png") ||
                    url.toLowerCase().endsWith(".gif") ||
                    url.toLowerCase().endsWith(".bmp") ||
                    url.toLowerCase().endsWith(".webp");
        }
    }

    // ViewHolder cho tin nhắn nhận được
    static class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        private final ChatItemRecieveBinding binding;
        private final Context context;

        ReceivedMessageHolder(ChatItemRecieveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = binding.getRoot().getContext();
        }

        void bind(Message message) {
            String content = message.getMessageContent();

            // Kiểm tra nếu nội dung là URL ảnh
            if (isImageUrl(content)) {
                // Hiển thị ảnh, ẩn text
                binding.tvMessageContent.setVisibility(View.GONE);
                binding.imgMessage.setVisibility(View.VISIBLE);

                // Load ảnh với Glide
                Glide.with(context)
                        .load(content)
                        .into(binding.imgMessage);
            } else {
                // Hiển thị text, ẩn ảnh
                binding.tvMessageContent.setVisibility(View.VISIBLE);
                binding.imgMessage.setVisibility(View.GONE);
                binding.tvMessageContent.setText(content);
            }

            // Format thời gian
            if (message.getSentAt() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                binding.tvTime.setText(sdf.format(message.getSentAt()));
            }
        }

        // Phương thức kiểm tra URL ảnh đơn giản
        private boolean isImageUrl(String url) {
            if (url == null || url.isEmpty()) {
                return false;
            }

            // Kiểm tra xem URL có phải là URL hợp lệ không
            try {
                new URL(url);
            } catch (MalformedURLException e) {
                return false;
            }

            // Kiểm tra xem URL có phải định dạng ảnh phổ biến không
            return url.toLowerCase().endsWith(".jpg") ||
                    url.toLowerCase().endsWith(".jpeg") ||
                    url.toLowerCase().endsWith(".png") ||
                    url.toLowerCase().endsWith(".gif") ||
                    url.toLowerCase().endsWith(".bmp") ||
                    url.toLowerCase().endsWith(".webp");
        }
    }
}