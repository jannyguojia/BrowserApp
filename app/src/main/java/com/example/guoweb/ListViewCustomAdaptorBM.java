package com.example.guoweb;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class ListViewCustomAdaptorBM extends BaseAdapter {
    private List<History> itemList;
    private LayoutInflater inflater;
    private OnClickInMyAdapterListener  myActivityInterface;
    public Activity context;

    public ListViewCustomAdaptorBM(Activity context, List<History> itemList, OnClickInMyAdapterListener  myActivityInterface) {
        super();
        this.myActivityInterface= myActivityInterface;
        this.itemList = itemList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return itemList.size();
    }

    public Object getItem(int position) {
        return itemList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.bookmark_row, null);
            holder.textViewurl = (TextView) convertView.findViewById(R.id.textViewurl);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        History history = itemList.get(position);
        holder.textViewurl.setText(history.getTitle());
        holder.textViewTime.setText(history.getUrl());
        Button remvbtn= (Button)convertView.findViewById(R.id.remvBM);

        remvbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                Log.d("his", "rembtnBM"+position);
                myActivityInterface.onremovebookmarkclicked(position);


            }
        });
        return convertView;
    }

    public static class ViewHolder {
        TextView textViewurl;
        TextView textViewTime;

    }

}


