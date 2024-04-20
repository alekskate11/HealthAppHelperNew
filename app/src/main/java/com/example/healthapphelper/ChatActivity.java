package com.example.healthapphelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.format.DateFormat;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthapphelper.chats.Chat;
import com.example.healthapphelper.databinding.ActivityChatBinding;
import com.example.healthapphelper.message.Message;
import com.example.healthapphelper.message.MessageAdapter;
import com.example.healthapphelper.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private String chatId;
    DatabaseReference userRefForSeen;
    ValueEventListener seenListener;
TextView user_role_status_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
user_role_status_tv=findViewById(R.id.user_role_status_tv);
        chatId = getIntent().getStringExtra("chatId");

        String username = getIntent().getStringExtra("username");

        userRefForSeen = FirebaseDatabase.getInstance().getReference().child("Users").child(chatId);
        if (chatId == null || username == null) {
            Toast.makeText(this, "Chat information is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.usernameChatTv.setText(username);


        FirebaseDatabase.getInstance().getReference().child("Users").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String onlineStatus = dataSnapshot.child("OnlineStatus").getValue(String.class);
                    if (onlineStatus != null) {
                        if ("online".equals(onlineStatus)) {
                            user_role_status_tv.setText(onlineStatus);
                        } else {
                            long time = Long.parseLong(onlineStatus);
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(time);
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm a", cal).toString();
                            user_role_status_tv.setText("Last seen at: " + dateTime);
                        }
                    } else {
                        Toast.makeText(ChatActivity.this, "OnlineStatus is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "User data doesn't exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });


        loadMessages(chatId);

        binding.messageBtn.setOnClickListener(v -> {
            String message = binding.messageEt.getText().toString();
            if (message.isEmpty()) {
                Toast.makeText(this, "Message field cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            String date = simpleDateFormat.format(new Date());
            binding.messageEt.setText("");
            sendMessage(chatId, message, date);
            loadMessages(chatId);
        });


        binding.backFromChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.btnDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(v);
            }
        });
    }
    public void onDeleteClick(View view) {

        if (chatId != null) {
            deleteChatFromDatabase(chatId);


            Intent resultIntent = new Intent();
            resultIntent.putExtra("deletedChatId", chatId);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
    private void deleteChatFromDatabase(String chatId) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId);

        // Удаление чата из "Chats" узла
        chatRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Удаление чата из списков чатов пользователей

                        // Получаем текущего пользователя
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();

                            // Удаление чата из "chats" списка пользователя
                            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("chats");
                            userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String chatsStr = snapshot.getValue(String.class);
                                        if (chatsStr != null && !chatsStr.isEmpty()) {
                                            String[] chatsIds = chatsStr.split(",");
                                            StringBuilder newChatsStr = new StringBuilder();
                                            for (String id : chatsIds) {
                                                if (!id.equals(chatId)) {
                                                    newChatsStr.append(id).append(",");
                                                }
                                            }
                                            if (newChatsStr.length() > 0) {
                                                newChatsStr.setLength(newChatsStr.length() - 1);  // Remove the last comma
                                            }
                                            snapshot.getRef().setValue(newChatsStr.toString());
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(ChatActivity.this, "Failed to delete chat from user's chats list", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Failed to delete chat", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendMessage(String chatId, String message, String date) {
        if (chatId == null) return;
        HashMap<String, String> messageInfo = new HashMap<>();
        messageInfo.put("text", message);
        messageInfo.put("ownerId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        messageInfo.put("date", date);
        FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId)
                .child("messages").push().setValue(messageInfo);
    }

    private void loadMessages(String chatId) {
        if (chatId == null) return;

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) return;

                        List<Message> messages = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            String messageId = messageSnapshot.getKey();
                            String ownerId = messageSnapshot.child("ownerId").getValue() != null ? messageSnapshot.child("ownerId").getValue().toString() : "";
                            String text = messageSnapshot.child("text").getValue() != null ? messageSnapshot.child("text").getValue().toString() : "";
                            String date = messageSnapshot.child("date").getValue() != null ? messageSnapshot.child("date").getValue().toString() : "";

                            messages.add(new Message(messageId, ownerId, text, date));
                        }

                        binding.messageRv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        binding.messageRv.setAdapter(new MessageAdapter(messages));
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue() instanceof Map) {
                                    Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                                    String status = (String) data.get("status");
                                    if (status != null) {
                                        updateStatusUI(status); // Обновляем UI
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Обработка ошибки
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибки
                    }
                });
    }


    private void updateLastSeen() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(chatId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("OnlineStatus", timeStamp);
        dbRef.updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully updated last seen time
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Failed to update last seen", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkOnlineStatus(String status) {
        if (chatId != null) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(chatId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("OnlineStatus", status);
            dbRef.updateChildren(hashMap);
        }
    }
    private void updateStatusUI(String status) {
        if ("online".equals(status)) {
            binding.statusOnline.setVisibility(View.VISIBLE);
            binding.statusOffline.setVisibility(View.GONE);
        } else {
            binding.statusOnline.setVisibility(View.GONE);
            binding.statusOffline.setVisibility(View.VISIBLE);
        }
    }


    private void updateStatus(String status) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("OnlineStatus", status);
        userRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("online");
        checkOnlineStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isChangingConfigurations()) {
            updateStatus("offline");
            updateLastSeen();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateStatus("offline");
    }

    @Override
    protected void onStart() {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        updateStatus("online");
        super.onStart();
    }

    private void Status(String status) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("OnlineStatus", status);
        userRef.updateChildren(hashMap);
    }
}