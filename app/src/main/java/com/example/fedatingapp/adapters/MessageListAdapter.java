package com.example.fedatingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.models.ExploreCategory;
import com.example.fedatingapp.models.MessageItem;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {
    private Context context;
    private List<MessageItem> messageList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Long receiverId, String reveiverName,String receiverPicture);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, content, count;
        ImageView thumbnail;
        RelativeLayout viewIndicator;
        RelativeLayout relativeLayout;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_name);
            content = view.findViewById(R.id.text_content);
            thumbnail = view.findViewById(R.id.thumbnail);
            viewIndicator = view.findViewById(R.id.layout_dot_indicator);
            relativeLayout = view.findViewById(R.id.layout_main);
        }
    }


    public MessageListAdapter(Context context, List<MessageItem> messageList, OnItemClickListener listener) {
        this.context = context;
        this.messageList = messageList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_message_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final MessageItem item = messageList.get(position);
        holder.name.setText(item.getName());
        if (item.getCount() <= 0)
        {
            holder.content.setText("Bat dau tro chuyen ngay");
        }
        else if (item.getContent().contains("http"))
        {
            holder.content.setText("Ảnh");
        }
        else {
            holder.content.setText(item.getContent());
        }


        if(item.getCount() <= 0){
            holder.viewIndicator.setVisibility(View.INVISIBLE);
        }

        Glide.with(context)
                .load(item.getPicture())
                .into(holder.thumbnail);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick((long) item.getId(),item.getName(),item.getPicture());
            }
        });


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}
