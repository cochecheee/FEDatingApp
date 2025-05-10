package com.example.fedatingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.models.MatchFeed;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MyViewHolder> {
    private Context context;
    //    private List<Match> matchList;
    private List<MatchFeed> matchList;
    private clickInterface clickInterface;
    public interface clickInterface{
        void likeClick(Long matchUserId);
        void disLikeClick(Long disLikeUserId);
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
//        TextView name, date, location;
        ImageView imgLike, imgDislike;
        TextView name, date, location, tvNewMatch; // Thêm tvNewMatch
        ImageView imgContent; // Ảnh lớn
        CircleImageView imgProfile; // Ảnh profile tròn

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_name);
            date = view.findViewById(R.id.text_date);
            location = view.findViewById(R.id.text_location);
            imgProfile = view.findViewById(R.id.img_profile); // ID ảnh tròn
            imgContent = view.findViewById(R.id.img_content); // ID ảnh lớn
            tvNewMatch = view.findViewById(R.id.text_new_match); // ** ID TextView "Nouveau Match !" **
            // Ánh xạ thêm các nút Like, Chat nếu cần bắt sự kiện click ở đây
            imgLike = view.findViewById(R.id.img_like);
            imgDislike = view.findViewById(R.id.img_dislike);
        }
    }


    public MatchListAdapter(Context context, List<MatchFeed> matchList, clickInterface clickInterface) {
        this.context = context;
        this.matchList = matchList;
        this.clickInterface = clickInterface;
    }

    @NonNull // ** Thêm NonNull **
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // ** Thêm NonNull **
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_layout_match, parent, false); // ** Layout item của bạn **
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MatchFeed item = matchList.get(position);
        // --- Cập nhật dữ liệu ---
        holder.name.setText(item.getName());
        holder.date.setText(item.getDate()); // Ngày đã được format sẵn trong Match model
        holder.location.setText(item.getLocation());

        // --- Hiển thị "Nouveau Match !" nếu cần ---
        if (holder.tvNewMatch != null) { // Kiểm tra null phòng trường hợp layout chưa có ID này
            holder.tvNewMatch.setVisibility(item.isNewMatch() ? View.VISIBLE : View.GONE);
        }

        // --- Load ảnh bằng Glide ---
        if (context != null) { // Kiểm tra context trước khi dùng Glide
            // Load ảnh profile tròn
            Glide.with(context)
                    .load(item.getPictureUrl()) // ** Sử dụng getPictureUrl() **
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder_error) // Ảnh lỗi
                    .circleCrop() // Cắt tròn
                    .into(holder.imgProfile);

            // Load ảnh lớn (nếu ảnh giống nhau)
            Glide.with(context)
                    .load(item.getPictureUrl()) // ** Sử dụng getPictureUrl() **
                    .placeholder(R.drawable.default_placeholder)
                    .error(R.drawable.default_placeholder_error)
                    .centerCrop() // Crop cho vừa
                    .into(holder.imgContent);
        }

        // --- Thêm sự kiện click nếu cần ---
        holder.itemView.setOnClickListener(v -> {
            // Xử lý khi click vào toàn bộ item (ví dụ: mở profile)
            Log.d("MatchListAdapter", "Clicked on match: " + item.getName());
            // Intent intent = new Intent(context, ProfileDetailActivity.class);
            // intent.putExtra("USER_ID", item.getId()); // Truyền ID
            // context.startActivity(intent);
        });
        holder.imgLike.setOnClickListener(v ->{
            clickInterface.likeClick(Long.parseLong(item.getId()));
        });
        holder.imgDislike.setOnClickListener(v ->{
            clickInterface.disLikeClick(Long.parseLong(item.getId()));
        });
    }

    @Override
    public int getItemCount() {
        return matchList == null ? 0 : matchList.size(); // Kiểm tra null
    }

    // ** Hàm để cập nhật dữ liệu cho Adapter **
    public void updateData(List<MatchFeed> newMatchList) {
        if (newMatchList != null) {
            this.matchList.clear();
            this.matchList.addAll(newMatchList);
            notifyDataSetChanged(); // Báo cho RecyclerView cập nhật lại
        } else {
            this.matchList.clear();
            notifyDataSetChanged();
        }
    }

}