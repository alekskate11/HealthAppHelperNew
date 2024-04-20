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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.healthapphelper.LoginActivity;
import com.example.healthapphelper.R;
import com.example.healthapphelper.RegisterActivity;
import com.example.healthapphelper.ResetPasswordActivity;
import com.example.healthapphelper.databinding.FragmentChatsBinding;
import com.example.healthapphelper.databinding.FragmentNewChatBinding;
import com.example.healthapphelper.databinding.FragmentYourChatsBinding;
import com.example.healthapphelper.users.User;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class YourChatsFragment extends Fragment {

    private FragmentYourChatsBinding binding;
    private ImageButton btn_add_user;
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentYourChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();


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
                        .replace(R.id.fragment_container, addParticipantsToGroupFragment)  // R.id.fragment_container - это ID вашего контейнера фрагментов
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
