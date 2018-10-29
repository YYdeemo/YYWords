package com.example.apple.yywords;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    public List<KYWord> wordList;
    public MyAdapter myAdapter;
    public WordsManager manager;

    private String today;

    public TextView total_num;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);



        //更新时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = df.format(new Date());

//        调取已学习表中的单词
        manager = new WordsManager(ReviewActivity.this);
        int size = manager.sizeTB3();
        //wrong
        wordList = manager.listALLTB3(size);
//        判断是否为空，为空则跳出窗口
        boolean isempty = isEmpty(wordList);

        total_num = (TextView) findViewById(R.id.total_num);
        total_num.setText(String.valueOf(manager.sizeTB3()));

        if(!isempty){
//        所有单词以Item形式展示
            myAdapter = new MyAdapter(ReviewActivity.this,R.layout.activity_review,wordList);
            ListView view = (ListView) findViewById(R.id.list_view);
            view.setAdapter(myAdapter);
//          单点时间 显示中文含义
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                    KYWord word = wordList.get(position);
                    TextView mean_view = (TextView) view.findViewById(R.id.meaning_review);
                    if(mean_view.getVisibility()==View.VISIBLE) {
                        mean_view.setVisibility(View.INVISIBLE);
                    }else{
                        mean_view.setVisibility(View.VISIBLE);
                    }
                }
            });
//          长按事件，将其加入未背单词表中
            view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int position, long l) {
                    AlertDialog.Builder bb = new AlertDialog.Builder(ReviewActivity.this);
                    bb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            KYWord word = wordList.get(position);
                            wordList.remove(position);
                            manager.deleteTB3(word);
                            manager.ADDTB1(word);
                            myAdapter.notifyDataSetChanged();
                        }
                    });
                    bb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    bb.setMessage(" 确定从复习表中删除 重新记忆吗");
                    bb.setTitle("!!!");
                    bb.show();

                    return true;
                }
            });
        }

    }



    public boolean isEmpty(List<KYWord> kyWordList){
        if(kyWordList==null){
            //弹出对话框 没有需要复习的单词 快去学习吧
            AlertDialog.Builder bb = new AlertDialog.Builder(this);
            bb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent1 = new Intent(ReviewActivity.this,MainActivity.class);
                    startActivity(intent1);
                }
            });
            bb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent1 = new Intent(ReviewActivity.this,MainActivity.class);
                    startActivity(intent1);
                }
            });

            bb.setMessage(" 没有需要复习的单词 快去学习吧!");
            bb.setTitle("!!!");
            bb.show();

            return true;
        }else {
            return false;
        }
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.resert,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    //实现返回首页

    public void returnHome_review(View  view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    public void returnStudy(View view){
        //需要判断今天是否有需要学习的单词
        int today_num = getTDnotlearnNum();
        if(today_num<=0){
            newWindow();
        }else {
            //如果没有需要进入Home页面
            Intent intent = new Intent(this, StudyActivity.class);
            startActivity(intent);
        }
    }

    //从文件中获取今天还有多少单词未学习 返回整数
    private int getTDnotlearnNum(){
        SharedPreferences sharedPreferences = getSharedPreferences("myword",Context.MODE_PRIVATE);
        int not_learn_num = sharedPreferences.getInt(today,0);
        return not_learn_num;
    }

    public void newWindow(){
        //弹出对话框 没有需要复习的单词 快去学习吧
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent1 = new Intent(ReviewActivity.this,MainActivity.class);
                startActivity(intent1);
            }
        });
        bb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        bb.setMessage(" 没有需要学习的单词 快去首页制定学习任务吧!");
        bb.setTitle("!!!");
        bb.show();

    }



}
