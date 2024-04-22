package com.example.healthapphelper.bottomnav.chats_extended;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapphelper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.HolderGroupChat> {

    private static final int MSG_TYPE_LEFT=0;
    private static final int MSG_TYPE_right=1;

    private Context context;
    private ArrayList<ModelCroupChat> modelGroupChatLists;
    private FirebaseAuth firebaseAuth;
    public AdapterChat(Context context, ArrayList<ModelCroupChat> modelCroupChats){
        this.context=context;
        this.modelGroupChatLists=modelCroupChats;
        firebaseAuth=FirebaseAuth.getInstance();

    }


    @NonNull
        @Override
public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        if(viewType==MSG_TYPE_right){
View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right,parent,false);
return  new HolderGroupChat(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left,parent,false);
            return  new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        ModelCroupChat model = modelGroupChatLists.get(position);
        String tempstamp=model.getTimestamp();
        String message = model.getMessage();
        String sendrUid = model.getSender();

        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(tempstamp));


        setUserName(model, holder);
    }

    private void setUserName(ModelCroupChat model, HolderGroupChat holder){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds:snapshot.getChildren()){
                            String name=""+ds.child("name").getValue();
                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AdapterChat", "Error fetching username", error.toException());
                    }
                });
    }

    @Override
    public int getItemViewType(int position) {
    if(modelGroupChatLists.get(position).getSender().equals(firebaseAuth.getUid())){
        return MSG_TYPE_right;
    }else{
        return MSG_TYPE_LEFT;
    }
    }
    public int getItemCount() {
        return modelGroupChatLists.size();
    }
    class HolderGroupChat extends RecyclerView.ViewHolder{
private TextView nameTv,messageTv,timeTv;

        public HolderGroupChat(@NonNull View itemView){
            super(itemView);
            nameTv=itemView.findViewById(R.id.nameTv);
            messageTv=itemView.findViewById(R.id.messageTv);
            timeTv=itemView.findViewById(R.id.timeTv);

        }
    }

}
