package com.example.apple.yywords;

import java.io.Serializable;

public class KYWord implements Serializable{

    public int id;
    public String kyword;
    public String meaning;
    public boolean isok;

    public KYWord(){
        super();
        kyword = "";
        meaning = "";
        isok = true;

    }

    public KYWord(int id, String kyword, String meaning, boolean isok){
        super();
        this.kyword = kyword;
        this.meaning = meaning;
        this.isok = isok;


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKyword() {
        return kyword;
    }

    public void setKyword(String kyword) {
        this.kyword = kyword;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean isIsok() {
        return isok;
    }

    public void setIsok(boolean isok) {
        this.isok = isok;
    }

}
