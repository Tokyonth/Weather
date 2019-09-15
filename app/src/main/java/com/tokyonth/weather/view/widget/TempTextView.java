package com.tokyonth.weather.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class TempTextView extends androidx.appcompat.widget.AppCompatTextView {

    public TempTextView(Context context) {
        super(context);
        init(context);
    }

    public TempTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TempTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Temp.ttf");
        this.setTypeface(typeface);
    }


}
