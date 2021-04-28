package com.mpproject.delivery_together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostInfoActivity extends AppCompatActivity {

    private String lifeCycleTag = "PostInfo Activity";
    private String tag = "PostInfoActivity Message";

    private String postTitle;
    private String postContent;
    private String postKey;
    private String postUid;

    private TextView titleTextView;
    private TextView contentTextView;
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
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용

        // 파이어베이스 인증, 데이터베이스 연동
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        //아까 메인 화면에서 보내준 정보 받기
        Intent intent = getIntent();

        //****추가적인 정보 더 받기
        postTitle = intent.getExtras().getString("title");
        postContent = intent.getExtras().getString("content");
        postKey = intent.getExtras().getString("key");
        postUid = intent.getExtras().getString("uid");

        //화면에 제목, 내용 보여주기
        titleTextView = (TextView) findViewById(R.id.posttitle);
        titleTextView.setText(postTitle);
        contentTextView = (TextView) findViewById(R.id.postcontent);
        contentTextView.setText(postContent);

        backBtn = (Button) findViewById(R.id.postback);
        deleteBtn = (Button) findViewById(R.id.postdt);
        chatBtn = (Button) findViewById(R.id.chat);

        //게시물 정보를 보는 사람이 작성자라면 삭제 버튼 활성화
        if (postUid.equals(firebaseAuth.getUid())) {
            deleteBtn.setEnabled(true);
            Log.d(tag, "User = Writer, Button Enable");  //사용자와 작성자가 같아 삭제 권한 부여
        }


        //뒤로 버튼 뒤로
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Back Button Clicked");  //뒤로 가기 버튼 눌림
                finish();
            }
        });

        //게시물 삭제 버튼
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Delete Button Clicked");  //게시물 삭제 버튼 눌림
                deletePost(postKey);
            }
        });

        //채팅 입장 버튼
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Chat Button Clicked");  //채팅방 입장 버튼 눌림

                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                Bundle bundle=new Bundle();

                //채팅방 이름(게시물 key값), 사용자id 보냄
                bundle.putString("key",postKey);
                bundle.putString("title",postTitle);
                bundle.putString("uid",firebaseAuth.getUid());

                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(lifeCycleTag, "In the onStart() event");  // life cycle 확인용
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(lifeCycleTag, "In the onRestart() event");  // life cycle 확인용
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(lifeCycleTag, "In the onResume() event");  // life cycle 확인용
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(lifeCycleTag, "In the onPause() event");  // life cycle 확인용
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(lifeCycleTag, "In the onStop() event");  // life cycle 확인용
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(lifeCycleTag, "In the onDestroy() event");  // life cycle 확인용
    }

    private void deletePost(String key) {
        //database 데이터 지우기
        database.child("post").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(tag, "Delete Success");  //게시물 삭제 성공

                    Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
                    finish();
                    //enter your code what you want excute after remove value in firebase.
                } else {
                    Log.d(tag, "Delete Failed");  //게시물 삭제 실패

                    Toast.makeText(getApplicationContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}