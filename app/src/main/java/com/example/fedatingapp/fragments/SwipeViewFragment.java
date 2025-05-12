package com.example.fedatingapp.fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fedatingapp.R;

import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.models.Profile;
import com.example.fedatingapp.models.TinderCard;
import com.example.fedatingapp.utils.TokenManager;
import com.example.fedatingapp.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList; // ** Import ArrayList **
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SwipeViewFragment extends Fragment {

    private static final String TAG = "SwipeViewFragment";

    private View rootLayout;
    private FloatingActionButton fabBack, fabLike, fabSkip, fabSuperLike, fabBoost;
    private SwipePlaceHolderView mSwipeView;

    private Context mContext;
    private ApiService apiService;
    private TokenManager tokenManager; // ** Khai báo TokenManager **

    // ** Danh sách lưu trữ Profile thẻ đang hiển thị **
    private List<Profile> currentDisplayedCards = new ArrayList<>();

    public SwipeViewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootLayout = inflater.inflate(R.layout.fragment_swipe_view, container, false);

        // Ánh xạ Views
        mSwipeView = rootLayout.findViewById(R.id.swipeView);
        fabBack = rootLayout.findViewById(R.id.fabBack);
        fabLike = rootLayout.findViewById(R.id.fabLike);
        fabSkip = rootLayout.findViewById(R.id.fabSkip);
        fabSuperLike = rootLayout.findViewById(R.id.fabSuperLike);
        fabBoost = rootLayout.findViewById(R.id.fabBoost);

        mContext = getActivity();
        if (mContext != null) {
            // ** Sử dụng ApiClient của bạn **
//            apiService = RetrofitClient.getApiService();
            apiService = RetrofitClient.getApiService(mContext); // Nếu ApiClient cần Context (cho Interceptor)
            tokenManager = new TokenManager(mContext); // ** Khởi tạo TokenManager **
        } else {
            Log.e(TAG, "Context is null in onCreateView.");
        }

        setupSwipeView();
        setupButtonClickListeners();

        return rootLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Tải dữ liệu thẻ sau khi view đã được tạo hoàn chỉnh
        if (apiService != null && tokenManager != null) { // Đảm bảo service và manager đã sẵn sàng
            fetchDiscoveryCards();
        } else {
            showErrorView("Không thể khởi tạo dịch vụ.");
        }
    }

    private void setupSwipeView() {
        if (mSwipeView == null || mContext == null || getActivity() == null) return;

        int bottomMargin = Utils.dpToPx(100);
        Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        // Cấu hình kích thước và vị trí có thể không cần thiết nếu layout thẻ đã ổn
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));
    }

    private void setupButtonClickListeners() {
        fabSkip.setOnClickListener(v -> {
            animateFab(fabSkip);
            performSwipeAction("pass", false); // Gọi hàm xử lý tập trung
        });

        fabLike.setOnClickListener(v -> {
            animateFab(fabLike);
            performSwipeAction("like", true); // Gọi hàm xử lý tập trung
        });

        fabBack.setOnClickListener(v -> {
            animateFab(fabBack);
            if (mSwipeView != null) {
                mSwipeView.undoLastSwipe();
                Toast.makeText(mContext, "Hoàn tác (UI)", Toast.LENGTH_SHORT).show();
            }
        });

        fabSuperLike.setOnClickListener(v -> {
            animateFab(fabSuperLike);
            performSwipeAction("superlike", true); // Gọi hàm xử lý tập trung
        });

        fabBoost.setOnClickListener(v -> {
            animateFab(fabBoost);
            if (mContext != null) Toast.makeText(mContext, "Boost!", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Hàm xử lý TẬP TRUNG cho các hành động swipe (từ nút bấm).
     * Nó sẽ gọi API và sau đó kích hoạt hiệu ứng swipe trên UI.
     * @param action Loại hành động ("like", "pass", "superlike").
     * @param swipeInDirection Hướng vuốt trên UI (true = vào/phải, false = ra/trái).
     */
    private void performSwipeAction(String action, boolean swipeInDirection) {
        if (mContext == null || apiService == null) {
            Log.e(TAG, "Context or ApiService is null in performSwipeAction");
            return;
        }

        if (!currentDisplayedCards.isEmpty()) {
            Profile topCardProfile = currentDisplayedCards.get(0); // Lấy dữ liệu thẻ trên cùng
            Long targetUserId = topCardProfile.getId(); // ** Cần có getId() trong Profile **

            if (targetUserId == null) {
                Log.e(TAG, "Target user ID is null or empty for the top card.");
                // Có thể xóa thẻ lỗi này và thử lại với thẻ tiếp theo
                currentDisplayedCards.remove(0);
                if (mSwipeView != null)
                    mSwipeView.doSwipe(swipeInDirection); // Vẫn thực hiện swipe UI
                checkIfEmptyAfterSwipe();
                return;
            }

            // Gọi API swipe từ Fragment
            sendSwipeApiCall(action, targetUserId);

            // Kích hoạt hiệu ứng swipe trên UI
            if (mSwipeView != null) {
                mSwipeView.doSwipe(swipeInDirection);
            }

            // Xóa thẻ đã xử lý khỏi danh sách dữ liệu
            currentDisplayedCards.remove(0);
            checkIfEmptyAfterSwipe();

        } else {
            Log.w(TAG, "No cards left to " + action);
            if (mContext != null)
                Toast.makeText(mContext, "Đã hết thẻ.", Toast.LENGTH_SHORT).show();
            showEmptyView("Đã hết thẻ để xem.");
        }
    }
    /**
     * Hàm helper để xóa một thẻ khỏi danh sách currentDisplayedCards dựa trên ID.
     * @param userIdToRemove ID của người dùng cần xóa.
     */
    private void removeCardFromListById(Long userIdToRemove) {
        if (userIdToRemove == null || currentDisplayedCards == null) return; // Thêm kiểm tra list null

        // ** Sử dụng Iterator để duyệt và xóa **
        Iterator<Profile> iterator = currentDisplayedCards.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Profile profile = iterator.next();
            // So sánh ID (đảm bảo profile và ID của nó không null)
            if (profile != null && userIdToRemove.equals(profile.getId())) {
                iterator.remove(); // Xóa phần tử hiện tại một cách an toàn
                removed = true;
                Log.d(TAG, "Removed profile with ID " + userIdToRemove + " from data list.");
                break; // Thoát vòng lặp vì đã tìm thấy và xóa
            }
        }

        if (!removed) {
            Log.w(TAG, "Could not find profile with ID " + userIdToRemove + " in data list to remove.");
        }
    }


    // Hàm gọi API để lấy thẻ
    private void fetchDiscoveryCards() {
        if (apiService == null || mContext == null || tokenManager == null) { // ** Thêm kiểm tra tokenManager **
            Log.e(TAG, "ApiService, Context or TokenManager is null.");
            showErrorView("Lỗi cấu hình.");
            return;
        }

        // ** Lấy accessToken từ TokenManager **
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            showErrorView("Lỗi xác thực. Vui lòng đăng nhập lại.");
            Log.e(TAG, "Auth token is missing.");
            // Điều hướng về Login?
            return;
        }
        // ** Tạo chuỗi Bearer Token **
        String bearerToken = "Bearer " + accessToken;

        showLoading(true);
        if (mSwipeView != null) mSwipeView.removeAllViews();
        currentDisplayedCards.clear();

        // ** Gọi API dùng ApiService và Callback đúng kiểu **
//        apiService.getDiscoveryCards("Bearer " + authToken).enqueue(new Callback<ApiResponse<List<Profile>>>() {
        apiService.getDiscoveryCards(bearerToken).enqueue(new Callback<ApiResponse<List<Profile>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Profile>>> call, @NonNull Response<ApiResponse<List<Profile>>> response) {
                if (!isAdded() || mContext == null) return;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Profile> fetchedCards = response.body().getData(); // Lấy data từ ApiResponse
                    Log.i(TAG, "API Success: Fetched " + fetchedCards.size() + " cards.");

                    if (fetchedCards.isEmpty()) {
                        showEmptyView("Không tìm thấy ai phù hợp.");
                    } else {
                        currentDisplayedCards.addAll(fetchedCards); // Thêm vào danh sách của Fragment
                        for (Profile profile : currentDisplayedCards) { // Duyệt qua danh sách mới
                            if (profile != null && profile.getId() != null && mSwipeView != null) {
                                // ** Tạo TinderCard với Profile **
                                mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
                            } else {
                                Log.w(TAG, "Skipping invalid profile data received from API.");
                            }
                        }
                        if (!currentDisplayedCards.isEmpty() && mSwipeView.getChildCount() > 0) {
                            showDataView();
                        } else {
                            showEmptyView("Không thể hiển thị thẻ.");
                        }
                    }
                } else {
                    // Xử lý lỗi HTTP
                    String errorMsg = "Lỗi tải thẻ: " + response.code();
                    Log.e(TAG, errorMsg + " - " + response.message());
                    showErrorView(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Profile>>> call, @NonNull Throwable t) {
                if (!isAdded() || mContext == null) return;
                showLoading(false);
                String errorMsg = "Lỗi kết nối mạng.";
                Log.e(TAG, errorMsg, t);
                showErrorView(errorMsg);
            }
        });
    }

    // Hàm gọi API Swipe từ Fragment
    private void sendSwipeApiCall(final String action, final Long targetUserId) { // ** Nhận Long ID **
        // Kiểm tra các thành phần cần thiết
        if (apiService == null || mContext == null || targetUserId == null || tokenManager == null) {
            Log.e(TAG, "Cannot send swipe API call - missing prerequisites");
            return;
        }
        Log.d(TAG, "Sending API swipe action '" + action + "' for target user ID: " + targetUserId);

        // khi không dùng authinterceptor
        // ** Lấy accessToken từ TokenManager **
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.isEmpty()) {
            showErrorView("Lỗi xác thực. Vui lòng đăng nhập lại.");
            Log.e(TAG, "Auth token is missing.");
            // Điều hướng về Login?
            return;
        }
        // ** Tạo chuỗi Bearer Token **
        String bearerToken = "Bearer " + accessToken;

        Call<ApiResponse<String>> apiCall = null;
        switch (action.toLowerCase()) { // Chuyển sang chữ thường để so sánh dễ hơn
            case "like":
                // Gọi API likeUser
                apiCall = apiService.likeUser(targetUserId,bearerToken); // Không cần truyền token nếu dùng Interceptor
                break;
            case "pass":
            case "skip": // Có thể dùng "pass" hoặc "skip" cho dislike
                // Gọi API dislikeUser
                Log.w(TAG, "Dislike API call not implemented yet."); // Thông báo nếu chưa có
//                apiCall = apiService.likeUser(targetUserId); // Không cần truyền token nếu dùng Interceptor
                break;
            case "superlike":
                // Gọi API superlikeUser (nếu có endpoint riêng)
                // apiCall = apiService.superlikeUser(targetUserIdLong); // Bỏ comment nếu có API này
                // Hoặc nếu superlike cũng là một dạng "like" đặc biệt gửi qua API khác
                Log.w(TAG, "Superlike API call not implemented yet."); // Thông báo nếu chưa có
                // Tạm thời coi như like thường để ví dụ
//                apiCall = apiService.likeUser(targetUserId); // ** THAY BẰNG API SUPERLIKE KHI CÓ **
                break;
            default:
                Log.e(TAG, "Unknown swipe action: " + action);
                return; // Không thực hiện gì nếu action không xác định
        }

        apiCall.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "API Swipe action '" + action + "' sent successfully for user " + targetUserId);
                    // TODO: Xử lý khi có Match (ví dụ: nếu code là 201 hoặc response body chứa thông tin match)
                    // if ((action.equals("like") || action.equals("superlike")) && response.code() == 201) {
                    //     Log.d(TAG, "IT'S A MATCH with " + targetUserId);
                    //     // Hiển thị màn hình/dialog Match
                    // }
                } else {
                    // Xử lý lỗi từ server khi gửi swipe
                    Log.e(TAG, "API Error sending swipe action '" + action + "' for user " + targetUserId + ": " + response.code());
                    if (response.code() == 401) {
                        // Lỗi token hết hạn khi đang swipe
                        tokenManager.clearTokens();
                        if (mContext != null) Toast.makeText(mContext, "Phiên đăng nhập hết hạn.", Toast.LENGTH_SHORT).show();
                        // Điều hướng về Login
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable throwable) {
                if (!isAdded()) return;
                Log.e(TAG, "API Network error sending swipe action '" + action + "' for user " + targetUserId, throwable);
                // Thường không cần thông báo lỗi mạng cho từng swipe, chỉ log
            }
        });
    }

    // Hàm kiểm tra và hiển thị trạng thái rỗng sau khi vuốt hết thẻ
    private void checkIfEmptyAfterSwipe() {
        if (mSwipeView != null) {
            // Dùng postDelayed để chờ hiệu ứng swipe kết thúc và view được remove
            mSwipeView.postDelayed(() -> {
                // Kiểm tra lại isAdded và context phòng trường hợp Fragment bị hủy trong lúc delay
                if (isAdded() && mContext != null && currentDisplayedCards.isEmpty() && mSwipeView.getChildCount() == 0) {
                    Log.i(TAG, "All cards have been swiped. Showing empty view.");
                    showEmptyView("Bạn đã xem hết mọi người quanh đây!");
                }
            }, 500); // Chờ khoảng 500ms (điều chỉnh nếu cần)
        }
    }

    // Các hàm quản lý trạng thái hiển thị UI
    private void showLoading(boolean isLoading) {
        if (mSwipeView != null) mSwipeView.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private void showEmptyView(String message) {
        if (mSwipeView != null) mSwipeView.setVisibility(View.GONE);
    }

    private void showErrorView(String message) {
        if (mSwipeView != null) mSwipeView.setVisibility(View.GONE);
        if (mContext != null) {
            Toast.makeText(mContext, "Lỗi: " + message, Toast.LENGTH_LONG).show();
        }
    }

    private void showDataView() {
        if (mSwipeView != null) mSwipeView.setVisibility(View.VISIBLE);
    }

    // Animation cho FAB
    private void animateFab(final FloatingActionButton fab) {
        if (fab == null) return;
        fab.animate().scaleX(0.7f).scaleY(0.7f).setDuration(100).withEndAction(() -> {
            if (fab != null) {
                fab.animate().scaleX(1f).scaleY(1f).setDuration(100);
            }
        });
    }
}