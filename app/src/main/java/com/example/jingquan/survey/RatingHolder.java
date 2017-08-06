package com.example.jingquan.survey;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Jing Quan on 2017-08-06.
 */

public class RatingHolder extends RecyclerView.ViewHolder {

    private TextView tv;
    private RatingBar rb;

    public RatingHolder(View v) {
        super(v);
        this.tv = (TextView) v.findViewById(R.id.rating);
        this.rb = (RatingBar) v.findViewById(R.id.ratingBar);
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public RatingBar getRb() {
        return rb;
    }

    public void setRb(RatingBar rb) {
        this.rb = rb;
    }
}
