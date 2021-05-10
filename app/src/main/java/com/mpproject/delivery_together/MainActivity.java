package com.mpproject.delivery_together;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private String lifeCycleTag = "Main Activity";
    private String tag = "MainActivity Message";

    private long backKeyPressedTime = 0;
    private Boolean autoLogin;
    private EditText searchedit;

    private RecyclerAdapter adapter;
    private Button writeBtn;
    private Button logoutBtn; //임시 로그아웃 기능하는 버튼(나중에 마이 프로필에 로그아웃 기능 추가)

    private List<String> titleList = new ArrayList<String>();  //게시물 제목
    private List<String> contentList = new ArrayList<String>();  //게시물 내용
    private List<String> keyList = new ArrayList<String>();  //게시물 키값
    private List<String> uidList = new ArrayList<>();  //게시물 작성자 id


    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용

        Intent callIntent=getIntent();
        autoLogin=callIntent.getBooleanExtra("autoLogin",false);

        database = FirebaseDatabase.getInstance().getReference("post");
        firebaseAuth = FirebaseAuth.getInstance();

        database.addValueEventListener(new ValueEventListener() {

            //게시물 데이터베이스 값 변동시 발생
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //리사이클 뷰 초기화
                titleList.clear();
                contentList.clear();
                keyList.clear();
                uidList.clear();

                //****현재 조건에 맞게 게시물 보여주기****

                //database있을때 까지 리사이클 뷰 채우기
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    String str1 = (String) message.child("title").getValue();
                    String str2 = (String) message.child("content").getValue();
                    String str3 = (String) message.child("uid").getValue();
                    String key = message.getKey();
                    keyList.add(key);
                    titleList.add(str1);
                    contentList.add(str2);
                    uidList.add(str3);
                }
                //화면에 출력
                getData();
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //글쓰기 버튼 누르고 화면전환
        //버튼 누르면 write 화면
        writeBtn = (Button) findViewById(R.id.writeBtn);
        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(tag, "Write Button Clicked");  //게시물 작성 버튼 클릭됨

                Intent intent = new Intent(getApplicationContext(), PostWriteActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn=(Button)findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                finish();
            }
        });
        searchedit=(EditText)findViewById(R.id.searchEdit);
        searchedit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                adapter.getFilter().filter(arg0);
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

        //자동 로그인이 체크 되있지 않았다면 종료할 때 로그아웃함
        if(firebaseAuth.getCurrentUser()!=null&&autoLogin==false)
            firebaseAuth.signOut();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            if(autoLogin==false)
                firebaseAuth.signOut();

            finish();
        }
    }

    private void getData() {
        init();

        for (int i = 0; i < titleList.size(); i++) {
            RecyclerItem data = new RecyclerItem();
            data.setTitle(titleList.get(i));
            data.setCont(contentList.get(i));

            adapter.addItem(data);
        }
        Log.d(tag, "Post Reload");  //게시물 리스트 보여주기
    }

    private void init() {
        //초기화 함수
        //리사이클뷰 새로 만들어주기
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        adapter = new RecyclerAdapter();
        //리사이클뷰 클릭이벤트
        //특정 리사이클뷰 클릭시 그 글 post에 들어가고 title content key값 넘김
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                //****추가적인 정보 더 보내기
                Intent intent = new Intent(getApplicationContext(), PostInfoActivity.class);
                Bundle bundle=new Bundle();

                bundle.putString("title", titleList.get(pos));
                bundle.putString("content", contentList.get(pos));
                bundle.putString("key", keyList.get(pos));
                bundle.putString("uid", uidList.get(pos));

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }


}


