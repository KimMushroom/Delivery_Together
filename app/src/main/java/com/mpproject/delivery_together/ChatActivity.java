package com.mpproject.delivery_together;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
//채팅하기 누르면 채팅방으로 참여
//현재 나중에 들어오는 사람도 이전 채팅 내역을 볼 수 있음
//채팅방에 들어왔을 때부터의 채팅 내역만 보이게 수정

public class ChatActivity extends AppCompatActivity {

    private String lifeCycleTag = "Chat Activity";
    private String tag = "ChatActivity Message";

    private String myId;
    private String chatTitle;
    private String chatKey;

    private EditText myMessageEditText;
    private ListView messageListView;

    private ArrayList<MessageItem> messageItemList;  //닉네임, 시간, 메세지가 담긴 객체
    private  ChatAdapter adapter;  //채팅목록을 관리하는 adapter

    //Firebase Database 관리 객체참조변수
    private FirebaseAuth firebaseAuth;
    private DatabaseReference chatRef;

    private boolean join=false;
    private String ss="false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용

        Intent callIntent = getIntent();
        chatKey=callIntent.getExtras().getString("key");
        chatTitle=callIntent.getExtras().getString("title");
        myId=callIntent.getExtras().getString("uid");

        //메세지 목록 객체
        messageItemList =new ArrayList<>();

        //채팅방 제목을 닉네임으로(=chatTitle)
        getSupportActionBar().setTitle(chatTitle);

        myMessageEditText =findViewById(R.id.et);
        messageListView =findViewById(R.id.lv);

        //Firebase DB관리 객체와 'chat'노드 참조객체 얻어오기
        chatRef= FirebaseDatabase.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();


        adapter = new ChatAdapter(messageItemList,getLayoutInflater());
        messageListView.setAdapter(adapter);


        //firebaseDB에서 채팅 메세지들 실시간 읽어오기
        //'chat'노드의 child노드 중 내가 입장한 방의 데이터가 들어있는 곳에서 데이터들을 읽어오기
        chatRef.child("chat").child("room"+chatKey).addChildEventListener(new ChildEventListener() {
            //database에 뭔가 하나라도 변경되면 작동하는 event handler
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);

                //새로운 메세지를 리스트뷰에 추가하기 위해 ArrayList에 추가
                messageItemList.add(messageItem);
                Log.d("Tag", String.valueOf(messageItemList.size()));  // life cycle 확인용
                //채팅목록 리스트뷰를 갱신
                adapter.notifyDataSetChanged();
                messageListView.setSelection(messageItemList.size()-1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    public void clickSend(View view) {
        //firebase DB에 저장할 값들( 닉네임, 메세지, 시간)
        String nickName= myId;
        String message= myMessageEditText.getText().toString();

        //메세지 작성 시간 문자열로
        Calendar calendar= Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        //firebase DB에 저장할 값(MessageItem객체) 설정
        MessageItem messageItem= new MessageItem(nickName,message,time);

        //'chat'노드의 child노드에 MessageItem객체를 통해 추가
        chatRef.child("chat").child("room"+chatKey).push().setValue(messageItem);

        //EditText에 있는 글씨 지우기
        myMessageEditText.setText("");

        //입력하면 키패드가 내려가도록 설정
        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
}