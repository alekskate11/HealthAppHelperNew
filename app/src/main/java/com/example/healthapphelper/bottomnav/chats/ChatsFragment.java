package com.example.healthapphelper.bottomnav.chats;


import static android.icu.text.DisplayContext.LENGTH_SHORT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.healthapphelper.chats.Chat;
import com.example.healthapphelper.chats.ChatsAdapter;
import com.example.healthapphelper.databinding.FragmentChatsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatsFragment extends Fragment {
    private FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        loadChats();
        return binding.getRoot();
    }

    private void loadChats() {
        ArrayList<Chat> chats = new ArrayList<>();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot userSnapshot = snapshot.child("Users").child(uid);
                    if (userSnapshot.exists()) {
                        DataSnapshot chatsSnapshot = userSnapshot.child("chats");
                        if (chatsSnapshot.exists()) {
                            String chatsStr = chatsSnapshot.getValue(String.class);
                            if (chatsStr != null && !chatsStr.isEmpty()) {
                                String[] chatsIds = chatsStr.split(",");
                                for (String chatId : chatsIds) {
                                    DataSnapshot chatSnapshot = snapshot.child("Chats").child(chatId);
                                    if (chatSnapshot.exists()) {
                                        String userId1 = Objects.requireNonNull(chatSnapshot.child("user1").getValue(String.class));
                                        String userId2 = Objects.requireNonNull(chatSnapshot.child("user2").getValue(String.class));
                                        String chatUserId = (uid.equals(userId1)) ? userId2 : userId1;
                                        DataSnapshot chatUserSnapshot = snapshot.child("Users").child(chatUserId);
                                        if (chatUserSnapshot.exists()) {
                                            String chatName = Objects.requireNonNull(chatUserSnapshot.child("username").getValue(String.class));
                                            Chat chat = new Chat(chatId, chatName, userId1, userId2);
                                            chats.add(chat);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.chatsRv.setAdapter(new ChatsAdapter(chats));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Failed to get user chats", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
