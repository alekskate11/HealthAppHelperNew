package com.example.healthapphelper.bottomnav.new_chat;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthapphelper.databinding.FragmentChatsBinding;
import com.example.healthapphelper.databinding.FragmentNewChatBinding;
import com.example.healthapphelper.users.User;
import com.example.healthapphelper.users.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding= FragmentNewChatBinding.inflate(inflater,container,false);
loadUsers();
        return binding.getRoot();
    }

    private void loadUsers(){
        ArrayList<User> users = new ArrayList<User>();
        FirebaseDatabase.getInstance().getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()){
                    if (userSnapshot.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        continue;
                    }

                    String uid = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue().toString();
                    String profileImage = userSnapshot.child("profileImage").getValue().toString();

                    users.add(new User(uid, username, profileImage));
                }

                binding.usersRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.usersRv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                binding.usersRv.setAdapter(new UsersAdapter(users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
