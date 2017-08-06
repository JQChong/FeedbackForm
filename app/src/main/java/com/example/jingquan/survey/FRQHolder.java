package com.example.jingquan.survey;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Jing Quan on 2017-08-06.
 */

public class FRQHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    private EditText et;

    public FRQHolder(View v) {
        super(v);
        this.tv = (TextView) v.findViewById(R.id.frqtext);
        this.et = (EditText) v.findViewById(R.id.edit2);
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public EditText getEt() {
        return et;
    }

    public void setEt(EditText et) {
        this.et = et;
    }
}
