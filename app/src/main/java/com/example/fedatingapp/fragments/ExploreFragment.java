package com.example.fedatingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.adapters.ExploreCardAdapter;
import com.example.fedatingapp.databinding.ExploreBinding;
import com.example.fedatingapp.models.ExploreViewModel;
import com.example.fedatingapp.models.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment {

    private ExploreBinding binding;
    private ExploreViewModel viewModel;
    private ExploreCardAdapter exploreAdapter;

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

        setupRecyclerView();
        setupBannerClickListener();
        setupMainBanner();
        observeViewModel();
    }

    private void setupRecyclerView() {
        // Khởi tạo adapter với dữ liệu rỗng ban đầu
        exploreAdapter = new ExploreCardAdapter(new ArrayList<>(), category -> {
            // Xử lý khi người dùng click vào một thẻ tìm kiếm
            Toast.makeText(requireContext(), "Đã chọn: " + category.getTitle(), Toast.LENGTH_SHORT).show();
            // Có thể điều hướng đến màn hình chi tiết của danh mục
            // navigateToCategoryDetail(category);
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
        Glide.with(requireContext())
                .load("https://example.com/featured_banner.jpg")
                .centerCrop()
                .into(binding.imageView5);
    }

    private void observeViewModel() {
        // Quan sát dữ liệu danh mục
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            exploreAdapter = new ExploreCardAdapter(categories, category -> {
                Toast.makeText(requireContext(), "Đã chọn: " + category.getTitle(), Toast.LENGTH_SHORT).show();
                // navigateToCategoryDetail(category);
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
}