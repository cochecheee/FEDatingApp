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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.BlockService;
import com.example.fedatingapp.Service.MessageService;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.adapters.ChatAdapter;
import com.example.fedatingapp.databinding.BoxChatBinding;
import com.example.fedatingapp.databinding.ChatItemRecieveBinding;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.models.ImgurResponse;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.NotificationUtils;
import com.example.fedatingapp.utils.TokenManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements WebSocketClient.MessageListener {
    private static final String CHANNEL_ID = "notify_channel";
    private MessageService messageService;
    private BlockService blockService;
    private TokenManager tokenManager;
    private WebSocketManager webSocketManager;
    private BoxChatBinding binding;
    private ChatAdapter messageAdapter;
    private List<Message> messageList;

    // ID người dùng hiện tại và người nhận tin nhắn
    private Long currentUserId;
    private Long receiverUserId;
    private String receiverName;
    private String receiverImage;
    private boolean isLoading = false;
    private int offset = 0;
    private UserService userService;
    private Uri mUri;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = ChatActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoxChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tokenManager = new TokenManager(getApplicationContext());
        userService = new UserService("Bearer " + tokenManager.getAccessToken());
        messageService = new MessageService("Bearer " + tokenManager.getAccessToken());
        blockService = new BlockService("Bearer " + tokenManager.getAccessToken());
        webSocketManager = WebSocketManager.getInstance(getApplicationContext());
        webSocketManager.setMessageListener(this::onMessageReceived);
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

        binding.recyclerMessages.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                    if (firstVisibleItem <= 1) { // Gần đầu danh sách
                        isLoading = true;
                        loadMoreMessages();
                        isLoading = false;
                    }
                }
            }
        });
    }
    private void loadMoreMessages()
    {
        List<Message> dummyMessages = new ArrayList<>();
        offset += 1;
        messageService.getMessages(currentUserId, receiverUserId, 20, offset, new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null)
                {
                    if (response.body().isEmpty())
                    {
                        offset -= 1;
                        return;
                    }
                    dummyMessages.addAll(response.body());
                    Collections.reverse(dummyMessages);
                    dummyMessages.addAll(messageList);
                    // Cập nhật lên UI
                    messageList.clear();
                    messageList.addAll(dummyMessages);
                    messageAdapter.notifyDataSetChanged();
                    binding.recyclerMessages.scrollToPosition(20 - 1);
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
        binding.profileImage.setOnClickListener(v -> openUserProfile());
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

        messageService.getMessages(currentUserId, receiverUserId, 20, offset, new Callback<List<Message>>() {
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

    private void openUserProfile()
    {
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        intent.putExtra("receiverUserId" , receiverUserId);
        intent.putExtra("receiverImage", receiverImage);
        startActivity(intent);
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
        PopupMenu popupMenu = new PopupMenu(this,binding.btnMore);
        popupMenu.getMenuInflater().inflate(R.menu.chat_menu_block,popupMenu.getMenu());
//bắt sự kiện
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.btnUnMatch) {
                    blockService.unMatch(receiverUserId, new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful())
                            {
                                Toast.makeText(ChatActivity.this, "Chặn người dùng "+receiverUserId, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else
                            {
                                Toast.makeText(ChatActivity.this, "Chặn người dùng "+response.body(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable throwable) {
                            Toast.makeText(ChatActivity.this, "Hệ thống đang gặp sự cố, mời quay lại sau", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void attachFiles() {
        CheckPermission();
    }

    private void CheckPermission() {
        // Kiểm tra xem quyền READ_EXTERNAL_STORAGE đã được cấp chưa
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Trường hợp đã cấp quyền: mở thư viện ảnh ngay lập tức
            openGallery();
        } else {
            // Trường hợp chưa cấp quyền: yêu cầu quyền
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_REQUEST_CODE);
        }
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
                // Tạo file tạm trong bộ nhớ cache
                File tempFile = new File(getApplicationContext().getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");

                // Lấy InputStream từ Uri
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(mUri);
                if (inputStream != null) {
                    // Ghi InputStream vào file tạm
                    FileOutputStream outputStream = new FileOutputStream(tempFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.close();

                    // Upload file tạm lên Imgur
                    userService.uploadImageToImgur(tempFile, new Callback<ImgurResponse>() {
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
                                Toast.makeText(getApplicationContext(), "Upload image thất bại", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Upload failed: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ImgurResponse> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Upload error: " + t.getMessage());
                        }
                    });

                    // Xóa file tạm sau khi dùng xong (tùy chọn)
                    tempFile.deleteOnExit();
                } else {
                    throw new IOException("Không thể mở InputStream từ Uri");
                }
            } catch (IOException e) {
                Log.e(TAG, "Error preparing file: " + e.getMessage(), e);
                Toast.makeText(getApplicationContext(), "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
                Toast.makeText(getApplicationContext(), "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }


    private Activity requireActivity() {
        return ChatActivity.this;
    }

    @Override
    public void onMessageReceived(Message message) {
        if (message.getFromUser() == receiverUserId)
        {
            messageList.add(message);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            binding.recyclerMessages.smoothScrollToPosition(messageList.size() - 1);
        }
        else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("Tin nhan moi")
                    .setContentText(message.getMessageContent())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocketManager.removeMessageListener();
    }
}