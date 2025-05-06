package com.example.fedatingapp.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.fedatingapp.R;
import com.example.fedatingapp.api.ApiResponse;
import com.example.fedatingapp.api.ApiService;
import com.example.fedatingapp.api.RetrofitClient;
import com.example.fedatingapp.utils.TokenManager;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Layout(R.layout.adapter_tinder_card)
public class TinderCard {
    private static final String TAG_CARD = "TinderCard"; // Tag log
    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    // ** Thêm ApiService và TokenManager **
    private ApiService apiService;
    private TokenManager tokenManager;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView) {
        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;

        // Khởi tạo service và manager
        if (mContext != null) {
            // Giả sử dùng Interceptor, ApiClient cần context
            this.apiService = RetrofitClient.getApiService(mContext.getApplicationContext());
            this.tokenManager = new TokenManager(mContext.getApplicationContext());
        } else {
            Log.e(TAG_CARD, "Context is null during TinderCard initialization!");
        }
    }

    /**
     * Called when the view is ready to be populated with data.
     * Uses Glide to load the profile image and sets the text views.
     */
    @Resolve
    private void onResolved(){
        // Basic null checks
        if (mProfile == null || mContext == null) {
            Log.e(TAG_CARD, "Profile or Context is null in onResolved. Cannot bind data.");
            // Hide views or show error state on the card if data is invalid
            if(nameAgeTxt != null) nameAgeTxt.setVisibility(android.view.View.GONE);
            if(locationNameTxt != null) locationNameTxt.setVisibility(android.view.View.GONE);
            if(profileImageView != null) profileImageView.setImageResource(R.drawable.default_placeholder_error); // Show error image
            return;
        }

        String imageUrl = mProfile.getImageUrl(); // Get image URL from Profile model
        Log.d(TAG_CARD, "Resolving card for: " + mProfile.getName() + ", Image URL: " + imageUrl);

        // --- Load Image using Glide ---
        Glide.with(mContext)
                .load(imageUrl)
//                .load("https://letsenhance.io/static/73136da51c245e80edc6ccfe44888a99/1015f/MainBefore.jpg")
                .placeholder(R.drawable.default_placeholder) // Placeholder while loading
                .error(R.drawable.default_placeholder_error)   // Image shown on error
                .centerCrop() // Scale type to fill the ImageView
//                .skipMetadata() // ** IMPORTANT: Skips reading EXIF data to avoid format errors **
                // .diskCacheStrategy(DiskCacheStrategy.NONE) // Uncomment to disable disk cache (for debugging)
                 .skipMemoryCache(true) // Uncomment to disable memory cache (for debugging)
                .listener(new RequestListener<Drawable>() { // Add listener for detailed error logging
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG_CARD, "Glide onLoadFailed for URL: " + model, e);
                        // Returning false allows Glide to set the error placeholder drawable
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        Log.d(TAG_CARD, "Glide onResourceReady for URL: " + model);
                        return false; // Let Glide handle setting the resource
                    }
                })
                .into(profileImageView);

        // --- Set Name and Age Text ---
        String name = mProfile.getName() != null ? mProfile.getName() : "";
        String age = mProfile.getAge() != null ? String.valueOf(mProfile.getAge()) : "";
        String nameAgeString = name;
        if (!name.isEmpty() && !age.isEmpty()) {
            nameAgeString += ", " + age;
        } else if (name.isEmpty() && !age.isEmpty()){
            nameAgeString = "Age " + age; // Show only age if name is missing
        }
        nameAgeTxt.setText(nameAgeString);
        nameAgeTxt.setVisibility(nameAgeString.isEmpty() ? android.view.View.GONE : android.view.View.VISIBLE);


        // --- Set Location Text ---
        String locationString = mProfile.getLocation() != null ? mProfile.getLocation().trim() : "";
        locationNameTxt.setText(locationString);
        locationNameTxt.setVisibility(locationString.isEmpty() ? android.view.View.GONE : android.view.View.VISIBLE); // Hide if no location
    }

    @SwipeOut // Swiped Left (Pass)
    private void onSwipedOut(){
        if (mProfile == null || mProfile.getId() == null) {
            Log.e(TAG_CARD,"SwipeOut triggered but profile or ID is null.");
            return;
        }
        Log.d("EVENT", "UI SwipedOut for user: " + mProfile.getName() + " (ID: " + mProfile.getId() + ")");
        // ** Gọi API Dislike **
        sendSwipeApiCall("pass", mProfile.getId());
    }

    @SwipeIn // Swiped Right (Like)
    private void onSwipeIn(){
        if (mProfile == null || mProfile.getId() == null) {
            Log.e(TAG_CARD,"SwipeIn triggered but profile or ID is null.");
            return;
        }
        Log.d("EVENT", "UI SwipedIn for user: " + mProfile.getName() + " (ID: " + mProfile.getId() + ")");
        // ** Gọi API Like **
        sendSwipeApiCall("like", mProfile.getId());
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
        // Called when a swipe is cancelled (e.g., user lifts finger before completing)
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
        // Called when the card is being dragged into the 'SwipeIn' area
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
        // Called when the card is being dragged into the 'SwipeOut' area
    }
    public Profile getProfile() {
        return mProfile;
    }
    // Hàm gọi API cho swipe card
    private void sendSwipeApiCall(final String action, final Long targetUserId) {
        // Kiểm tra prerequisites
        if (apiService == null || mContext == null || targetUserId == null || tokenManager == null) {
            Log.e(TAG_CARD, "Cannot send swipe API call from TinderCard - missing prerequisites");
            return;
        }

        // ** Lấy token (Nếu KHÔNG dùng Interceptor, bỏ comment khối này) **
         String accessToken = tokenManager.getAccessToken();
         if (accessToken == null || accessToken.isEmpty()) {
             Log.e(TAG_CARD, "Cannot send swipe API call - missing auth token");
             Toast.makeText(mContext, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
             return;
         }
         String bearerToken = "Bearer " + accessToken;

        Log.d(TAG_CARD, "Sending API swipe action '" + action + "' for target user ID: " + targetUserId + " from TinderCard");

        Call<ApiResponse<String>> apiCall;

        // Chọn API Call dựa trên action
        switch (action.toLowerCase()) {
            case "like":
                apiCall = apiService.likeUser(targetUserId, bearerToken);
                break;
            case "pass":
            case "skip":
                apiCall = apiService.dislikeUser(targetUserId,bearerToken);
                break;
            case "superlike":
                Log.w(TAG_CARD,"Superlike action triggered, sending 'like' API call for now.");
                // TODO: Thay bằng API superlike khi có
                apiCall = apiService.likeUser(targetUserId, bearerToken); // Tạm
                break;
            default:
                Log.e(TAG_CARD, "Unknown swipe action in sendSwipeApiCall: " + action);
                return;
        }

        // Thực hiện API Call
        apiCall.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    Log.i(TAG_CARD, "API Swipe action '" + action + "' successful for user " + targetUserId + ". Response: " + message);
                    if (message != null && message.toLowerCase().contains("match") && mContext != null) {
                        Log.d(TAG_CARD, "IT'S A MATCH with user " + targetUserId + "!");
                        Toast.makeText(mContext, "Bạn đã tương hợp!", Toast.LENGTH_LONG).show();
                        // TODO: Thông báo cho Fragment/Activity để hiển thị màn hình Match
                        // Có thể dùng LocalBroadcastManager hoặc Interface callback
                    }
                } else {
                    Log.e(TAG_CARD, "API Error sending swipe action '" + action + "' for user " + targetUserId + ": " + response.code());
                    // Xử lý lỗi 401 nếu cần (nhưng tốt hơn là xử lý ở Interceptor hoặc Fragment)
                    if (response.code() == 401 && mContext != null) {
                        // Có thể thông báo lỗi token ở đây nhưng không nên xóa token từ đây
                        Toast.makeText(mContext, "Lỗi xác thực (401)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable throwable) {
                Log.e(TAG_CARD, "API Network error sending swipe action '" + action + "' for user " + targetUserId, throwable);
            }
        });
    }
}