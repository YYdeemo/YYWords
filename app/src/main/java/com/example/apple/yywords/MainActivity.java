package com.example.apple.yywords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
首页：
1.可输入今日学习单词数
2.通过监听输入，计算出其中首次学习的单词数量和复习得单词数量
3.通过button可以进入到学习页面
4.学习内容是根据输入的学习单词数从数据库中提取单词，其中以复习得单词优先，若复习单词全部学习完毕则加入新单词
5.复习页面是已完成学习的单词，可复习。

容错：
1.输入的单词数最少是10个？？？？？？？？
2.若当日已学习了x个单词，则再次修改输入时不能小于x个（仅限当日）在点击学习button时弹窗提醒 此时新旧单词数都为0
3.当再次增加学习数量时，从数据库中提取的单词数应该是这次提交的数量减去上次提交的数量
4.当没有完后任何单词的学习时 在复习页面没有任何数据 所以点击复习button时 弹窗提醒完成学习
 */

public class MainActivity extends AppCompatActivity {

    private EditText inputnum;
    private TextView newnum;
    private TextView oldnum;

    private static final String TAG = "MainActivity***********";

    private String today;

    private static WordsManager manager;

    private SharedPreferences sharedPreferences;

    private String TD_input;
    private String TD_Action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = new WordsManager(MainActivity.this);

        sharedPreferences = getSharedPreferences("myword", Context.MODE_PRIVATE);
//判断是否是第一次使用该APP 如果是 需要加载数据库
        isfisrtUse();

//      更新时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = df.format(new Date());

//        Test();

        inputnum = (EditText) findViewById(R.id.num_input);
        newnum = (TextView) findViewById(R.id.newnum);
        oldnum = (TextView) findViewById(R.id.oldnum);

        TD_input = today+"input";
        int inputtext = sharedPreferences.getInt(TD_input,0);

        TD_Action = today+"Action";

        inputnum.setText(""+inputtext);

        scalwordsnum();

        //输入学习量 计算新旧单词数量
        inputnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>0){
                    calwordsnum(editable);
                }

            }
        });


    }

    private boolean isoverflaw(int input){
        int MAXnum = manager.sizeTB1()+manager.sizeTB2();

        if(input>5400||input>MAXnum){
            return true;
        }else{
            return false;
        }

    }

    private void isfisrtUse(){
        String first = sharedPreferences.getString("FirstDay",null);
//        first = null;
        if(first==null) {
            Log.i(TAG, "onCreate: this is fisrt day");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            first = df.format(new Date());
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putString("FirstDay",first);
            editor1.commit();
            //第一次打开APP时导入词汇
            List<KYWord> allwords;
            allwords = getWords(MainActivity.this);
            Log.i(TAG, "onCreate: allwords length"+allwords.size());
            manager.ADDALLTB1(allwords);
        }else{
            Log.i(TAG, "onCreate: Firstday is "+first);
        }
    }

    //计算需要新学习的数量和需要复习得数量
    //分两种情况 1.今天第一次提交学习计划  2.今天非第一次提交学习计划 （2.1想增加 2.2想少学）
    private void calwordsnum(Editable editable){

        int input_learn_number = Integer.parseInt(editable.toString());
        int today_dnot_learn_num = getTDnotlearnNum();
        int total_dnot_learn_num = manager.sizeTB2();
        int today_input_num = getInputsize();
        Log.i(TAG, "calwordsnum: input_learn_number="+input_learn_number);
        Log.i(TAG, "calwordsnum: today_dnot_learn_num="+today_dnot_learn_num);
        Log.i(TAG, "calwordsnum: total_dnot_learn_num="+total_dnot_learn_num);
        Log.i(TAG, "calwordsnum: today_input_num="+today_input_num);
        Log.i(TAG, "calwordsnum: getTDAction"+getTDisfirst());
        if(getTDisfirst()){
            if(input_learn_number>=total_dnot_learn_num){
                oldnum.setText(String.valueOf(total_dnot_learn_num));
                newnum.setText(String.valueOf(input_learn_number-total_dnot_learn_num));
            }else{
                newnum.setText("0");
                oldnum.setText(String.valueOf(input_learn_number));
            }
        }else{
            if(input_learn_number<=today_input_num-today_dnot_learn_num){
                oldnum.setText("0");
                newnum.setText("0");
                //现在输入的学习数量-（上次输入的数量-上次提交后未学习完的 ）> 学习表大小
            }else if(input_learn_number-today_input_num+today_dnot_learn_num>total_dnot_learn_num){
                oldnum.setText(String.valueOf(total_dnot_learn_num));
                newnum.setText(String.valueOf(input_learn_number-today_input_num+today_dnot_learn_num-total_dnot_learn_num));
            }else{
                oldnum.setText(String.valueOf(input_learn_number-today_input_num+today_dnot_learn_num));
                newnum.setText("0");
            }
        }


    }
//从文件中获取今天还有多少单词未学习 返回整数
    private int getTDnotlearnNum(){
        int not_learn_num = sharedPreferences.getInt(today,0);
        return not_learn_num;
    }
    //从文件中获取今天是否是第一次修改学习数量
    private boolean getTDisfirst(){
        Boolean TDAction = sharedPreferences.getBoolean(TD_Action,true);
        return TDAction;
    }
    //从文件中获取今天上次写入的学习量
    private int getInputsize(){
        String todayinput = today+"input";
        int today_input_num = sharedPreferences.getInt(todayinput,0);
        return today_input_num;
    }

    public void newWindow(){
        //弹出对话框 你今天已学习完 若想学习更多 请输入多的数量
        Log.i(TAG, "inputstudynum: error input, please input a ");
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        bb.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        bb.setMessage(" 你输入的数量，今日已学习完！若想继续学习，请输入更大的数");
        bb.setTitle("!!!");
        bb.show();
    }

    public void newWindow2(){
        //弹出对话框 你今天已学习完 若想学习更多 请输入多的数量
        Log.i(TAG, "inputstudynum: error input, please input a ");
        AlertDialog.Builder bb = new AlertDialog.Builder(this);
        bb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        bb.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        bb.setMessage(" 你输入的数量已经超过了单词总数，请输入更小的数值");
        bb.setTitle("!!!");
        bb.show();
    }

    //study button 事件
    public void ClickStudy(View view){
        int today_should_learn_num = Integer.parseInt(newnum.getText().toString())
                +Integer.parseInt(oldnum.getText().toString());
        //判断今天学习总数是否上溢出
        if(isoverflaw(today_should_learn_num)){
            newWindow2();
        }else {
            //判断是否今日还需学习的数量小于0
            if (today_should_learn_num <= 0) {
                newWindow();
            } else {
                putinfo(today_should_learn_num);

                Intent intent = new Intent(this, StudyActivity.class);
//            intent.putExtra("today", today_should_learn_num);
                startActivity(intent);
                Log.i(TAG, "ClickStudy: Send listwords to studyActivity");
            }
        }

    }

    private void putinfo(int today_should_learn_num){
        //点击时 首先将输入的今日学习数量 写入文件
        Log.i(TAG, "ClickStudy: send today in file is " + today_should_learn_num);
        inserttodayNum(today_should_learn_num);
        insertInputNum();
        insertTDAction();
        //将相应数量的新单词从TB1提交到TB2中
        int newnumwords = Integer.parseInt(newnum.getText().toString());
        List<KYWord> wordList = manager.listALLTB1(newnumwords);
        if (!wordList.isEmpty()) {
            //从TB1中删除加入到TB2中的单词
            Log.i(TAG, "ClickStudy: should delet list size is " + wordList.size());
            Log.i(TAG, "ClickStudy: befor the TB1 size is "+manager.sizeTB1());
            manager.deleteTB1(wordList);
            manager.ADDALLTB2(wordList);
            Log.i(TAG, "ClickStudy: after enter the study page now the TB2 size is " + manager.sizeTB2());
            Log.i(TAG, "ClickStudy: now the TB1 size is "+manager.sizeTB1());
        }
    }

    //读取文件中的单词及含义
    public static List<KYWord> getWords(Context context){

        String filename = "/Users/apple/Desktop/kywords.txt";
        List<String> arrayList = new ArrayList<>();
        try {
            InputStream inputReader = context.getResources().getAssets().open("data/kywords.txt");
            //InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file),"GBK");
            BufferedReader bf = new BufferedReader(new InputStreamReader(inputReader));
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                arrayList.add(str);
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int i = 0;
//        for(String s : arrayList){
//            i++;
//            if(s.split("\t").length<4||i==1) {
//                arrayList.remove(s);
//                continue;
//            }
////            System.out.println("line:"+i+" words:"+s);
//            System.out.println("word:"+s.split("\t")[1]);
//            System.out.println("meaning:"+s.split("\t")[3]);
//            if(i>10)
//                break;
//        }
        List<KYWord> list = new ArrayList<KYWord>();
        for(String s : arrayList){
            i++;
            if(s.split("\t").length<4||i==1) {
                //arrayList.remove(s);
                continue;
            }
            KYWord word = new KYWord();
            word.setKyword(s.split("\t")[1]);
            word.setMeaning(s.split("\t")[3]);
            word.setIsok(true);
            list.add(word);
        }

        return list;

    }

    public void ClickReview(View view){
        int today_should_learn_num = Integer.parseInt(newnum.getText().toString())
                +Integer.parseInt(oldnum.getText().toString());
        putinfo(today_should_learn_num);
        //跳转到Review 页面
        Intent intent2 = new Intent(this,ReviewActivity.class);
        startActivity(intent2);

    }

    //点击button时 必须提交当时输入的学习数量
    private void insertInputNum(){
        int todayInput = Integer.parseInt(inputnum.getText().toString());
        String todayinput = today+"input";
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(todayinput,todayInput);
        editor.commit();
    }
    //点击button 必须提交今天已完成第一次修改 所以下一次 一定是今天非第一次提交
    private void insertTDAction(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TD_Action,false);
        editor.commit();
    }
    //在进入StudyActivity之前将需要学习的数量传给Study
    private void inserttodayNum(int num){
        Log.i(TAG, "inserttodayNum:  now input into file today is "+num);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(today,num);
        editor.commit();
    }


    private void scalwordsnum(){

        int input_learn_number = Integer.parseInt(inputnum.getText().toString());
        int today_dnot_learn_num = getTDnotlearnNum();
        int total_dnot_learn_num = manager.sizeTB2();

        int today_input_num = getInputsize();
        Log.i(TAG, "calwordsnum: input_learn_number="+input_learn_number);
        Log.i(TAG, "calwordsnum: today_dnot_learn_num="+today_dnot_learn_num);
        Log.i(TAG, "calwordsnum: total_dnot_learn_num="+total_dnot_learn_num);
        Log.i(TAG, "calwordsnum: today_input_num="+today_input_num);
        Log.i(TAG, "calwordsnum: getTDAction"+getTDisfirst());
        if(getTDisfirst()){
            if(input_learn_number>=total_dnot_learn_num){
                oldnum.setText(String.valueOf(total_dnot_learn_num));
                newnum.setText(String.valueOf(input_learn_number-total_dnot_learn_num));
            }else{
                newnum.setText("0");
                oldnum.setText(String.valueOf(input_learn_number));
            }
        }else{
            if(input_learn_number<=today_input_num-today_dnot_learn_num){
                oldnum.setText("0");
                newnum.setText("0");
                //现在输入的学习数量-（上次输入的数量-上次提交后未学习完的 ）> 学习表大小
            }else if(input_learn_number-today_input_num+today_dnot_learn_num>total_dnot_learn_num){
                oldnum.setText(String.valueOf(total_dnot_learn_num));
                newnum.setText(String.valueOf(input_learn_number-today_input_num+today_dnot_learn_num-total_dnot_learn_num));
            }else{
                oldnum.setText(String.valueOf(input_learn_number-today_input_num+today_dnot_learn_num));
                newnum.setText("0");
            }
        }


    }

    public void Test(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(TD_Action,true);
        editor.putInt(today,0);
        editor.putInt(TD_input,0);
        editor.commit();
        manager.ADDALLTB1(manager.listALLTB3(manager.sizeTB3()));
        manager.ADDALLTB1(manager.listALLTB2(manager.sizeTB2()));
        manager.deleteALLTB2();
        manager.deleteALLTB3();
    }

}
