package com.example.fedatingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.entities.Image;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<Image> imageList;
    private OnImageDeleteListener deleteListener; // Interface để gọi hàm xóa


    // Interface định nghĩa callback
    public interface OnImageDeleteListener {
        void onImageDelete(Image image);
    }

    // Constructor với thêm listener
    public ImageAdapter(Context context, List<Image> imageList, OnImageDeleteListener listener) {
        this.context = context;
        this.imageList = imageList;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Image image = imageList.get(position);
        Glide.with(context)
                .load(image.getImage())
                .into(holder.imageView);

        holder.imageButton.setOnClickListener(v -> {
            if (deleteListener != null && image != null) {
                deleteListener.onImageDelete(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton imageButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageButton = itemView.findViewById(R.id.btnDelete);

        }
    }
}
