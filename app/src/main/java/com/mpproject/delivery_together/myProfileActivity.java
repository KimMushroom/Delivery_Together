package com.mpproject.delivery_together;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.util.HashMap;

public class myProfileActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView nicknameTextView;
    private TextView phoneTextView;
    private TextView mannerTextView;

    private Button logoutBtn;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        nameTextView=(TextView)findViewById(R.id.profileName);
        nicknameTextView=(TextView)findViewById(R.id.profileNickname);
        phoneTextView=(TextView)findViewById(R.id.profilePhone);
        mannerTextView=(TextView)findViewById(R.id.profileMannerScore);

        logoutBtn=(Button)findViewById(R.id.logoutBtn);

        firebaseAuth=FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance().getReference("user_info");

        database.orderByChild("userId").equalTo(firebaseAuth.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                HashMap<String,String> user=(HashMap)snapshot.getValue();

                nameTextView.setText(user.get("userName"));
                nicknameTextView.setText(user.get("userNickname"));
                mannerTextView.setText(user.get("userManner"));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
            }
        });
    }

    private static <K, V> K getKey(HashMap<K, V> map, V value) {

        for (K key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }
}