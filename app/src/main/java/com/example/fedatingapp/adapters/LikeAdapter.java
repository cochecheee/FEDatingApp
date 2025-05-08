package com.example.fedatingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.models.MessageItem;

import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ContactViewHolder>{

    private Context context;
    private List<MessageItem> likeList;
    private onclickinterface listener;

    public interface onclickinterface{
        void onclicklistener(Long receiverId, String reveiverName,String receiverPicture);
    }

    public LikeAdapter(Context context, List<MessageItem> likeList, onclickinterface listener) {
        this.context = context;
        this.likeList = likeList;
        this.listener = listener;
    }

    @Override
    public LikeAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_like_item, parent, false);

        return new LikeAdapter.ContactViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return likeList.size();
    }


    class ContactViewHolder extends RecyclerView.ViewHolder {
        LinearLayout likeLayout;
        TextView likeName;
        ImageView likeImage;


        ContactViewHolder(View itemView) {

            super(itemView);
            likeLayout = itemView.findViewById(R.id.layout_like);
            likeName = itemView.findViewById(R.id.text_like_name);
            likeImage = itemView.findViewById(R.id.circle_image_like);

        }
    }

    @Override
    public void onBindViewHolder(LikeAdapter.ContactViewHolder holder, final int position) {
        final MessageItem item = likeList.get(position);
        holder.likeName.setText(item.getName());

        Glide.with(context)
                .load(item.getPicture())
                .into(holder.likeImage);

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onclicklistener((long) item.getId(),item.getName(),item.getPicture());
            }
        });
    }
}