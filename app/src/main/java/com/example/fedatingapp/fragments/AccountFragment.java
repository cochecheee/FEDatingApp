package com.example.fedatingapp.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.activities.ProfileActivity;
import com.example.fedatingapp.adapters.SliderAdapter;
import com.example.fedatingapp.entities.Image;
import com.example.fedatingapp.entities.Users;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private View rootLayout;
    private TextView username, userJob, userOld;
    private LinearLayout setting, image, profile;
    private final UserService userService = new UserService();
    private Users users;
    private List<Image> userImage;
    private Long userId;
    private CircleImageView circleImageView;
    public AccountFragment() {
        // Required empty public constructor
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

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        return rootLayout;
    }

    private void binding() {
        username = rootLayout.findViewById(R.id.userName);
        userJob = rootLayout.findViewById(R.id.userJob);
        userOld = rootLayout.findViewById(R.id.userOld);
        setting = rootLayout.findViewById(R.id.btn_setting);
        image = rootLayout.findViewById(R.id.btn_addImage);
        profile = rootLayout.findViewById(R.id.btn_editProfile);
        setting = rootLayout.findViewById(R.id.btn_setting);
        image = rootLayout.findViewById(R.id.btn_addImage);
        profile = rootLayout.findViewById(R.id.btn_editProfile);
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
            userOld.setText(String.valueOf(CountOld(
                    users.getBirthday().getDate(),
                    users.getBirthday().getMonth(),
                    users.getBirthday().getYear()
            )));
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
}