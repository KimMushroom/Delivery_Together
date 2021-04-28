package com.mpproject.delivery_together;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostWriteActivity extends AppCompatActivity {

    private String lifeCycleTag ="PostWrite Activity";
    private String tag ="PostWriteActivity Message";

    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    private String uid;
    private String inputTitle;
    private String inputContent;
    private String inputLocation;
    private Integer inputMaxPeople;
    private Integer inputGender;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용

        //완료 버튼을 누르면
        Button writeBtn = findViewById(R.id.completeBtn);

        database = FirebaseDatabase.getInstance().getReference().child("post");
        firebaseAuth=FirebaseAuth.getInstance();


        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Write Button Clicked");  // 게시물 등록 버튼 눌림
                //게시글의 정보를 가져온다
                EditText titleEditText = (EditText) findViewById(R.id.writeTitle);
                EditText contentEditText = (EditText) findViewById(R.id.writeContent);

                //작성할 게시물 정보 가져옴
                uid=firebaseAuth.getUid();
                inputTitle = titleEditText.getText().toString();
                inputContent = contentEditText.getText().toString();
                inputLocation="Gachon";  //임의값
                inputMaxPeople=4;  //임의값
                inputGender=0;  //임의값

                //정보 HashMap 형태로 저장
                HashMap<String,String>newPost=new HashMap<>();
                newPost.put("uid",uid);
                newPost.put("title",inputTitle);
                newPost.put("content",inputContent);
                newPost.put("location",inputLocation);
                newPost.put("maxPeople",inputMaxPeople.toString());
                newPost.put("gender",inputGender.toString());

                //데이터베이스에 HashMap값 추가
                database.push().setValue(newPost)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(tag, "Write Success");  // 게시물 등록 성공

                                Toast.makeText(getApplicationContext(), "게시물을 등록했습니다", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(tag, "Write Failed");  // 게시물 등록 실패

                                Toast.makeText(getApplicationContext(), "게시물 등록 실패", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
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
}
