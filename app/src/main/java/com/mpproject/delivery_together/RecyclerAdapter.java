package com.mpproject.delivery_together;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable{
    //item list
    private ArrayList<RecyclerItem> postList = new ArrayList();
    private ArrayList<RecyclerItem> searchList = new ArrayList();
    private int index;

    public void switchLayout(int position) {
    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }


    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    //layoutinflater : recycler_item을 inflate시켜주는거
    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    //item을 하나하나 보여주는(=bind) 함수
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(searchList.get(position));
    }

    //recyclerView의 총 개수
    @Override
    public int getItemCount() {
        return searchList.size();
    }

    //item을 추가했을 때 쓰는 함수
    void addItem(RecyclerItem data)
    {
        postList.add(data);
        searchList.add(data);
    }

    //subView 세팅해주는거
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;

        ViewHolder (View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.postTitleTextView);
            content = itemView.findViewById(R.id.postContentTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    RecyclerItem itematposition=new RecyclerItem();
                    itematposition= searchList.get(pos);
                    int position= postList.indexOf(itematposition);

                    if(pos != RecyclerView.NO_POSITION){
                        if(mListener==null)
                        {
                            System.out.println("NUll");
                        }
                        if(mListener!=null) {
                            mListener.onItemClick(v, position);
                        }
                    }
                }
            });

        }
        void onBind(RecyclerItem data) {
            title.setText(data.getTitle());
            content.setText(data.getCont());

        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                index=0;
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    searchList = postList;
                } else {
                    ArrayList<RecyclerItem> filteringList = new ArrayList<>();
                    for(RecyclerItem potion : postList) {
                        String name = potion.getTitle();
                        index++;
                        System.out.println("name"+name);
                        if(name.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(potion);
                        }
                    }
                    searchList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = searchList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                searchList = (ArrayList<RecyclerItem>)results.values;
                notifyDataSetChanged();
            }
        };
    }

}