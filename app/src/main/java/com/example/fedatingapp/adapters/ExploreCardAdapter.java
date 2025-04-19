package com.example.fedatingapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.fedatingapp.R;
import com.example.fedatingapp.models.ExploreCategory;

import java.util.List;

public class ExploreCardAdapter extends RecyclerView.Adapter<ExploreCardAdapter.CardViewHolder> {

    private List<ExploreCategory> categories;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ExploreCategory category);
    }

    public ExploreCardAdapter(List<ExploreCategory> categories, OnItemClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.explore_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ExploreCategory category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageBackground;
        private TextView textTitle;
        private TextView textDescription;
        private TextView textBadge;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBackground = itemView.findViewById(R.id.imageBackground);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            textBadge = itemView.findViewById(R.id.textBadge);
        }

        public void bind(final ExploreCategory category, final OnItemClickListener listener) {
            textTitle.setText(category.getTitle());
            textDescription.setText(category.getDescription());

            // Hiển thị hoặc ẩn badge HOT
            if (category.isHot()) {
                textBadge.setVisibility(View.VISIBLE);
            } else {
                textBadge.setVisibility(View.GONE);
            }

            // Load ảnh bằng Glide
            Glide.with(itemView.getContext())
                    .load(category.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(imageBackground);

            // Xử lý sự kiện click
            itemView.setOnClickListener(v -> {
                listener.onItemClick(category);
            });
        }
    }
}