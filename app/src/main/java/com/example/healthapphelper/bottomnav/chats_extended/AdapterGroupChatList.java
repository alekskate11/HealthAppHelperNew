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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChatList extends RecyclerView.Adapter<AdapterGroupChatList.HolderGroupChatList> {
    private Context context;
    private ArrayList<ModelGroupChatList> groupChatLists;


    public AdapterGroupChatList(Context context,ArrayList<ModelGroupChatList> groupChatLists) {
        this.context = context;
        this.groupChatLists=groupChatLists;
    }
    @NonNull
    @Override
    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chatlist_row, parent, false);
        return new HolderGroupChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {
        ModelGroupChatList model = groupChatLists.get(position);
       final String groupId = model.groupId;
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();
        holder.nameTv.setText("");
        holder.messagetV.setText("");

        holder.groupTitleTv.setText(groupTitle);
        holder.nameTv.setText("Sender Name");
loadLastMessege(model,holder);

        holder.groupTitleTv.setText(groupTitle);
        try{
            Picasso.get().load(groupIcon).placeholder(R.drawable.username_icon).into(holder.groupIconIv);
        }catch(Exception e){
            holder.groupIconIv.setImageResource(R.drawable.username_icon);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,GroupChatActivity.class);
                intent.putExtra("groupId",groupId);
                context.startActivity(intent);
            }
        });
    }

    private void loadLastMessege(ModelGroupChatList model, HolderGroupChatList holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(model.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String message=""+ds.child("message").getValue();
                            String timestamp=""+ds.child("timestamp").getValue();
                            String sender =""+ds.child("sender").getValue();

                        holder.messagetV.setText(message);
                 DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
                 ref.orderByChild("uid").equalTo(sender)
                         .addValueEventListener(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 for(DataSnapshot ds:snapshot.getChildren()){
                                     String name=""+ds.child("name").getValue();
                                     holder.nameTv.setText(name);

                                 }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    public int getItemCount() {
        return groupChatLists.size();
    }

    static class HolderGroupChatList extends RecyclerView.ViewHolder {
        private ImageView groupIconIv;
        private TextView groupTitleTv, nameTv, messagetV;

        public HolderGroupChatList(@NonNull View itemView) {
            super(itemView);

            groupIconIv = itemView.findViewById(R.id.profile_iv);
            groupTitleTv = itemView.findViewById(R.id.groupTitle_tv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messagetV = itemView.findViewById(R.id.messageGroupTv);
        }
    }
}
