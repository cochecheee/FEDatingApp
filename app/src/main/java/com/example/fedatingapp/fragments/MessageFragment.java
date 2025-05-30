package com.example.fedatingapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.fedatingapp.R;
import com.example.fedatingapp.Service.MessageService;
import com.example.fedatingapp.WebSocket.WebSocketClient;
import com.example.fedatingapp.WebSocket.WebSocketManager;
import com.example.fedatingapp.activities.ChatActivity;
import com.example.fedatingapp.activities.MainActivity;
import com.example.fedatingapp.adapters.LikeAdapter;
import com.example.fedatingapp.adapters.MessageListAdapter;
import com.example.fedatingapp.entities.Message;
import com.example.fedatingapp.models.MessageItem;
import com.example.fedatingapp.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements MessageListAdapter.OnItemClickListener, LikeAdapter.onclickinterface
, WebSocketClient.MessageListener {
    private static final String CHANNEL_ID = "notify_channel";
    MessageService messageService;
    private TokenManager tokenManager;
    WebSocketManager webSocketManager;
    View rootLayout;
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MessageItem> messageList;
    private List<MessageItem> likeList;
    private MessageListAdapter mAdapter;
    private LikeAdapter contactAdapter;
    private Long curentUserId;
    private boolean isLoading = false;
    private EditText findName ;
    public MessageFragment(Long currentUserid) {
        this.curentUserId = currentUserid;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);
        tokenManager = new TokenManager(getActivity());
        messageService = new MessageService("Bearer " + tokenManager.getAccessToken());
        RecyclerView recyclerView = rootLayout.findViewById(R.id.recycler_view_messages);
        webSocketManager = WebSocketManager.getInstance(getActivity());
        webSocketManager.setMessageListener(this);
        messageList = new ArrayList<>();
        likeList = new ArrayList<>();

        mAdapter = new MessageListAdapter(getContext(), messageList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prepareMessageList();
        prepareLikeList();

        contactAdapter = new LikeAdapter(getContext(), likeList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewContact =  rootLayout.findViewById(R.id.recycler_view_likes);
        recyclerViewContact.setLayoutManager(layoutManager);
        recyclerViewContact.setAdapter(contactAdapter);
        //new HorizontalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerViewContact));

        findName = rootLayout.findViewById(R.id.find_name);
        findName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterMessageList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return rootLayout;
    }

    private void filterMessageList(String text)
    {
        List<MessageItem> filteredList = new ArrayList<>();
        if (text.isEmpty()) {
            prepareLikeList();
        } else {
            // Filter the list based on search text
            String searchQuery = text.toLowerCase().trim();
            for (MessageItem item : messageList) {
                if (item.getName().toLowerCase().contains(searchQuery)) {
                    filteredList.add(item);
                }
            }
        }
        likeList.clear();
        likeList.addAll(filteredList);
        contactAdapter.notifyDataSetChanged();
    }
    private void prepareMessageList(){
        messageService.getListMatch(curentUserId, new Callback<List<MessageItem>>() {
            @Override
            public void onResponse(Call<List<MessageItem>> call, Response<List<MessageItem>> response) {
                if (response.isSuccessful())
                {
                    messageList.clear();
                    messageList.addAll(response.body());
                    mAdapter.notifyDataSetChanged();

                }
                else {
                    Log.d("ChatFragment", "onFailure: "+ response.body());
                }
            }

            @Override
            public void onFailure(Call<List<MessageItem>> call, Throwable throwable) {
                Log.d("ChatFragment", "onFailure: "+ throwable.getMessage());
            }
        });
    }

    private void prepareLikeList(){
        messageService.getListMatch(curentUserId, new Callback<List<MessageItem>>() {
            @Override
            public void onResponse(Call<List<MessageItem>> call, Response<List<MessageItem>> response) {
                if (response.isSuccessful() && !response.body().isEmpty())
                {
                    likeList.clear();
                    likeList.addAll(response.body());
                    contactAdapter.notifyDataSetChanged();
                }
                else {
                    Log.d("ChatFragment", "onFailure: "+ response.body());
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

    @Override
    public void onclicklistener(Long receiverId, String reveiverName, String receiverPicture) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECEIVER_USER_ID",receiverId);
        intent.putExtra("RECEIVER_NAME",reveiverName);
        intent.putExtra("RECEIVER_IMAGE",receiverPicture);
        intent.putExtra("CURRENT_USER_ID", curentUserId);
        startActivity(intent);
    }

    @Override
    public void onMessageReceived(Message message) {

    }
}