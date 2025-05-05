package com.example.fedatingapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fedatingapp.R;
import com.example.fedatingapp.activities.LoginActivity;
import com.example.fedatingapp.activities.MainActivity;
import com.example.fedatingapp.adapters.MatchListAdapter;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.api.response.ApiResponse;
import com.example.fedatingapp.models.Match;
import com.example.fedatingapp.models.MatchFeed;
import com.example.fedatingapp.models.Profile;
import com.example.fedatingapp.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment {
    View rootLayout;
    private static final String TAG = "FeedFragment"; // Hoặc MatchFragment
    //    private List<Match> matchList;
    private List<MatchFeed> matchList;
    private MatchListAdapter mAdapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private TokenManager tokenManager;
    private Context mContext;
    private Long currentUserId; // Lưu ID user hiện tại


    public FeedFragment(Long userId) {
        this.userId = userId;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        rootLayout = inflater.inflate(R.layout.fragment_feed, container, false);
//
//
//        RecyclerView recyclerView = rootLayout.findViewById(R.id.recycler_view_matchs);
//        matchList = new ArrayList<>();
//        mAdapter = new MatchListAdapter(getContext(), matchList);
//
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
//        recyclerView.setAdapter(mAdapter);
//
//        prepareMatchList();
//
//        return rootLayout;
        rootLayout = inflater.inflate(R.layout.fragment_feed, container, false); // ** Layout chứa RecyclerView **

        recyclerView = rootLayout.findViewById(R.id.recycler_view_matchs); // ** ID RecyclerView **

        matchList = new ArrayList<>();
        mAdapter = new MatchListAdapter(mContext, matchList); // ** Truyền mContext đã lấy **

        if (mContext != null) {
            apiService = RetrofitClient.getApiService(mContext);
            tokenManager = new TokenManager(mContext);
        } else {
            Log.e(TAG, "Context is null in onCreateView.");
        }

        setupRecyclerView();

        return rootLayout;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Gọi API khi view đã được tạo
        fetchMatchesFromApi();
    }

    private void setupRecyclerView() {
        if (recyclerView == null || mContext == null) return;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }
    private void fetchMatchesFromApi() {
        if (apiService == null || tokenManager == null || mContext == null) {
            Log.e(TAG, "Cannot fetch matches: Prerequisites missing.");
            showErrorView("Lỗi tải dữ liệu.");
            return;
        }

        // ** Lấy token (Nếu không dùng Interceptor) **
         String accessToken = tokenManager.getAccessToken();
         if (accessToken == null || accessToken.isEmpty()) {
             showErrorView("Vui lòng đăng nhập lại.");
             navigateToLogin(); // Ví dụ
             return;
         }
         String bearerToken = "Bearer " + accessToken;

        Log.d(TAG, "Fetching matches...");
        showLoading(true);

        apiService.getMatches(bearerToken).enqueue(new Callback<ApiResponse<List<MatchFeed>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MatchFeed>>> call, Response<ApiResponse<List<MatchFeed>>> response) {
                if (!isAdded() || mContext == null) return;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<MatchFeed> apiMatches = response.body().getData(); // Lấy List<MatchFeed>
                    Log.i(TAG, "API Success: Fetched " + apiMatches.size() + " matches.");
                    // ** Cập nhật Adapter với dữ liệu mới **
                    if (mAdapter != null) {
                        // Có thể cần format ngày tháng hoặc xử lý isNewMatch ở đây trước khi đưa vào Adapter
                        // Ví dụ:
                        // List<MatchFeed> processedMatches = processMatchFeedData(apiMatches);
                        // mAdapter.updateData(processedMatches);
                        mAdapter.updateData(apiMatches); // Hoặc cập nhật trực tiếp nếu không cần xử lý thêm
                    }

                    // Hiển thị trạng thái rỗng hoặc RecyclerView
                    if (apiMatches.isEmpty()) {
                        showEmptyView("Bạn chưa có tương hợp nào.");
                    } else {
                        showRecyclerView();
                    }
                } else {
                    Log.e(TAG, "Error fetching matches: " + response.code());
                    handleApiError(response); // Xử lý lỗi API
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MatchFeed>>> call, Throwable t) {
                if (!isAdded() || mContext == null) return;
                showLoading(false);
                String errorMsg = "Lỗi kết nối mạng.";
                Log.e(TAG, errorMsg, t);
                showErrorView(errorMsg);
            }
        });

    }

    // Các hàm quản lý trạng thái UI
    private void showLoading(boolean isLoading) {
        if (recyclerView != null) recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showEmptyView(String message) {
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
    }

    private void showErrorView(String message) {
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (mContext != null) {
            Toast.makeText(mContext, "Lỗi: " + message, Toast.LENGTH_LONG).show();
        }
    }

    private void showRecyclerView() {
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
    }


    // Hàm xử lý lỗi API tập trung
    private void handleApiError(Response<?> response){
        if (!isAdded() || mContext == null) return;
        String errorMsg = "Lỗi không xác định (" + response.code() + ")";
        if (response.code() == 401) {
            errorMsg = "Phiên đăng nhập hết hạn.";
            Log.w(TAG, "Authentication error (401). Clearing token and navigating to Login.");
            if (tokenManager != null) tokenManager.clearTokens();
            navigateToLogin();
        } else {
            try {
                String errBody = response.errorBody() != null ? response.errorBody().string() : "N/A";
                Log.e(TAG, "API Error " + response.code() + ": " + response.message() + " | Body: " + errBody);
                errorMsg = "Lỗi máy chủ (" + response.code() + ")";
            } catch (Exception e) { Log.e(TAG, "Error parsing error body", e); }
            showErrorView(errorMsg); // Hiển thị lỗi cuối cùng
        }
    }

    // Hàm điều hướng về màn hình Login
    private void navigateToLogin() {
        if (mContext != null && getActivity() != null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finishAffinity();
        } else {
            Log.e(TAG, "Cannot navigate to login, context or activity is null.");
        }
    }


    // Hàm helper để format ngày tháng
    private String formatDisplayDate(String isoDateString) {
        if (isoDateString == null || isoDateString.isEmpty()) {
            return "Gần đây"; // Hoặc trả về chuỗi rỗng
        }
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US); // Hoặc định dạng ISO khác mà backend trả về
            parser.setTimeZone(TimeZone.getTimeZone("UTC")); // Quan trọng nếu backend trả về UTC
            Date date = parser.parse(isoDateString);

            // Format thành dạng mong muốn, ví dụ: "dd MMM. yyyy" hoặc "HH:mm dd/MM"
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM. yyyy", new Locale("vi", "VN"));
            return formatter.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + isoDateString, e);
            return isoDateString; // Trả về gốc nếu lỗi
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null; // Giải phóng context
    }
}