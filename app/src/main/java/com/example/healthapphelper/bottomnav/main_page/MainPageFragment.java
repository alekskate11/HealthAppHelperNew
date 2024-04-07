package com.example.healthapphelper.bottomnav.main_page;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthapphelper.databinding.FragmentChatsBinding;
import com.example.healthapphelper.databinding.FragmentMainpageBinding;
import com.example.healthapphelper.databinding.FragmentNewChatBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MainPageFragment extends Fragment {
    private FragmentMainpageBinding binding;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        binding= FragmentMainpageBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
}
