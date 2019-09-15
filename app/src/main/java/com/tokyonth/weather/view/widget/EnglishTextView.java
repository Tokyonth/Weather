package com.tokyonth.weather.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.tokyonth.weather.view.FontHelper;

public class EnglishTextView extends androidx.appcompat.widget.AppCompatTextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public EnglishTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyFontStyle(context,attrs);
    }

    public EnglishTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyFontStyle(context,attrs);
    }

    private void applyFontStyle(Context context , AttributeSet attrs){
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        Typeface tf = selectTypeface(context,textStyle);
        setTypeface(tf);
    }

    private Typeface selectTypeface(Context context, int textStyle) {

        switch (textStyle) {
            case Typeface.BOLD: // bold
                return FontHelper.getTypeface("fonts/DINPro_Medium.otf", context);

            case Typeface.NORMAL: // regular
            default:
                return FontHelper.getTypeface("fonts/DINPro_Regular.otf", context);
        }
    }

}
