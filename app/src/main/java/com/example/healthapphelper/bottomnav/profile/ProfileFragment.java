package com.example.healthapphelper.bottomnav.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.healthapphelper.LoginActivity;
import com.example.healthapphelper.SplashScreen;
import com.example.healthapphelper.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private Uri filePath;

    @Nullable
    @Override
    public View onCreateView(@Nonnull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        loadUserinfo();
        setupProfileImageClickListener();
        setupEditProfileButton();
        setupExitProfileButton();

        return binding.getRoot();
    }

    private void setupProfileImageClickListener() {
        binding.profileImageView.setOnClickListener(v -> SelectedImage());
    }

    private void setupEditProfileButton() {
        binding.editProfileBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
    }

    private void setupExitProfileButton() {
        binding.exitProfileBtn.setOnClickListener(v -> {

            startActivity(new Intent(getContext(), SplashScreen.class));


            new Handler().postDelayed(() -> {

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();

                System.exit(0);
            }, 3000);
        });
    }
    ActivityResultLauncher<Intent> pickImageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    filePath = result.getData().getData();

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                        binding.profileImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadImage();
                }
            }
    );

    private void loadUserinfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String username = snapshot.child("username").getValue(String.class);
                        String profileImage = snapshot.child("profileImage").getValue(String.class);

                        binding.usernameTv.setText(username);

                        if (profileImage != null && !profileImage.isEmpty()) {
                            Glide.with(getContext()).load(profileImage).into(binding.profileImageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
    }

    private void SelectedImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        pickImageActivityResultLauncher.launch(intent);
    }

    private void uploadImage() {
        if (filePath != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseStorage.getInstance().getReference().child("images/" + uid)
                    .putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Photo upload complete", Toast.LENGTH_SHORT).show();

                        FirebaseStorage.getInstance().getReference().child("images/" + uid).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                                            .child("profileImage").setValue(uri.toString());
                                });
                    });
        }
    }
}