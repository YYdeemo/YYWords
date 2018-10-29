package com.example.apple.yywords;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.UserDictionary;

import java.util.ArrayList;
import java.util.List;

public class WordsManager {
    private DBHelper dbHelper;
    private String TB_NAME1;
    private String TB_NAME2;
    private String TB_NAME3;

    public WordsManager(Context context){
        dbHelper = new DBHelper(context);
        TB_NAME1 = DBHelper.TB_NAME1;
        TB_NAME2 = DBHelper.TB_NAME2;
        TB_NAME3 = DBHelper.TB_NAME3;
    }

    //添加KYWord 到总表 参数：(KYWord kyword)
    public void ADDALLTB1(List<KYWord> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(KYWord word : list){
            if(findbyWord(TB_NAME1, word.getKyword()))
                continue;
            ContentValues values = new ContentValues();
            values.put("word", word.getKyword());
            values.put("meaning", word.getMeaning());
            db.insert(TB_NAME1,null,values);
        }
        db.close();
    }

    //添加KYWords 到复习表 （list）
    public void ADDALLTB2(List<KYWord> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(KYWord word : list){
            if(findbyWord(TB_NAME2, word.getKyword()))
                continue;
            ContentValues values = new ContentValues();
            values.put("word", word.getKyword());
            values.put("meaning", word.getMeaning());
            db.insert(TB_NAME2,null,values);
        }
        db.close();
    }

    //添加KYWords 到已学习表 （list)
    public void ADDALLTB3(List<KYWord> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for(KYWord word : list){
            if(findbyWord(TB_NAME3, word.getKyword()))
                continue;
            ContentValues values = new ContentValues();
            values.put("word", word.getKyword());
            values.put("meaning", word.getMeaning());
            db.insert(TB_NAME3,null,values);
        }
        db.close();
    }

    //从总表中提取出i个单词 并删除 (list)
    public List<KYWord> listALLTB1(int size){
        int i = 1;
        List<KYWord> wordslist = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TB_NAME1,null,null,null,null,null,null);
        if(cursor != null && size<=cursor.getCount()){
            wordslist = new ArrayList<KYWord>();
            int j=1;
            while(cursor.moveToPosition(j) && i<=size){
                j = j+30;
                if(j>=sizeTB1())
                    j=2;
                KYWord word = new KYWord();
                word.setKyword(cursor.getString(cursor.getColumnIndex("WORD")));
                word.setMeaning(cursor.getString(cursor.getColumnIndex("MEANING")));
                word.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                word.setIsok(true);

                wordslist.add(word);

                i++;
            }
            cursor.close();
        }
        db.close();
        return wordslist;
    }
    //从总表中删除
    public void deleteTB1(List<KYWord> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (KYWord word : list) {
            String wordname = word.getKyword();
            db.delete(TB_NAME1, "word=?", new String[]{wordname});
        }
        db.close();
    }

    //从复习表中提取 i个单词 (list)
    public List<KYWord> listALLTB2(int size){
        int i = 1;
        List<KYWord> wordslist = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TB_NAME2,null,null,null,null,null,null);
        if(cursor != null && size <=cursor.getCount()){
            wordslist = new ArrayList<KYWord>();
            while(cursor.moveToNext()&& i<=size){
                KYWord word = new KYWord();
                word.setKyword(cursor.getString(cursor.getColumnIndex("WORD")));
                word.setMeaning(cursor.getString(cursor.getColumnIndex("MEANING")));
                word.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                word.setIsok(true);

                wordslist.add(word);
                i++;
            }
            cursor.close();
        }
        db.close();
        return wordslist;
    }

    //从复习表中删除
    public void deleteTB2(List<KYWord> list){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (KYWord word : list) {
            String wordname = word.getKyword();
            db.delete(TB_NAME2, "word=?", new String[]{wordname});
        }
        db.close();
    }

    //从已学习表中提取listwords
    public List<KYWord> listALLTB3(int size){
        int i = 1;
        List<KYWord> wordslist = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TB_NAME3,null,null,null,null,null,null);
        if(cursor != null && size <= cursor.getCount()){
            wordslist = new ArrayList<KYWord>();
            while(cursor.moveToNext()&& i<=size){
                KYWord word = new KYWord();
                word.setKyword(cursor.getString(cursor.getColumnIndex("WORD")));
                word.setMeaning(cursor.getString(cursor.getColumnIndex("MEANING")));
                word.setId(cursor.getInt(cursor.getColumnIndex("ID")));
                word.setIsok(true);

                wordslist.add(word);
                i++;
            }
            cursor.close();
        }
        db.close();
        return wordslist;
    }

    //从已学习表中删除
    public void deleteTB3(KYWord word){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String wordstring = word.getKyword();
        db.delete(TB_NAME3, "word=?", new String[]{wordstring});
        db.close();
    }

    public void deleteALLTB3(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TB_NAME3,null,null);
        db.close();
    }

    //从总表中添加一个单词 从已学习表中删除的
    public void ADDTB3(KYWord word){
        if(!findbyWord(TB_NAME3, word.getKyword())) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("word", word.getKyword());
            values.put("meaning", word.getMeaning());
            db.insert(TB_NAME3, null, values);
            db.close();
        }
    }

    //从总表中添加一个单词 从已学习表中删除的
    public void ADDTB1(KYWord word){
        if(!findbyWord(TB_NAME1, word.getKyword())) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("word", word.getKyword());
            values.put("meaning", word.getMeaning());
            db.insert(TB_NAME1, null, values);
            db.close();
        }
    }

    //返回数量
    public int sizeTB1(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TB_NAME1,null,null,null,null,null,null);
        int size = cursor.getCount();
        cursor.close();
        db.close();
        return size;
    }
    public int sizeTB2(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TB_NAME2,null,null,null,null,null,null);
        int size = cursor.getCount();
        cursor.close();
        db.close();
        return size;
    }
    public int sizeTB3(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TB_NAME3,null,null,null,null,null,null);
        int size = cursor.getCount();
        cursor.close();
        db.close();
        return size;
    }

    public void deleteALLTB1(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TB_NAME1,null,null);
        db.close();
    }

    public void deleteALLTB2(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TB_NAME2,null,null);
        db.close();
    }

    public boolean findbyWord(String tb_name,String wordn){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(tb_name,null,"word=?",new String[]{wordn},null,null,null);
        if(cursor != null && cursor.moveToFirst()){
            cursor.close();
            return true;
        }else{
            return false;
        }

    }



}
