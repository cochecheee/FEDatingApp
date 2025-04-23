package com.example.fedatingapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.MessageService;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.adapters.ChatAdapter;
import com.example.fedatingapp.databinding.BoxChatBinding;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.models.ImgurResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private MessageService messageService = new MessageService();
    private WebSocketManager webSocketManager;
    private BoxChatBinding binding;
    private ChatAdapter messageAdapter;
    private List<Message> messageList;

    // ID người dùng hiện tại và người nhận tin nhắn
    private Long currentUserId;
    private Long receiverUserId;
    private String receiverName;
    private String receiverImage;

    private UserService userService = new UserService();
    private Uri mUri;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = ChatActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoxChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        webSocketManager = WebSocketManager.getInstance();
        // Lấy dữ liệu từ Intent
        currentUserId = getIntent().getLongExtra("CURRENT_USER_ID", 0L);
        receiverUserId = getIntent().getLongExtra("RECEIVER_USER_ID", 0L);
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");
        receiverImage = getIntent().getStringExtra("RECEIVER_IMAGE");

        // Khởi tạo danh sách tin nhắn và adapter
        messageList = new ArrayList<>();
        messageAdapter = new ChatAdapter(this, messageList, currentUserId);

        // Thiết lập RecyclerView
        binding.recyclerMessages.setAdapter(messageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.recyclerMessages.setLayoutManager(layoutManager);

        // Thiết lập thông tin người nhận trong giao diện
        setupUserInfo();

        // Thiết lập lắng nghe sự kiện
        setupListeners();

        // Tải các tin nhắn
        loadMessages();
    }

    private void setupUserInfo() {
        binding.tvUsername.setText(receiverName);

        Glide.with(this).load(receiverImage).placeholder(R.drawable.ic_launcher_foreground).into(binding.profileImage);
    }

    private void setupListeners() {
        // Nút gửi tin nhắn
        binding.btnSend.setOnClickListener(v -> sendMessage());

        // Nút quay lại
        binding.btnBack.setOnClickListener(v -> finish());

        // Các nút khác tùy chức năng
        binding.btnVoiceCall.setOnClickListener(v -> makeVoiceCall());
        binding.btnVideoCall.setOnClickListener(v -> makeVideoCall());
        binding.btnMore.setOnClickListener(v -> showMoreOptions());
        binding.btnAttachment.setOnClickListener(v -> attachFiles());
        binding.btnEmoji.setOnClickListener(v -> showEmojiPicker());
        binding.etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.etMessage.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    private void sendMessage() {
        String msgContent = binding.etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msgContent)) {
            return;
        }
        // Tạo đối tượng tin nhắn mới
        Message message = new Message();
        message.setFromUser(currentUserId);
        message.setToUser(receiverUserId);
        message.setMessageContent(msgContent);
        message.setSentAt(new Date());
        message.setLiked(false);

        // Thêm vào danh sách và cập nhật hiển thị
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        binding.recyclerMessages.smoothScrollToPosition(messageList.size() - 1);

        // Xóa nội dung trong ô nhập
        binding.etMessage.setText("");

        webSocketManager.sendMessage(message.getMessageContent(),message.getToUser());
    }

    private void loadMessages() {
        List<Message> dummyMessages = new ArrayList<>();

        messageService.getMessages(currentUserId, receiverUserId, 20, 0, new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful())
                {
                    dummyMessages.addAll(response.body());
                    Collections.reverse(dummyMessages);
                    // Cập nhật lên UI
                    messageList.clear();
                    messageList.addAll(dummyMessages);
                    messageAdapter.notifyDataSetChanged();
                    binding.recyclerMessages.scrollToPosition(messageList.size() - 1);
                    Log.d("ChatActivity", "ok: "+dummyMessages.get(0).getMessageContent());
                }
                else
                {
                    Log.d("ChatActivity", "onFailure: "+response.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable throwable) {
                Log.d("ChatActivity", "onFailure: "+throwable.getMessage());
            }
        });

    }



    private void makeVoiceCall() {
        Toast.makeText(this, "Gọi thoại với " + receiverName, Toast.LENGTH_SHORT).show();
        // TODO: Implement voice call functionality
    }

    private void makeVideoCall() {
        Toast.makeText(this, "Gọi video với " + receiverName, Toast.LENGTH_SHORT).show();
        // TODO: Implement video call functionality
    }

    private void showMoreOptions() {
        // TODO: Hiển thị menu tùy chọn khác
        Toast.makeText(this, "Tùy chọn khác", Toast.LENGTH_SHORT).show();
    }

    private void attachFiles() {
        CheckPermission();
    }

    private void showEmojiPicker() {
        // TODO: Hiển thị bộ chọn emoji
        Toast.makeText(this, "Chọn emoji", Toast.LENGTH_SHORT).show();
    }

    public static String[] storge_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };

    // Viết hàm CheckPermission()
    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    private void CheckPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_REQUEST_CODE);
        openGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Log.d("open gallerry", "CheckPermission: ");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "onActivityResult");
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data == null) {
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        UploadImage();
                    }
                }
            }
    );

    public void UploadImage() {
        if (mUri != null) {
            try {
                // Convert URI to File
                File file = new File(getRealPathFromURI(mUri));

                // Upload to Imgur
                userService.uploadImageToImgur(file, new Callback<ImgurResponse>() {
                    @Override
                    public void onResponse(Call<ImgurResponse> call, Response<ImgurResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String imageUrl = response.body().data.link;
                            // Tạo đối tượng tin nhắn mới
                            Message message = new Message();
                            message.setFromUser(currentUserId);
                            message.setToUser(receiverUserId);
                            message.setMessageContent(imageUrl);
                            message.setSentAt(new Date());
                            message.setLiked(false);

                            // Thêm vào danh sách và cập nhật hiển thị
                            messageList.add(message);
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            binding.recyclerMessages.smoothScrollToPosition(messageList.size() - 1);

                            // Xóa nội dung trong ô nhập
                            binding.etMessage.setText("");

                            webSocketManager.sendMessage(message.getMessageContent(),message.getToUser());

                        } else {
                            Toast.makeText(getApplicationContext(), "Upload image that bai", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Upload failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ImgurResponse> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Upload error: " + t.getMessage());
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error preparing file: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Error preparing image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }
    // Helper method to get real path from URI
    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return uri.getPath();
    }

    private Activity requireActivity() {
        return ChatActivity.this;
    }
}