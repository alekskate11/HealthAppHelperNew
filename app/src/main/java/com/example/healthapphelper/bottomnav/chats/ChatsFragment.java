package com.example.healthapphelper.bottomnav.chats;


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

    private void loadChats(){
      ArrayList<Chat> chats=new ArrayList<>();
      chats.add(new Chat("2121","dfkdf","smsdls","sdsdks"));
        binding.chatsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatsRv.setAdapter(new ChatsAdapter(chats));
    }
}