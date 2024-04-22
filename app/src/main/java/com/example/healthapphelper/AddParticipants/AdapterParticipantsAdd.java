package com.example.healthapphelper.AddParticipants;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapphelper.R;
import com.example.healthapphelper.bottomnav.chats_extended.ModelCroupChat;
import com.example.healthapphelper.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterParticipantsAdd extends RecyclerView.Adapter<AdapterParticipantsAdd.HolderParticipantsAdd>{

    private Context context;
    private ArrayList<User> userArrayList;
private String groupId,myGroupRole;

    public AdapterParticipantsAdd(Context context, ArrayList<User> userArrayList, String groupId, String myGroupRole) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View  view = LayoutInflater.from(context).inflate(R.layout.row_participants_add,parent,false);

        return new HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {
User user=userArrayList.get(position);
String name=user.getUsername();
        String image=user.getProfileImage();
        String uid=user.getUid();

        holder.nameTv.setText(name);
  try{
      Picasso.get().load(image).placeholder(R.drawable.username_icon).into(holder.avatarIv);
  }catch (Exception e){
holder.avatarIv.setImageResource(R.drawable.username_icon);
  }


  checkIfAlreadyExists(user,holder);
    }

    private void checkIfAlreadyExists(User user, HolderParticipantsAdd holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String hisrole=""+snapshot.child("role").getValue();
                        }else{

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class HolderParticipantsAdd extends RecyclerView.ViewHolder{
        private ImageView avatarIv;
        private TextView nameTv;
        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);
            avatarIv=itemView.findViewById(R.id.profile_iv_participants);
            nameTv=itemView.findViewById(R.id.usernameChatTv_participants);
        }
    }
}
