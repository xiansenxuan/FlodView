package com.xuan.foldview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView list_view;
    private ArrayList<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView list_view=findViewById(R.id.list_view);

        itemList=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            itemList.add(" item  "+i);
        }

        list_view.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return itemList.size();
            }

            @Override
            public Object getItem(int position) {
                return itemList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView= LayoutInflater.from(MainActivity.this).
                        inflate(R.layout.item,parent,false);
                TextView view= (TextView) convertView;
                view.setText(itemList.get(position));
                return convertView;
            }
        });
    }

}
