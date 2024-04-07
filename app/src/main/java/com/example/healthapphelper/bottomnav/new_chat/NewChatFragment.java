package com.example.healthapphelper.bottomnav.new_chat;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthapphelper.databinding.FragmentChatsBinding;
import com.example.healthapphelper.databinding.FragmentNewChatBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding binding;
    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding= FragmentNewChatBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
}
