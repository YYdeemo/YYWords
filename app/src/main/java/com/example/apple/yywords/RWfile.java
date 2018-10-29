package com.example.apple.yywords;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RWfile {

    public static ArrayList<String> getWords(){
    //public static void main(String args[]){
        String filename = "/Users/apple/Desktop/kywords.txt";
        //File file = new File(filename);
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            File file = new File(filename);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file),"GBK");
            BufferedReader bf = new BufferedReader(inputReader);
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
        for(String s : arrayList){
            i++;
            if(s.split("\t").length<4||i==1) {
                arrayList.remove(s);
                continue;
            }
//            System.out.println("line:"+i+" words:"+s);
            System.out.println("word:"+s.split("\t")[1]);
            System.out.println("meaning:"+s.split("\t")[3]);
            if(i>10)
                break;
        }

        return arrayList;
    }

    public List<HashMap<String,String>> StringToMap(ArrayList<String> arrayList){
        List<HashMap<String,String>> words_list = new ArrayList<HashMap<String, String>>();
        HashMap<String,String> map = new HashMap<String, String>();
        for(String s : arrayList){
            map.put("word",s.split(" ")[0]);
            System.out.println("word"+s.split(" ")[0]);
            map.put("meaning",s.split(" ")[2]);
            System.out.println("meaning"+s.split(" ")[2]);
            words_list.add(map);
        }
        return words_list;
    }

    public List<KYWord> getlistwords(){
        ArrayList<String> arrayList = getWords();
        List<KYWord> list = new ArrayList<KYWord>();
        for(String s : arrayList){
            KYWord word = new KYWord();
            word.setKyword(s.split("\t")[1]);
            word.setMeaning(s.split("\t")[3]);
            word.setIsok(true);
            list.add(word);
        }

        return list;
    }

}
