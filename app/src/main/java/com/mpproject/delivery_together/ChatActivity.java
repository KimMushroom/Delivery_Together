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
public class ChatActivity extends AppCompatActivity {

    EditText et;
    //채팅목록 리스트뷰
    ListView listView;

    //닉네임, 시간, 메세지가 담긴 객체
    ArrayList<MessageItem> messageItems;
    //채팅목록을 관리하는 adapter
    ChatAdapter adapter;

    //Firebase Database 관리 객체참조변수
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;

    //'chat'노드의 참조객체 참조변수
    DatabaseReference chatRef;
    String chatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        Intent intent = getIntent();

        //post에서 넘겨준 chat_title로 채팅방 제목 설정
        //chatTitle = intent.getExtras().getString("chat_title");
        chatTitle="no";
        messageItems=new ArrayList<>();

        //채팅방 제목을 닉네임으로(=chatTitle)
        getSupportActionBar().setTitle(chatTitle);

        et=findViewById(R.id.et);
        listView=findViewById(R.id.lv);

        adapter = new ChatAdapter(messageItems,getLayoutInflater());
        listView.setAdapter(adapter);

        //Firebase DB관리 객체와 'chat'노드 참조객체 얻어오기
        firebaseDatabase= FirebaseDatabase.getInstance();
        chatRef= firebaseDatabase.getReference();

        firebaseAuth=FirebaseAuth.getInstance();


        //firebaseDB에서 채팅 메세지들 실시간 읽어오기
        //'chat'노드의 child노드 중 내가 입장한 방의 데이터가 들어있는 곳에서 데이터들을 읽어오기
        chatRef.child("chat").child(chatTitle).addChildEventListener(new ChildEventListener() {
            //database에 뭔가 하나라도 변경되면 작동하는 event handler
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //새로 추가된 데이터(값 : MessageItem객체) 가져오기
                MessageItem messageItem = dataSnapshot.getValue(MessageItem.class);
                //메세지가 잘 들어왔는지 확인log
                Log.d("태그", messageItem.message);
                Log.d("태그", messageItem.name);
                //새로운 메세지를 리스트뷰에 추가하기 위해 ArrayList에 추가
                messageItems.add(messageItem);

                //채팅목록 리스트뷰를 갱신
                adapter.notifyDataSetChanged();
                listView.setSelection(messageItems.size()-1); //리스트뷰의 마지막 위치로 스크롤 위치 이동
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

    public void clickSend(View view) {
        //firebase DB에 저장할 값들( 닉네임, 메세지, 시간)
        String nickName= firebaseAuth.getUid();
        String message= et.getText().toString();


        //메세지 작성 시간 문자열로
        Calendar calendar= Calendar.getInstance(); //현재 시간을 가지고 있는 객체
        String time=calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE);

        //firebase DB에 저장할 값(MessageItem객체) 설정
        MessageItem messageItem= new MessageItem(nickName,message,time);

        //'chat'노드의 child노드에 MessageItem객체를 통해 추가
        chatRef.child("chat").child(chatTitle).push().setValue(messageItem);

        //EditText에 있는 글씨 지우기
        et.setText("");

        //입력하면 키패드가 내려가도록 설정
        InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

    }
}