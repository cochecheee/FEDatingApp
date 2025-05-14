package com.example.fedatingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.SearchCardService;
import com.example.fedatingapp.Service.UserService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.adapters.ExploreCardAdapter;
import com.example.fedatingapp.databinding.ExploreBinding;
import com.example.fedatingapp.models.Profile;
import com.example.fedatingapp.entities.Users;
import com.example.fedatingapp.models.ExploreViewModel;
import com.example.fedatingapp.models.GridSpacingItemDecoration;
import com.example.fedatingapp.models.Notification;
import com.example.fedatingapp.utils.NotificationUtils;
import com.example.fedatingapp.utils.TokenManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment implements WebSocketClient.Listener {
    private SearchCardService searchCardService;
    private TokenManager tokenManager;
    private UserService userService;
    private ExploreBinding binding;
    private ExploreViewModel viewModel;
    private ExploreCardAdapter exploreAdapter;
    private final Long userId;
    public ExploreFragment(Long userId)
    {
        this.userId = userId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        tokenManager = new TokenManager(getActivity());
        userService = new UserService("Bearer " + tokenManager.getAccessToken());
        searchCardService = new SearchCardService("Bearer " + tokenManager.getAccessToken());
        setupRecyclerView();
        setupBannerClickListener();
        setupMainBanner();
        observeViewModel();
    }

    private void setupRecyclerView() {
        // Khởi tạo adapter với dữ liệu rỗng ban đầu
        exploreAdapter = new ExploreCardAdapter(new ArrayList<>(), category -> {

        });

        // Cấu hình RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(exploreAdapter);
        recyclerView.setHasFixedSize(true);
        // Thêm khoảng cách giữa các item
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));
    }

    private void setupBannerClickListener() {
        // Xử lý khi người dùng click vào banner chính
        binding.constraintLayout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Khám phá ngay!", Toast.LENGTH_SHORT).show();
            // Có thể điều hướng đến màn hình đề xuất đặc biệt
            // navigateToFeaturedSuggestions();
        });
    }

    private void setupMainBanner() {
        // Load ảnh cho banner chính

        userService.getUserInfo(userId, new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful())
                {
                    Users user = response.body();
                    binding.textView9.setText(user.getSexualOrientation());
                    if (user.getSexualOrientation() == null)
                    {
                        binding.textView9.setText("Chưa cập nhật");
                    }
                    else {
                        binding.textView9.setText(user.getSexualOrientation());
                    }
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable throwable) {

            }
        });
        Glide.with(requireContext())
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQPv4Qp1LoG0y_f7_OAP8rmWJkkb1wdQEzCoQ&s")
                .centerCrop()
                .into(binding.imageView5);
    }

    private void observeViewModel() {

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            exploreAdapter = new ExploreCardAdapter(categories, category -> {
                searchCardService.findByInterests(category.getTitle(), new Callback<List<Profile>>() {
                    @Override
                    public void onResponse(Call<List<Profile>> call, Response<List<Profile>> response) {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            SwipeViewFragment swipeFragment = new SwipeViewFragment();

                            // Tạo Bundle để truyền dữ liệu
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("listProfile", (Serializable) response.body());

                            // Gắn Bundle vào Fragment
                            swipeFragment.setArguments(bundle);

                            // Thay thế Fragment
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, swipeFragment);

                            transaction.commit();
                        }
                        else {
                            Toast.makeText(getContext(), "Khong tim thay "+category.getTitle() , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Profile>> call, Throwable throwable) {
                        Toast.makeText(getContext(), "Khong tim thay" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            binding.recyclerView.setAdapter(exploreAdapter);
        });

        // Quan sát trạng thái loading
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Hiển thị hoặc ẩn loading indicator
            if (isLoading) {
                // Hiển thị loading indicator nếu cần
            } else {
                // Ẩn loading indicator
            }
        });

        // Quan sát thông báo lỗi
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNotifyReceived(Notification notification) {

    }
}