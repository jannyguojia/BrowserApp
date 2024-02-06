package com.example.guoweb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewCustomAdaptor extends BaseAdapter {
    private List<History> itemList;
    private LayoutInflater inflater;

    public ListViewCustomAdaptor(Activity context, List<History> itemList) {
        super();
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
            convertView = inflater.inflate(R.layout.history_row, null);
            holder.textViewurl = (TextView) convertView.findViewById(R.id.textViewurl);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        History history = itemList.get(position);
        holder.textViewurl.setText(history.getUrl());
        holder.textViewTime.setText(history.getTime());
        return convertView;
    }

    public static class ViewHolder {
        TextView textViewurl;
        TextView textViewTime;

    }
}

