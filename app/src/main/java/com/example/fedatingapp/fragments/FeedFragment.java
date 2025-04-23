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
import com.example.fedatingapp.adapters.MatchListAdapter;
import com.example.fedatingapp.models.Match;
import com.example.fedatingapp.models.MessageItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FeedFragment extends Fragment implements MatchListAdapter.onclickinterface {
    MessageService messageService = new MessageService();
    View rootLayout;
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<MessageItem> messageList;
    private MatchListAdapter mAdapter;
    private final long userId;


    public FeedFragment(Long userId) {
        this.userId = userId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_feed, container, false);


        RecyclerView recyclerView = rootLayout.findViewById(R.id.recycler_view_matchs);
        messageList = new ArrayList<>();
        mAdapter = new MatchListAdapter(getContext(), messageList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        prepareMatchList();

        return rootLayout;
    }


    private void prepareMatchList(){

        messageService.getListMatch(userId, new Callback<List<MessageItem>>() {
            @Override
            public void onResponse(Call<List<MessageItem>> call, Response<List<MessageItem>> response) {
                if (response.isSuccessful())
                {
                    messageList.clear();
                    messageList.addAll(response.body());
                    mAdapter.notifyDataSetChanged();
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
    public void onclicklistener(Long receiverId, String reveiverName, String receiverPicture) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("RECEIVER_USER_ID",receiverId);
        intent.putExtra("RECEIVER_NAME",reveiverName);
        intent.putExtra("RECEIVER_IMAGE",receiverPicture);
        intent.putExtra("CURRENT_USER_ID", userId);
        startActivity(intent);
    }
}