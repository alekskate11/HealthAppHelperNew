package com.example.healthapphelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import com.example.healthapphelper.AddParticipants.AdapterParticipantsAdd;
import com.example.healthapphelper.users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupParticipantsActivity extends AppCompatActivity {

    private RecyclerView usersRv;

    private FirebaseAuth firebaseAuth;
    private String groupId;
    private String myGroupRole;
    private ArrayList<User> users;
    private AdapterParticipantsAdd adapterParticipantsAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants);



        firebaseAuth = FirebaseAuth.getInstance();
        usersRv = findViewById(R.id.usersRvParticipants);
        groupId = getIntent().getStringExtra("groupId");



        loadGroupInfo();
        getAllUsers();
    }

    private void getAllUsers() {
        users = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                adapterParticipantsAdd = new AdapterParticipantsAdd(GroupParticipantsActivity.this, users, "" + groupId, "" + myGroupRole);
                usersRv.setLayoutManager(new LinearLayoutManager(GroupParticipantsActivity.this));
                usersRv.setAdapter(adapterParticipantsAdd);
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String uid = ds.getKey();  // Получаем UID пользователя
                    String username = "" + ds.child("username").getValue();
                    String profileImage = "" + ds.child("profileImage").getValue();

                    User user = new User();
                    user.setUid(uid);
                    user.setUsername(username);
                    user.setProfileImage(profileImage);

                    if(!firebaseAuth.getUid().equals(user.getUid())){
                        users.add(user);
                    }
                }
                adapterParticipantsAdd = new AdapterParticipantsAdd(GroupParticipantsActivity.this, users, "" + groupId, "" + myGroupRole);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок
            }
        });
    }
    private void loadGroupInfo() {
        DatabaseReference ref1= FirebaseDatabase.getInstance().getReference("Groups");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String groupId=""+ds.child("groupId").getValue();
                    String groupTitle=""+ds.child("groupTitle").getValue();
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String createdBy=""+ds.child("createdBy").getValue();


                    ref1.child(groupId).child("Participants").child(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        myGroupRole=""+snapshot.child("role").getValue();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}