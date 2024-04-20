package com.example.healthapphelper.bottomnav.new_chat;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.healthapphelper.ChatActivity;
import com.example.healthapphelper.chats.Chat;
import com.example.healthapphelper.databinding.FragmentNewChatBinding;
import com.example.healthapphelper.users.User;
import com.example.healthapphelper.users.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    private SearchView searchView;
    private UsersAdapter adapter;
    private ArrayList<User> users = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        loadUsers();
        super.onCreate(savedInstanceState);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<User> filteredUsers = filter(users, newText);
                adapter.updateList(filteredUsers);

                return true;
            }
        });
    }

    private ArrayList<User> filter(ArrayList<User> users, String query) {
        ArrayList<User> filteredUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private void loadUsers() {
        users.clear();

        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String uid = userSnapshot.getKey();

                    if (uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        continue;
                    }

                    String username = userSnapshot.child("username").getValue(String.class);
                    String profileImage = userSnapshot.child("profileImage").getValue(String.class);
                    String status = userSnapshot.child("status").getValue(String.class);
                    if (username != null && profileImage != null) {
                        users.add(new User(uid, username, profileImage, status));
                    }
                }


                adapter = new UsersAdapter(users, NewChatFragment.this::createChat);
                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createChat(User user) {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String chatName = user.getUsername();

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .orderByChild("user1")
                .equalTo(currentUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean chatExists = false;

                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String user2 = chatSnapshot.child("user2").getValue(String.class);
                            if (user2 != null && user2.equals(user.getUid())) {
                                // Чат уже существует между пользователями, показать сообщение и завершить метод
                                Toast.makeText(getContext(), "Chat with this user already exists", Toast.LENGTH_SHORT).show();
                                chatExists = true;
                                break;
                            }
                        }

                        if (!chatExists) {
                            // Проверяем существование чата с пользователем в качестве user2 и текущим пользователем в качестве user1
                            FirebaseDatabase.getInstance().getReference().child("Chats")
                                    .orderByChild("user1")
                                    .equalTo(user.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            boolean reverseChatExists = false;

                                            for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                                                String user2 = chatSnapshot.child("user2").getValue(String.class);
                                                if (user2 != null && user2.equals(currentUserUid)) {
                                                    // Чат уже существует между пользователями в обратном порядке, показать сообщение и завершить метод
                                                    Toast.makeText(getContext(), "Chat with this user already exists", Toast.LENGTH_SHORT).show();
                                                    reverseChatExists = true;
                                                    break;
                                                }
                                            }

                                            if (!reverseChatExists) {
                                                // Чат еще не существует, создать новый
                                                String chatId = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();
                                                Chat chat = new Chat(chatId, chatName, currentUserUid, user.getUid());

                                                HashMap<String, Object> chatMap = new HashMap<>();
                                                chatMap.put("user1", currentUserUid);
                                                chatMap.put("user2", user.getUid());

                                                FirebaseDatabase.getInstance().getReference().child("Chats").child(chatId).setValue(chatMap)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                updateChatsForUser(currentUserUid, chatId);
                                                                updateChatsForUser(user.getUid(), chatId);

                                                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                                                intent.putExtra("chatId", chatId);
                                                                intent.putExtra("username", user.getUsername());
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(getContext(), "Failed to create chat", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(getContext(), "Failed to check chat existence", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to check chat existence", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateChatsForUser(String userId, String chatId) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatsStr = snapshot.getValue(String.class);

                if (chatsStr != null && !chatsStr.isEmpty()) {
                    chatsStr += "," + chatId;
                } else {
                    chatsStr = chatId;
                }

                FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("chats").setValue(chatsStr)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Failed to update user chats", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to update user chats", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
