package com.example.apple.yywords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
学习页面
1.按顺序加载单词
2.轻点屏幕，显示单词含义，及三个button
3.每一个button实现不同的功能
（每个单词首次点击"认识"时，即视为认识，若点击"忘记"则单词在此次之后的第三个单词后出现，
若点击"模糊"则该单词在此次之后的第7个单词后出现，在点击了"忘记"和"模糊"后，必须连续两次点击"认识"
才视为学会了此单词
4.

 */

public class StudyActivity extends AppCompatActivity {

    private static List<KYWord> wordList;
    private static List<KYWord> wordLearned;
    private static KYWord word;
    private static KYWord lastword;

    private TextView word_txt;
    private TextView meaning_txt;
    private Button forget;
    private Button vague;
    private Button learn;
    private static int main_word_num;

    private static final String TAG = "study page*************";

    private String today;

    private static WordsManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        manager = new WordsManager(StudyActivity.this);

        //更新时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = df.format(new Date());
        //获取今天需要学习的数量
        int today_should_learn_num = getTDnotlearnNum();

        wordList = new ArrayList<>();

        wordList = manager.listALLTB2(today_should_learn_num);

        word_txt = (TextView) findViewById(R.id.word_txt);

        wordLearned = new ArrayList<>();

        //显示第i个单词
        if(wordList==null){
            Log.i(TAG, "onCreate: the first the wordlist is empty");
        }else {
            word = wordList.get(0);
            word_txt.setText(word.getKyword());

        }

    }

    //从文件中获取今天还有多少单词未学习 返回整数
    private int getTDnotlearnNum(){
        SharedPreferences sharedPreferences = getSharedPreferences("myword",Context.MODE_PRIVATE);
        int not_learn_num = sharedPreferences.getInt(today,0);
        return not_learn_num;
    }

    //轻点出发事件 显示中文含义 以及显示三个button
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(wordList.isEmpty()){
            hide();
            newwindow_study();
        }else {

            meaning_txt = (TextView) findViewById(R.id.meaning_txt);
            forget = (Button) findViewById(R.id.forget);
            vague = (Button) findViewById(R.id.vague);
            learn = (Button) findViewById(R.id.learn);

            meaning_txt.setVisibility(View.VISIBLE);
            forget.setVisibility(View.VISIBLE);
            vague.setVisibility(View.VISIBLE);
            learn.setVisibility(View.VISIBLE);

            meaning_txt.setText(word.getMeaning());
        }

        return super.onTouchEvent(event);
    }

    //forget事件调动单词位置
    public void ClickForget(View view){
        isdone_study();
        wordList.remove(0);
        word.setIsok(false);
        addtoList(wordList,3,word);
        word = wordList.get(0);
        word_txt.setText(word.getKyword());
        meaning_txt.setText(word.getMeaning());
        hide();
    }

    //vague事件调动单词位置
    public void ClickVague(View view){
        isdone_study();
        wordList.remove(0);
        addtoList(wordList,7,word);
        word = wordList.get(0);
        word_txt.setText(word.getKyword());
        meaning_txt.setText(word.getMeaning());
        hide();
    }

    //learn事件 两种情况 调动单词位置 将单词放入已学习列表中
    public void ClickLearn(View view){
        isdone_study();
        if(word.isIsok()){
            Log.i(TAG, "onCreate: now the word is " + word.getKyword() + " and meaning is " + word.getMeaning());
            wordList.remove(0);
            wordLearned.add(word);
        }else{
            wordList.remove(0);
            word.setIsok(true);
            addtoList(wordList,11,word);
        }
        if(wordList.size()<=0){
            //学习完成 弹出页面
            newwindow_study();

        }else {
            word = wordList.get(0);
            word_txt.setText(word.getKyword());
            meaning_txt.setText(word.getMeaning());
            hide();
        }
    }

    public void isdone_study(){
        if(wordList.size()<1){
            Log.i(TAG, "isdone_study: ");
            manager.deleteALLTB2();
            newwindow_study();
        }
    }
    public void newwindow_study(){
        //学习完成 弹出页面
        hide();
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setPositiveButton("首页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                insertintoTB3();
                insertintoTB2();
                writeinfo();
                //返回首页
                Intent study_intent = new Intent(StudyActivity.this,MainActivity.class);
                startActivity(study_intent);
                Log.i(TAG, "ClickStudy: go to main activity");
            }
        });
        bb.setNegativeButton("复习", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                insertintoTB3();
                insertintoTB2();
                writeinfo();
                //进入复习页面
                Intent study_intent = new Intent(StudyActivity.this,ReviewActivity.class);
                startActivity(study_intent);
                Log.i(TAG, "ClickStudy: go to review activity");

            }
        });

        bb.setMessage("恭喜 完成任务！");
        bb.setTitle("~^-^~");
        bb.show();
    }
    //从TB2和TB1中删除
    public void insertintoTB2(){
        manager.deleteALLTB2();
        manager.ADDALLTB2(wordList);
    }
    //将今天已背单词写入到文件中
    public void writeinfo(){
        int today_num = wordList.size();
        Log.i(TAG, "writeinfo: now today in file is "+today_num);
        SharedPreferences sharedPreferences1 = getSharedPreferences("myword", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.putInt(today,today_num);
        editor1.commit();
    }
    //将单词写入TB3
    public void insertintoTB3(){
        Log.i(TAG, "insertintoTB3: and now the TB3 size is "+manager.sizeTB3());
        manager.ADDALLTB3(wordLearned);
//        Log.i(TAG, "onClick: now the word should add to review page size is "+wordLearned.size()+" and the first word is "+wordLearned.get(0));
        Log.i(TAG, "insertintoTB3: and now the TB3 size is "+manager.sizeTB3());
    }

    //隐藏button和meaning
    public void hide(){
        meaning_txt.setVisibility(View.GONE);
        forget.setVisibility(View.GONE);
        vague.setVisibility(View.GONE);
        learn.setVisibility(View.GONE);
    }
    //插入到List中
    public static void addtoList(List<KYWord> wordList,int index,KYWord word){
        if(wordList.size()<index){
            wordList.add(wordList.size(),word);
        }else{
            wordList.add(index,word);
        }
    }

    //menu处应有返回首页的位置
    //    返回首页应当首先将已背熟的单词加入到TB3中
    //    将剩下的单词加入到TB2中

    public void returnHome(View view){
        insertintoTB2();
        insertintoTB3();
        writeinfo();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void returnReview(View view){
        insertintoTB2();
        insertintoTB3();
        writeinfo();
        Intent intent = new Intent(this,ReviewActivity.class);
        startActivity(intent);
    }


}
