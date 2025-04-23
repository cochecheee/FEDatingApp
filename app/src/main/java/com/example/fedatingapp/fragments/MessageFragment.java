package com.example.fedatingapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.MessageService;
import com.example.fedatingapp.activities.ChatActivity;
import com.example.fedatingapp.activities.MainActivity;
import com.example.fedatingapp.adapters.LikeAdapter;
import com.example.fedatingapp.adapters.MessageListAdapter;
import com.example.fedatingapp.models.MessageItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageListAdapter.OnItemClickListener{
    MessageService messageService = new MessageService();

    View rootLayout;
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MessageItem> messageList;
    private MessageListAdapter mAdapter;
    private Long curentUserId;
    public MessageFragment(Long currentUserid) {
        this.curentUserId = currentUserid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = rootLayout.findViewById(R.id.recycler_view_messages);
        messageList = new ArrayList<>();


        mAdapter = new MessageListAdapter(getContext(), messageList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prepareMessageList();

        LikeAdapter contactAdapter = new LikeAdapter(getContext(), messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewContact =  rootLayout.findViewById(R.id.recycler_view_likes);
        recyclerViewContact.setLayoutManager(layoutManager);
        recyclerViewContact.setAdapter(contactAdapter);
        //new HorizontalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerViewContact));


        return rootLayout;
    }


    private void prepareMessageList(){
        messageService.getListMatch(1L, new Callback<List<MessageItem>>() {
            @Override
            public void onResponse(Call<List<MessageItem>> call, Response<List<MessageItem>> response) {
                if (response.isSuccessful())
                {
                    messageList.clear(); // Xóa dữ liệu cũ (tùy chọn)
                    messageList.addAll(response.body());
                    mAdapter.notifyDataSetChanged(); // Thông báo adapter cập nhật
                    Log.d("ChatFragment", "onFailure: "+ messageList.get(0).getPicture());

                }
            }

            @Override
            public void onFailure(Call<List<MessageItem>> call, Throwable throwable) {
                Log.d("ChatFragment", "onFailure: "+ throwable.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(Long receiverId, String reveiverName, String receiverPicture) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECEIVER_USER_ID",receiverId);
        intent.putExtra("RECEIVER_NAME",reveiverName);
        intent.putExtra("RECEIVER_IMAGE",receiverPicture);
        intent.putExtra("CURRENT_USER_ID", curentUserId);
        startActivity(intent);
    }
}