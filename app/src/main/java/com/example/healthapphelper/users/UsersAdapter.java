package com.example.healthapphelper.users;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthapphelper.R;
import com.example.healthapphelper.utils.ChatUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder>{

    private ArrayList<User> users = new ArrayList<>();

    private OnItemClickListener listener;
    private ImageView img_on;
    private ImageView img_off;
    private boolean isChat;
    public void updateList(ArrayList<User> newList) {
        users = newList;
        notifyDataSetChanged();
    }
    public UsersAdapter(ArrayList<User> users,boolean isChat){
        this.users = users;
        this.isChat=isChat;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public UsersAdapter(ArrayList<User> users, OnItemClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item_rv, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.username_tv.setText(user.getUsername());

        if (!user.getProfileImage().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(user.getProfileImage()).into(holder.profileImage_iv);
        }

        if (isChat) {
            if ("online".equals(user.getStatus())) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(user);
            }
        });
    }
    @Override
    public int getItemCount() {
        return users.size();
    }



}