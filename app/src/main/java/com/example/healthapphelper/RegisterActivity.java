package com.example.healthapphelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.healthapphelper.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        TextView signUpTextView = findViewById(R.id.ativity_sign_up_tv);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        TextView forgotPassword=findViewById(R.id.forget_password_tv_registerActivity);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, ResetPasswordActivity.class));
            }
        });
        binding.signupmainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(binding.emailEt.getText().toString().isEmpty() || binding.passwordEt.getText().toString().isEmpty() ||
                        binding.usernameEt.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Fields cannot be empty",Toast.LENGTH_SHORT).show();
                }else{


                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEt.getText().toString(),binding.passwordEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        updateStatus("online");
                                        startActivity(new Intent(RegisterActivity.this, SplashScreen.class));
                                        HashMap<String,String> userInfo=new HashMap<>();
                                        userInfo.put("email",binding.emailEt.getText().toString());
                                        userInfo.put("chats","");
                                        userInfo.put("onlineStatus","offline");
                                        userInfo.put("username",binding.usernameEt.getText().toString());
                                        userInfo.put("profileImage","");
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userInfo);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }, 3000);
                                    }
                                }
                            });

                }
            }
        });
    }
    private void updateStatus(String status) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        userRef.updateChildren(hashMap);
    }
}