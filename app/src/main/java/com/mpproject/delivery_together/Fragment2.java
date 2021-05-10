package com.mpproject.delivery_together;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Fragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_chatList);

        return view;
    }

    class ChatRecylcerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private String uid;
        private ArrayList<ChatRoomItem> ChatRoomItem = new ArrayList<ChatRoomItem>();

        public ChatRecylcerViewAdapter(){
            uid = FirebaseAuth.getInstance()
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

    }
}
