package com.mpproject.delivery_together;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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

import lib.kingja.switchbutton.SwitchMultiButton;


public class Fragment1 extends Fragment {
    private String lifeCycleTag = "Main Activity";
    private String tag = "MainActivity Message";

    private long backKeyPressedTime = 0;

    private RecyclerAdapter adapter;
    private Button writeBtn;

    private List<String> titleList = new ArrayList<String>();  //게시물 제목
    private List<String> contentList = new ArrayList<String>();  //게시물 내용
    private List<String> keyList = new ArrayList<String>();  //게시물 키값
    private List<String> uidList = new ArrayList<>();  //게시물 작성자 id

    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;



    Context context;
    OnTabItemSelectedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);

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
                getData(rootView);
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return rootView;
    }

    //이 프래그먼트가 액티비티 위에 올라갈 때 호출됨 : context객체/listener객체를 참조해서 변수에 할당하는 역할
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(lifeCycleTag, "In the onCreate() event");  // life cycle 확인용
        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }

    }

    private void getData(ViewGroup rootView) {
        init(rootView);

        for (int i = 0; i < titleList.size(); i++) {
            RecyclerItem data = new RecyclerItem();
            data.setTitle(titleList.get(i));
            data.setCont(contentList.get(i));

            adapter.addItem(data);
        }
        Log.d(tag, "Post Reload");  //게시물 리스트 보여주기
    }


    private void init(ViewGroup rootView) {
        //초기화 함수
          /*'내용'과 '사진'중에서 선택했을 때, 어댑터의 switchLayout메서드를 호출하여 레이아웃바꿔줌.
      작성 버튼을 누르면, 두번째 프래그먼트를 띄워줌.*/

        Button WriteBtn = rootView.findViewById(R.id.WriteBtn);
        WriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabSelected(1);
                }
                Intent intent = new Intent(context.getApplicationContext(), PostWriteActivity.class);
                startActivity(intent);
            }
        });

        SwitchMultiButton switchButton = rootView.findViewById(R.id.switchButton);
        switchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                Toast.makeText(getContext(), tabText, Toast.LENGTH_SHORT).show();

                adapter.switchLayout(position);
                adapter.notifyDataSetChanged();
            }
        });

        //리사이클뷰 새로 만들어주기
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        adapter = new RecyclerAdapter();
        //리사이클뷰 클릭이벤트
        //특정 리사이클뷰 클릭시 그 글 post에 들어가고 title content key값 넘김
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(context.getApplicationContext(), PostInfoActivity.class);
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
    //이 프래그먼트가 액티비티에서 내려올 때 호출됨.
    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
        }
    }


    private void initUI(ViewGroup rootView) {
    /*'내용'과 '사진'중에서 선택했을 때, 어댑터의 switchLayout메서드를 호출하여 레이아웃바꿔줌.
      작성 버튼을 누르면, 두번째 프래그먼트를 띄워줌.*/

        Button WriteBtn = rootView.findViewById(R.id.WriteBtn);
        WriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onTabSelected(1);
                }
            }
        });

        SwitchMultiButton switchButton = rootView.findViewById(R.id.switchButton);
        switchButton.setOnSwitchListener(new SwitchMultiButton.OnSwitchListener() {
            @Override
            public void onSwitch(int position, String tabText) {
                Toast.makeText(getContext(), tabText, Toast.LENGTH_SHORT).show();

                adapter.switchLayout(position);
                adapter.notifyDataSetChanged();
            }
        });


    }
}
