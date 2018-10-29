package com.example.apple.yywords;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<KYWord> {

    private int layoutID;

    public MyAdapter(Context context, int layoutID, List<KYWord> wordlist){
        super(context,layoutID,wordlist);
        this.layoutID = layoutID;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        KYWord word = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(layoutID, parent, false);
        TextView word_view = (TextView) view.findViewById(R.id.word_review);
        TextView meaning_view = (TextView) view.findViewById(R.id.meaning_review);
        word_view.setText(word.getKyword());
        meaning_view.setText(word.getMeaning());

        return view;
    }
}
