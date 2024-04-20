package com.example.healthapphelper.users;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapphelper.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    CircleImageView profileImage_iv;
    TextView username_tv;
     CircleImageView img_on;
     CircleImageView img_off;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage_iv=itemView.findViewById(R.id.profile_iv);
        username_tv=itemView.findViewById(R.id.username_tv);
        img_on=itemView.findViewById(R.id.statusOnline);
        img_off=itemView.findViewById(R.id.statusOffline);
    }
}
