package com.example.healthapphelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.example.healthapphelper.databinding.ActivityChatBinding;
import com.example.healthapphelper.message.Message;
import com.example.healthapphelper.message.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String chatId = getIntent().getStringExtra("chatId");
if(chatId==null)return;
        loadMessages(chatId);

        binding.messageBtn.setOnClickListener(v->{
            String message=binding.messageEt.getText().toString();
            if(message.isEmpty()){
                Toast.makeText(this,"Message field cannot be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date=simpleDateFormat.format(new Date());
            binding.messageEt.setText("");
            sendMessage(chatId,message,date);
        });
    }
    private void sendMessage(String chatId,String message,String date){
        if(chatId==null)return;
        HashMap<String,String> messageInfo =new HashMap<>();
        messageInfo.put("text",message);
        messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()));
        messageInfo.put("date",date);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .child("messages").push().setValue(messageInfo);
    }
    private void loadMessages(String chatId){
        if (chatId==null) return;

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        List<Message> messages = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()){
                            String messageId = messageSnapshot.getKey();
                            String ownerId = messageSnapshot.child("ownerId").getValue().toString();
                            String text = messageSnapshot.child("text").getValue().toString();
                            String date = messageSnapshot.child("date").getValue().toString();

                            messages.add(new Message(messageId, ownerId, text, date));
                        }

                        binding.messageRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        binding.messageRv.setAdapter(new MessageAdapter(messages));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}