package com.example.healthapphelper.bottomnav.chats;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.healthapphelper.databinding.FragmentChatsBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatsFragment extends Fragment{
    private FragmentChatsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
      binding=FragmentChatsBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
}
