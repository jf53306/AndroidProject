package com.example.jf.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jf.nestscrolling.NestScrollParentView;
import com.example.jf.nestscrolling.RefreshingView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jf on 17-3-13.
 */

public class ActivityNestedScrolling extends AppCompatActivity{
    RefreshingView viewList;
    private List<String> dd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nestedscroll);

        dd=new ArrayList<>();
        for (int i=0;i<20;i++){
            dd.add("eee");
        }

        viewList= (RefreshingView) this.findViewById(R.id.view_list_view);
        viewList.getListView().setAdapter(new AdapterList(dd));



        viewList.setOnRefreshListener(new NestScrollParentView.OnRefreshListener() {
            @Override
            public void onTopRefresh(final NestScrollParentView view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      view.finishRefresh();
                    }
                },1000);
            }

            @Override
            public void onBotRefresh(final NestScrollParentView view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      view.finishRefresh();
                    }
                },1000);
            }
        });
    }




    class AdapterList extends RecyclerView.Adapter{
        private List<String> datas;
        public AdapterList(List<String> datas){
            this.datas=datas;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(LayoutInflater.from(ActivityNestedScrolling.this).inflate(R.layout.item_nested_scrolling,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return datas.size();
        }


        class MyHolder extends RecyclerView.ViewHolder{

            public MyHolder(View itemView) {
                super(itemView);
            }
        }
    }
}

