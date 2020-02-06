package com.powerdata.barcode.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.powerdata.barcode.R;

public class TitledCardView extends CardView {

    private String mSubtitle;
    private String mTitle;
    private TextView mSubtitleTextView;
    private TextView mTitleTextView;

    public TitledCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_titled_card, this);

        mTitleTextView = findViewById(R.id.title_text_view);
        mSubtitleTextView = findViewById(R.id.subtitle_text_view);

        int[] sets = {R.attr.subtitle, R.attr.title};
        TypedArray a = context.obtainStyledAttributes(attrs, sets);
        try {
            setTitle(a.getString(R.styleable.TitledCardView_title));
            setSubtitle(a.getString(R.styleable.TitledCardView_subtitle));
        } finally {
            a.recycle();
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        System.out.println("title:" + title);
        this.mTitle = title;
        mTitleTextView.setText(title);
        invalidate();
        requestLayout();
    }

    public String getSubtitle() {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle) {
        System.out.println("subtitle:" + subtitle);
        this.mSubtitle = subtitle;
        mSubtitleTextView.setText(subtitle);
        invalidate();
        requestLayout();
    }

}
