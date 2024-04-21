package com.example.healthapphelper.bottomnav.chats_extended;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthapphelper.R;
import com.example.healthapphelper.users.UserViewHolder;

import java.io.IOException;
import java.util.ArrayList;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.HolderViewChatList>{
    private Context context;
    private ArrayList<ModelGroupChatList> groupChatLists;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChatList> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }


    @NonNull
    @Override
    public HolderViewChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chatlist_row, parent, false);
        return new HolderViewChatList(view);
    }
    @Override
    public void onBindViewHolder(@NonNull HolderViewChatList holder, int position) {
        ModelGroupChatList model = groupChatLists.get(position);
        String groupId = model.groupId;
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        holder.groupTitleTv.setText(groupTitle);
        holder.nameTv.setText("Sender Name");

        Glide.with(context)
                .load(groupIcon)
                .placeholder(R.drawable.username_icon)
                .into(holder.groupIconIv);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,GroupCreateActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    static class HolderViewChatList extends RecyclerView.ViewHolder {
        private ImageView groupIconIv;
        private TextView groupTitleTv, nameTv, messagetV;

        public HolderViewChatList(@NonNull View itemView) {
            super(itemView);

            groupIconIv = itemView.findViewById(R.id.profile_iv);
            groupTitleTv = itemView.findViewById(R.id.groupTitle_tv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messagetV = itemView.findViewById(R.id.messageGroupTv);
        }
    }
}