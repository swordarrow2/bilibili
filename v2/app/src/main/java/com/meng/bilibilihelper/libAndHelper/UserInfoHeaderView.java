package com.meng.bilibilihelper.libAndHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.meng.bilibilihelper.R;

public class UserInfoHeaderView extends LinearLayout {

    private ImageView imageView;
    private TextView textViewTitle;
    private TextView textViewSummry;

    public UserInfoHeaderView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.main_account_list_header, this);
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewTitle = (TextView) findViewById(R.id.textView1);
        textViewSummry = (TextView) findViewById(R.id.textView2);
    }

    public void setImage(Bitmap b) {
        imageView.setImageBitmap(b);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setTitle(String s) {
        textViewTitle.setText(s);
    }

    public String getTitle() {
        return textViewTitle.getText().toString();
    }

    public void setSummry(String s) {
        textViewSummry.setText(s);
    }

    public void setSummry(int i) {
        setSummry(String.valueOf(i));
    }

    public void setSummry(long l) {
        setSummry(String.valueOf(l));
    }

    public void setSummry(float f) {
        setSummry(String.valueOf(f));
    }

    public String getSummry() {
        return textViewSummry.getText().toString();
    }

}
