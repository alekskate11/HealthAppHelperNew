package com.example.healthapphelper.chats;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.healthapphelper.message.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Chat {
    private String chat_id,chat_name,userId1,userId2;
    private ArrayList<Message> messages = new ArrayList<>();

    public Chat(String chat, String chat_name, String userId1, String userId2) {
        this.chat_id = chat;
        this.chat_name = chat_name;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }

    public String getChat() {
        return chat_id;
    }

    public void setChat(String chat) {
        this.chat_id = chat;
    }

    public String getChat_name() {
        return chat_name;
    }

    public void setChat_name(String chat_name) {
        this.chat_name = chat_name;
    }

    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }
    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void deleteChat(String chatId, Context context) {
        FirebaseDatabase.getInstance().getReference("Chats").child(chatId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String uid = currentUser.getUid();
                            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("chats");
                            userChatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String chatsStr = snapshot.getValue(String.class);
                                    if (chatsStr != null && !chatsStr.isEmpty()) {
                                        chatsStr = chatsStr.replace(chatId + ",", "");
                                        if (chatsStr.endsWith(",")) {
                                            chatsStr = chatsStr.substring(0, chatsStr.length() - 1);
                                        }
                                        userChatsRef.setValue(chatsStr);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Обработка ошибок
                                }
                            });
                            Toast.makeText(context, "Chat deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete chat", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void removeMessage(Message message) {
        this.messages.remove(message);
    }
}
