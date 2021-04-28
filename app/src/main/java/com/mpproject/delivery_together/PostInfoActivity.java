package com.mpproject.delivery_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostInfoActivity extends AppCompatActivity {

    private String postTitle;
    private String postContent;
    private String postKey;
    private String postUid;

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView keyTextView;
    private Button backBtn;
    private Button deleteBtn;
    private Button chatBtn;

    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //게시글 화면
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        // 파이어베이스 인증, 데이터베이스 연동
        firebaseAuth=FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        //아까 메인 화면에서 보내준 titlel content key 받기
        Intent intent = getIntent();
        postTitle=intent.getStringExtra("title");
        postContent=intent.getStringExtra("content");
        postKey=intent.getStringExtra("key");
        postUid=intent.getStringExtra("uid");

        titleTextView = (TextView)findViewById(R.id.posttitle);
        titleTextView.setText(postTitle);
        contentTextView =(TextView)findViewById(R.id.postcontent);
        contentTextView.setText(postContent);

        backBtn =(Button)findViewById(R.id.postback);
        deleteBtn =(Button)findViewById(R.id.postdt);
        chatBtn =(Button)findViewById(R.id.chat);

        //게시물 정보를 보는 사람이 작성자라면 삭제 버튼 활성화
        if(postUid.equals(firebaseAuth.getUid()))
            deleteBtn.setEnabled(true);

        //뒤로 버튼 뒤로
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //key값을 이용해 지우기
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletevalue(postKey);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private void deletevalue(String key)
    {
        //database 데이터 지우기
        database.child("post").child(key).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "게시물 삭제 성공", Toast.LENGTH_SHORT).show();
                            finish();
                            //enter your code what you want excute after remove value in firebase.
                        } else {
                            //enter msg or enter your code which you want to show in case of value is not remove properly or removed failed.

                            Toast.makeText(getApplicationContext(), "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}