package com.example.healthapphelper;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.healthapphelper.bottomnav.chats.ChatsFragment;

import com.example.healthapphelper.bottomnav.chats_extended.GroupCreateActivity;
import com.example.healthapphelper.bottomnav.main_page.MainPageFragment;
import com.example.healthapphelper.bottomnav.new_chat.NewChatFragment;
import com.example.healthapphelper.bottomnav.chats_extended.YourChatsFragment;
import com.example.healthapphelper.bottomnav.profile.ProfileFragment;
import com.example.healthapphelper.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

            // Initialize default fragment
        loadFragment(new ChatsFragment());

        binding.bottomNav.setSelectedItemId(R.id.chats); // This should be your default selected item, e.g., home_page

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if (item.getItemId() == R.id.new_chat) {
                fragment = new NewChatFragment();
            } else if (item.getItemId() == R.id.home_page) {
                fragment = new MainPageFragment();
            } else if (item.getItemId() == R.id.profile) {
                fragment = new ProfileFragment();
            } else if (item.getItemId() == R.id.chats) {
                fragment = new ChatsFragment();
            } else if (item.getItemId() == R.id.chats_extended) { // This should now refer to YourChatsFragment
                fragment = new YourChatsFragment();
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(binding.fragmentContainer.getId(), fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void Status(String status){
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(userId);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        FirebaseDatabase.getInstance().getReference().updateChildren(hashMap);


    }

}