package com.example.healthapphelper.bottomnav.chats_extended;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthapphelper.LoginActivity;
import com.example.healthapphelper.R;
import com.example.healthapphelper.RegisterActivity;
import com.example.healthapphelper.ResetPasswordActivity;
import com.example.healthapphelper.chats.CahtViewHolder;
import com.example.healthapphelper.databinding.FragmentChatsBinding;
import com.example.healthapphelper.databinding.FragmentNewChatBinding;
import com.example.healthapphelper.databinding.FragmentYourChatsBinding;
import com.example.healthapphelper.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YourChatsFragment extends Fragment {

    private FragmentYourChatsBinding binding;
    private ImageButton btn_add_user;
    private RecyclerView groupsRv;
private FirebaseAuth firebaseAuth;
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentYourChatsBinding.inflate(inflater, container, false);
        groupsRv = binding.chatsRv;
        groupsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<ModelGroupChatList> groupChatLists = new ArrayList<>();
        AdapterGroupChat adapter = new AdapterGroupChat(getContext(), groupChatLists);
        groupsRv.setAdapter(adapter);
        loadGroupChats();
        return binding.getRoot();


    }
    private ArrayList<ModelGroupChatList> groupChatLists = new ArrayList<>();
    private void loadGroupChats() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Groups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<ModelGroupChatList> groupChatLists = new ArrayList<>();

                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String groupId = chatSnapshot.getKey();


                            if (groupId.equals(currentUserId)) {
                                continue;
                            }

                            String groupTitle = chatSnapshot.child("groupTitle").getValue(String.class);
                            String groupDescription = chatSnapshot.child("groupDescription").getValue(String.class);
                            String groupIcon = chatSnapshot.child("groupIcon").getValue(String.class);
                            String timestamp = chatSnapshot.child("timestamp").getValue(String.class);
                            String createdBy = chatSnapshot.child("createdBy").getValue(String.class);

                            // Создаем новый объект ModelGroupChatList и добавляем в список
                            ModelGroupChatList chat = new ModelGroupChatList(groupId, groupTitle, groupDescription, groupIcon, timestamp, createdBy);
                            groupChatLists.add(chat);
                        }

                        // Создаем и устанавливаем адаптер
                        AdapterGroupChat adapter = new AdapterGroupChat(getContext(), groupChatLists);
                        groupsRv.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("YourChatsFragment", "Error loading group chats", error.toException());
                        Toast.makeText(getContext(), "Failed to load group chats", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddParticipantsToGroup addParticipantsToGroupFragment = new AddParticipantsToGroup();

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, addParticipantsToGroupFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.btnCreareChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GroupCreateActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
