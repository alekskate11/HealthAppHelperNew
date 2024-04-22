package com.example.healthapphelper.bottomnav.chats_extended;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.healthapphelper.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {
private String groupId;

private ImageView groupIconIv;
private ImageButton attachBtn,sendBtn;
private TextView groupTitleTv;
private EditText messegeEt;
private FirebaseAuth firebaseAuth;
private RecyclerView chatRv;
private ImageButton back_btn;
private ArrayList<ModelCroupChat> groupChatList;
private AdapterChat adapterGroupChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
attachBtn=findViewById(R.id.attachBtn);
back_btn=findViewById(R.id.back_from_create_chat_btn);
        groupIconIv = findViewById(R.id.groupNew_iv);
        messegeEt=findViewById(R.id.messageEt);
        sendBtn=findViewById(R.id.sendBtn);
        groupTitleTv = findViewById(R.id.textViewGroup);
        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");
        chatRv=findViewById(R.id.chatRv);
        chatRv.setLayoutManager(new LinearLayoutManager(this));
        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupInfo();
        loadGroupMessages();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=messegeEt
                        .getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this,"Cannot send empty message....",Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
            }
        });


    }

    private void loadGroupMessages() {
        groupChatList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCroupChat model = ds.getValue(ModelCroupChat.class);
                    groupChatList.add(model);
                }
                adapterGroupChat=new AdapterChat(GroupChatActivity.this,groupChatList);
                chatRv.setAdapter(adapterGroupChat);
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
     }

    private void sendMessage(String message) {
        String timestamp=""+System.currentTimeMillis();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",""+firebaseAuth.getUid());
        hashMap.put("message",""+message);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("type",""+"text"); //text//image//file
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");

        ref.child(groupId).child("Messages").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
messegeEt.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupChatActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId) // Исправлено
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupTitle = snapshot.child("groupTitle").getValue(String.class);
                        String groupDescription = snapshot.child("groupDescription").getValue(String.class);
                        String groupIcon = snapshot.child("groupIcon").getValue(String.class);

                        groupTitleTv.setText(groupTitle);
                        try {
                            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_white).into(groupIconIv);
                        } catch (Exception e) {
                            groupIconIv.setImageResource(R.drawable.ic_group_white);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}