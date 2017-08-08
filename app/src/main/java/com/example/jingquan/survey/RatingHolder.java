package com.example.jingquan.survey;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Jing Quan on 2017-08-06.
 */

public class RatingHolder extends RecyclerView.ViewHolder {

    private TextView tv;
    private SeekBar sb;
    private EditText et;

    public RatingHolder(View v) {
        super(v);
        this.tv = (TextView) v.findViewById(R.id.rating);
        this.sb = (SeekBar) v.findViewById(R.id.seekBar);
        this.et = (EditText) v.findViewById(R.id.seekBarEdit);
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public SeekBar getSb() {
        return sb;
    }

    public void setSb(SeekBar sb) {
        this.sb = sb;
    }

    public EditText getEt() {
        return et;
    }

    public void setEt(EditText et) {
        this.et = et;
    }
}
