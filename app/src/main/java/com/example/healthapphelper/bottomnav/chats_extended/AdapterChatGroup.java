package com.example.healthapphelper.bottomnav.chats_extended;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterChatGroup {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_RIGHT=1;

    private Context context;
    private ArrayList<ModelGroupChatList> modelGroupChatLists;
    private FirebaseAuth firebaseAuth;
    public AdapterChatGroup(Context context,ArrayList<ModelGroupChatList> modelCroupChats){
        this.context=context;
        this.modelGroupChatLists=modelCroupChats;
        firebaseAuth=FirebaseAuth.getInstance();

    }
    class HolderGroupChat extends RecyclerView.ViewHolder{
        public HolderGroupChat(@NonNull View itemView){
            super(itemView);
        }
    }

}
