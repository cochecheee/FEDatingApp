package com.example.fedatingapp.fragments;

import static android.app.Activity.RESULT_OK;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.activities.ProfileActivity;
import com.example.fedatingapp.activities.SettingsActivity;
import com.example.fedatingapp.adapters.SliderAdapter;
import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.models.ImgurResponse;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.NotificationUtils;
import com.example.fedatingapp.utils.TokenManager;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment implements WebSocketClient.Listener {

    private View rootLayout;
    private TextView username, userJob, userOld;
    private LinearLayout setting, image, profile;
    private UserService userService ;
    private TokenManager tokenManager;
    private Users users;
    private List<Image> userImage;
    private Long userId;
    private CircleImageView circleImageView;
    private Uri mUri;
    public static final int MY_REQUEST_CODE = 100;
    public static final String TAG = AccountFragment.class.getName();

    public AccountFragment() {

    }

    // Factory method để truyền userId (nếu cần)
    public static AccountFragment newInstance(Long userId) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong("userId", 1L);
        } else {
            userId = 1L;
        }
        tokenManager = new TokenManager(getActivity());
        userService = new UserService("Bearer " + tokenManager.getAccessToken());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        rootLayout = inflater.inflate(R.layout.fragment_account, container, false);

        // Khởi tạo SliderView
        SliderView sliderView = rootLayout.findViewById(R.id.slider_view);
        final SliderAdapter adapter = new SliderAdapter(getActivity());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.SLIDE);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.startAutoCycle();

        binding();
        getUser();

        checkCountImage();

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatingInfoPopup();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckPermission();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);

            }
        });

        return rootLayout;
    }

    private void showDatingInfoPopup() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        intent.putExtra("userId",userId);
        startActivity(intent);
    }

    private void CheckPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_REQUEST_CODE);
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
                File tempFile = new File(getContext().getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");

                // Lấy InputStream từ Uri
                InputStream inputStream = getContext().getContentResolver().openInputStream(mUri);
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

                                // Tạo đối tượng Image
                                Image newImage = new Image();
                                newImage.setImage(imageUrl);
                                newImage.setUserId(userId);

                                // Lưu vào backend
                                userService.addUserImage(newImage);

                                Toast.makeText(getContext(), "Image uploaded thành công", Toast.LENGTH_SHORT).show();
                                // Refresh images
                                getUserImage();
                                checkCountImage();
                            } else {
                                Toast.makeText(getContext(), "Upload image thất bại", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Upload failed: " + response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<ImgurResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Error uploading image", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Unexpected error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCountImage()
    {
        userService.getAllUserImage(userId, new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                List<Image> images = new ArrayList<>();
                if (response.isSuccessful())
                {
                    images = response.body();
                }
                if (images.size() >= 5)
                {
                    image.setOnClickListener(null);
                    image.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable throwable) {

            }
        });
    }

    private void binding() {
        username = rootLayout.findViewById(R.id.tvProfileNameAge);
        userJob = rootLayout.findViewById(R.id.tvProfileJob);
        userOld = rootLayout.findViewById(R.id.tvProfileSchool);
        setting = rootLayout.findViewById(R.id.btn_setting);
        image = rootLayout.findViewById(R.id.btn_addImage);
        profile = rootLayout.findViewById(R.id.btn_editProfile);
        setting = rootLayout.findViewById(R.id.btn_setting);
        image = rootLayout.findViewById(R.id.btn_addImage);
        circleImageView = rootLayout.findViewById(R.id.profile_image);
    }

    private void getUser() {
        userService.getUserInfo(userId, new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.isSuccessful()) {
                    users = response.body();
                    fillView();
                } else {
                    Log.d("AccountFragment", "GetUser failed: " + response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call,@NonNull Throwable throwable) {
                Log.d("AccountFragment", "GetUser error: " + throwable.getMessage());
            }
        });
    }

    private void fillView() {
        if (users != null) {
            username.setText(users.getName());
            userJob.setText(users.getJob());
            if (users.getBirthday() != null)
            {
                userOld.setText(String.valueOf(CountOld(
                        users.getBirthday().getDate(),
                        users.getBirthday().getMonth(),
                        users.getBirthday().getYear()
                )));
            }
            else {
                userOld.setText("");
            }
        }
        getUserImage();
    }

    private void getUserImage() {
        userService.getAllUserImage(userId, new Callback<List<Image>>() {
            @Override
            public void onResponse(@NonNull Call<List<Image>> call,@NonNull Response<List<Image>> response) {
                if (response.isSuccessful()) {
                    userImage = response.body();
                    if (userImage != null && !userImage.isEmpty()) {
                        Random random = new Random();
                        int position = random.nextInt(userImage.size());
                        Log.d("AccountFragment", "onResponse: "+userImage.get(position).getImage());
                        Glide.with(AccountFragment.this)
                                .load(userImage.get(position).getImage())
                                .into(circleImageView);
                    }
                } else {
                    Log.d("AccountFragment", "GetUserImage failed: " + response.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Image>> call,@NonNull Throwable throwable) {
                Log.d("AccountFragment", "GetUserImage error: " + throwable.getMessage());
            }
        });
    }

    private int CountOld(int ngaySinh, int thangSinh, int namSinh) {

        LocalDate currentDay = LocalDate.now();
        Log.d("sinh nhat", "CountOld: "+ngaySinh+ thangSinh + namSinh);
        LocalDate birthday = LocalDate.of(namSinh, thangSinh, ngaySinh);
        Period old = Period.between(birthday, currentDay);
        return old.getYears();
    }

    @Override
    public void onNotifyReceived(Notification notification) {
        NotificationUtils.showPushNotification(getContext(),notification);
    }
}