package com.example.jingquan.survey;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Created by Jing Quan on 2017-08-08.
 */

public class MCQHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    private RadioGroup rg;

    public MCQHolder(View v) {
        super(v);
        this.tv = (TextView) v.findViewById(R.id.mcqtext);
        this.rg = (RadioGroup) v.findViewById(R.id.rg);
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public RadioGroup getRg() {
        return rg;
    }

    public void setRg(RadioGroup rg) {
        this.rg = rg;
    }
}
