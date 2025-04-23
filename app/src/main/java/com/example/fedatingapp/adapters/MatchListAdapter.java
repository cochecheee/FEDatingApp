package com.example.fedatingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fedatingapp.R;
import com.example.fedatingapp.models.Match;
import com.example.fedatingapp.models.MessageItem;

import java.util.List;

public class MatchListAdapter extends RecyclerView.Adapter<MatchListAdapter.MyViewHolder> {
    private Context context;
    private List<MessageItem> messageList;

    private onclickinterface listener;

    public interface onclickinterface{
        void onclicklistener(Long receiverId, String reveiverName,String receiverPicture);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, date, location;
        ImageView imgProfile, imgContent, imgChat;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.text_name);
            location = view.findViewById(R.id.text_location);
            imgProfile = view.findViewById(R.id.img_profile);
            imgContent = view.findViewById(R.id.img_content);
            imgChat = view.findViewById(R.id.img_chat);
        }
    }


    public MatchListAdapter(Context context, List<MessageItem> messageList, onclickinterface listener) {
        this.context = context;
        this.messageList = messageList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_layout_match, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MessageItem item = messageList.get(position);
        holder.name.setText(item.getName());


        Glide.with(context)
                .load(item.getPicture())
                .into(holder.imgProfile);

        Glide.with(context)
                .load(item.getPicture())
                .into(holder.imgContent);

        holder.imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onclicklistener((long) item.getId(),item.getName(), item.getPicture());
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

}