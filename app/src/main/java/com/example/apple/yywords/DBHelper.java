package com.example.apple.yywords;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "kyword.db";
    public static final String TB_NAME1 = "all_words";
    public static final String TB_NAME2 = "old_words";
    public static final String TB_NAME3 = "done_words";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    public DBHelper(Context context){
        super(context,DB_NAME,null,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,CURNAME TEXT,CURRATE TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME1+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,MEANING TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME2+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,MEANING TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE "+TB_NAME3+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,WORD TEXT,MEANING TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
