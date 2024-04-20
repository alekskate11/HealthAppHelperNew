package com.example.healthapphelper.chats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapphelper.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class CahtViewHolder extends RecyclerView.ViewHolder {

    CircleImageView chat_iv;
    TextView chat_name_tv;
    public  Button btnDeleteChat;

    public CahtViewHolder(@NonNull View itemView) {
        super(itemView);
        chat_iv = itemView.findViewById(R.id.profile_iv);
        chat_name_tv = itemView.findViewById(R.id.username_tv);
        btnDeleteChat = itemView.findViewById(R.id.btnDeleteChat);
    }
}