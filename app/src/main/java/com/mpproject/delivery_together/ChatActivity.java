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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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

    private ArrayList<MessageItem> messageItemList;  //comment(닉네임, 시간, 메세지, 읽은 유저), users(채팅방 참여 유저)가 담긴 객체
    private ArrayList<String> chatUserList; //채팅방 유저 리스트
    private HashMap<String, Boolean> user;
    private ChatAdapter adapter;  //채팅목록을 관리하는 adapter

    //Firebase Database 관리 객체참조변수
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;


    private int peopleCnt = 0; //읽음표시 확인용
    private String mapKey;
    private HashMap<String, Boolean> readUsers = new HashMap<String, Boolean>();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용

        Intent callIntent = getIntent();
        chatKey = callIntent.getExtras().getString("key");
        chatTitle = callIntent.getExtras().getString("title");
        myId = callIntent.getExtras().getString("uid");

        //메세지 목록 객체
        messageItemList = new ArrayList<MessageItem>();

        //채팅방 제목을 닉네임으로(=chatTitle)
        getSupportActionBar().setTitle(chatTitle);

        myMessageEditText = findViewById(R.id.et);
        messageListView = findViewById(R.id.lv);

        //Firebase DB관리 객체와 'chat'노드 참조객체 얻어오기
        ref = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        adapter = new ChatAdapter(messageItemList, getLayoutInflater());
        messageListView.setAdapter(adapter);

        //유저리스트 목록 객체
        chatUserList = new ArrayList<String>();


        //채팅방에 처음 입장했을 때
        //1. chatUserList에 user 저장, 방에 총 몇 명인지 peopleCnt에 저장
        //2-1.if (chatUserList에 내가 없다면) 처음 입장 -> 00님이 입장하셨습니다
        //2-2.else comment-user-myId == true 인 애들을 다 불러오기
        ref.child("chat").child("room" + chatKey).child("comment").addChildEventListener(new ChildEventListener() {
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
                messageListView.setSelection(messageItemList.size() - 1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
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

        ref.child("chat").child("room" + chatKey).child("users").addValueEventListener(new ValueEventListener() {
            HashMap<String, Boolean> temp;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    temp = (HashMap<String, Boolean>) ds.getValue();

                }
                for (String key : temp.keySet()) {
                    chatUserList.add(key);
                }

                //2-1.chatUserList에 내가 없다면 처음 입장 -> 00님이 입장하셨습니다
                if (!chatUserList.contains(myId)) {
                    //node에 myId추가
                    ref.child("roomUser").child("room" + chatKey).child("users").push().setValue(myId);
                    chatUserList.add(myId);
                    peopleCnt = chatUserList.size();
                    //******00님이 입장하셨습니다 추가하기*******
                } else {
                    Log.d("있음 시발", "else");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        ref.child("roomUser").child("room" + chatKey).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                messageItemList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MessageItem messageItem = ds.getValue(MessageItem.class);
//                    readUsers.put(messageItem.readUsers);
                    messageItemList.add(messageItem);
                }
                adapter.notifyDataSetChanged();
                messageListView.setSelection(messageItemList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

//    //메세지 가져오기
//    public void getMessageList() {
//
//        ref.child("roomUser").child("room" + chatKey).child("comment").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
//                messageItemList.clear();
//
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    MessageItem messageItem = ds.getValue(MessageItem.class);
//                    //readUsers.put(messageItem.readUsers);
//                    messageItemList.add(messageItem);
//                }
//                adapter.notifyDataSetChanged();
//                messageListView.setSelection(messageItemList.size() - 1);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//
//        });
//    }

    //chatUserList에 user 저장, 방에 총 몇 명인지 peopleCnt에 저장
//    public void countRoomUser()
//    {
//        //채팅방에 있는 유저리스트를 chatUserList에 저장
//        ref.child("chat").child("room" + chatKey).child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String user = ds.getValue((String.class));
//                    chatUserList.add(user);
//                }
//                peopleCnt = chatUserList.size();
//            }
//
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

    public void clickSend(View view) {
        //firebase DB에 저장할 값들( 닉네임, 메세지, 시간)
        String nickName = myId;
        String message = myMessageEditText.getText().toString();


        //메세지 작성 시간 문자열로
        Calendar calendar = Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);

        //readUsers에
        for (int i = 0; i < chatUserList.size(); i++) {
            readUsers.put(chatUserList.get(i), false);
        }
        //내가 보낸 거니까 일단 나는 바로 읽지 ㅇㅈ?
        readUsers.put(myId, true);

        // 전체에서 일단 내가 본 거만 빼서 넘겨 default
        int read = peopleCnt - readUsers.size();

        //firebase DB에 저장할 값(MessageItem객체) 설정
        MessageItem messageItem = new MessageItem(nickName, message, time, read, readUsers);

        //'chat'노드의 child노드에 MessageItem객체를 통해 추가
        ref.child("chat").child("room" + chatKey).child("comment").push().setValue(messageItem);

        //EditText에 있는 글씨 지우기
        myMessageEditText.setText("");

        //입력하면 키패드가 내려가도록 설정
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}