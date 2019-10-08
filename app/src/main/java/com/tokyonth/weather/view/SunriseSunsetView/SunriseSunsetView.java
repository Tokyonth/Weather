package com.tokyonth.weather.view.SunriseSunsetView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.tools.SvgResources;
import com.tokyonth.weather.view.SunriseSunsetView.formatter.SimpleSunriseSunsetLabelFormatter;
import com.tokyonth.weather.view.SunriseSunsetView.formatter.SunriseSunsetLabelFormatter;
import com.tokyonth.weather.view.SunriseSunsetView.model.Time;

import java.util.Calendar;
import java.util.Locale;

public class SunriseSunsetView extends View {

    private static final int DEFAULT_TRACK_COLOR = Color.WHITE;
    private static final int DEFAULT_TRACK_WIDTH_PX = 4;
    private static final int DEFAULT_SUN_RADIUS_PX = 20;
    private static final int DEFAULT_LABEL_TEXT_COLOR = Color.WHITE;
    private static final int DEFAULT_LABEL_TEXT_SIZE = 40;
    private static final int DEFAULT_LABEL_VERTICAL_OFFSET_PX = 5;
    private static final int DEFAULT_LABEL_HORIZONTAL_OFFSET_PX = 20;

    /**
     * 当前日出日落比率, mRatio < 0: 未日出, mRatio > 1 已日落
     */

    private static final int MINIMAL_TRACK_RADIUS_PX = 300; // 半圆轨迹最小半径
    private float mTrackRadius; // 轨迹圆的半径
    private float mRatio;
    private int mTrackColor = DEFAULT_TRACK_COLOR; // 轨迹的颜色
    private int mTrackWidth = DEFAULT_TRACK_WIDTH_PX;    // 轨迹的宽度
    private float mSunRadius = DEFAULT_SUN_RADIUS_PX; // 太阳半径
    private int mLabelTextSize = DEFAULT_LABEL_TEXT_SIZE; // 标签文字大小
    private int mLabelTextColor = DEFAULT_LABEL_TEXT_COLOR; // 标签颜色
    private int mLabelVerticalOffset = DEFAULT_LABEL_VERTICAL_OFFSET_PX; // 竖直方向间距
    private int mLabelHorizontalOffset = DEFAULT_LABEL_HORIZONTAL_OFFSET_PX; // 水平方向间距

    private Paint mTrackPaint;  // 绘制半圆轨迹的Paint
    private Paint mShadowPaint; // 绘制日出日落阴影的Paint
    private Paint mLine; //绘制底部直线
    private TextPaint mLabelPaint;   // 绘制日出日落时间的Paint
    private Bitmap bitmap;

    /**
     * 日出时间
     */
    private Time mSunriseTime;
    /**
     * 日落时间
     */
    private Time mSunsetTime;

    /**
     * 绘图区域
     */
    private RectF mBoardRectF = new RectF();

    private SunriseSunsetLabelFormatter mLabelFormatter = new SimpleSunriseSunsetLabelFormatter();

    public SunriseSunsetView(Context context) {
        super(context);
        init();
    }

    public SunriseSunsetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunriseSunsetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SunriseSunsetView, defStyleAttr, 0);
        if (a != null) {
            mTrackColor = a.getColor(R.styleable.SunriseSunsetView_ssv_track_color, DEFAULT_TRACK_COLOR);
            mTrackWidth = a.getDimensionPixelSize(R.styleable.SunriseSunsetView_ssv_track_width, DEFAULT_TRACK_WIDTH_PX);
            mSunRadius = a.getDimensionPixelSize(R.styleable.SunriseSunsetView_ssv_sun_radius, DEFAULT_SUN_RADIUS_PX);
            mLabelTextColor = a.getColor(R.styleable.SunriseSunsetView_ssv_label_text_color, DEFAULT_LABEL_TEXT_COLOR);
            mLabelTextSize = a.getDimensionPixelSize(R.styleable.SunriseSunsetView_ssv_label_text_size, DEFAULT_LABEL_TEXT_SIZE);
            mLabelVerticalOffset = a.getDimensionPixelOffset(R.styleable.SunriseSunsetView_ssv_label_vertical_offset, DEFAULT_LABEL_VERTICAL_OFFSET_PX);
            mLabelHorizontalOffset = a.getDimensionPixelOffset(R.styleable.SunriseSunsetView_ssv_label_horizontal_offset, DEFAULT_LABEL_HORIZONTAL_OFFSET_PX);
            a.recycle();
        }
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingRight = getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        // 处理wrap_content这种情况
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            widthSpecSize = paddingLeft + paddingRight + MINIMAL_TRACK_RADIUS_PX * 2 + (int) mSunRadius * 2;
        }

        mTrackRadius = 1.0f * (widthSpecSize - paddingLeft - paddingRight - 2 * mSunRadius) / 2;
        int expectedHeight = (int) (mTrackRadius + mSunRadius + paddingBottom + paddingTop);
        mBoardRectF.set(paddingLeft + mSunRadius, paddingTop + mSunRadius, widthSpecSize - paddingRight - mSunRadius, expectedHeight - paddingBottom);
        setMeasuredDimension(widthSpecSize, expectedHeight);
    }

    private void init() {
        // 初始化半圆轨迹的画笔
        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setStyle(Paint.Style.STROKE); // 画笔的样式为线条
        prepareTrackPaint();

        // 初始化日出日落阴影的画笔
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        prepareLabelPaint();

        // 初始化日出日落阴影的画笔
        mLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLine.setStyle(Paint.Style.FILL_AND_STROKE);
        prepareLinePaint();
    }

    // 半圆轨迹的画笔
    private void prepareTrackPaint() {
        mTrackPaint.setColor(mTrackColor);
        mTrackPaint.setStrokeWidth(mTrackWidth);
    }

    // 底部线条的画笔
    private void prepareLinePaint() {
        mLine.setColor(Color.WHITE);
        mLine.setStrokeWidth(mTrackWidth);
    }

    // 标签的画笔
    private void prepareLabelPaint() {
        mLabelPaint.setColor(mLabelTextColor);
        mLabelPaint.setTextSize(mLabelTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSunTrack(canvas);
        drawShadow(canvas);
        drawSun(canvas);
        drawSunriseSunsetLabel(canvas);
    }

    // 绘制太阳轨道（半圆）
    private void drawSunTrack(Canvas canvas) {
        prepareTrackPaint();
        canvas.save();
        RectF rectF = new RectF(mBoardRectF.left, mBoardRectF.top, mBoardRectF.right, mBoardRectF.bottom + mBoardRectF.height());
        canvas.drawArc(rectF, 180, 180, false, mTrackPaint);

        canvas.restore();
    }

    // 绘制日出日落阴影部分
    private void drawShadow(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        float endY = mBoardRectF.bottom;
        RectF rectF = new RectF(mBoardRectF.left, mBoardRectF.top, mBoardRectF.right, mBoardRectF.bottom + mBoardRectF.height());
        float curPointX = mBoardRectF.left + mTrackRadius - mTrackRadius * (float) Math.cos(Math.PI * mRatio);

        path.moveTo(0, endY);
        path.arcTo(rectF, 180, 180 * mRatio);
        path.lineTo(curPointX, endY);
        path.close();

        Shader shader = new LinearGradient(200, 0, 200, 400,
                Color.parseColor("#48EFEFF0"),
                Color.parseColor("#00000000"), Shader.TileMode.CLAMP);
        mShadowPaint.setShader(shader);
        canvas.drawPath(path, mShadowPaint);
        canvas.restore();
    }

    // 绘制太阳
    private void drawSun(Canvas canvas) {
        canvas.save();
        float curPointX = mBoardRectF.left + mTrackRadius - mTrackRadius * (float) Math.cos(Math.PI * mRatio);
        float curPointY = mBoardRectF.bottom - mTrackRadius * (float) Math.sin(Math.PI * mRatio);
       // canvas.drawCircle(curPointX, curPointY, mSunRadius, mSunPaint);

        bitmap = SvgResources.getBitmapFromDrawable(R.drawable.ic_sun);
        bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);

        canvas.drawBitmap(bitmap, curPointX - 40, curPointY - 40, null);
        canvas.restore();
    }

    // 绘制日出日落标签
    private void drawSunriseSunsetLabel(Canvas canvas) {
        if (mSunriseTime == null || mSunsetTime == null) {
            return;
        }
        prepareLabelPaint();

        canvas.save();
        // 绘制日出时间
        String sunriseStr = mLabelFormatter.formatSunriseLabel(mSunriseTime);

        mLabelPaint.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetricsInt metricsInt = mLabelPaint.getFontMetricsInt();
        float baseLineX = mBoardRectF.left + mSunRadius + mLabelHorizontalOffset;
        float baseLineY = mBoardRectF.bottom - metricsInt.bottom - mLabelVerticalOffset;
        canvas.drawText(sunriseStr, baseLineX + 70, baseLineY, mLabelPaint);

        Bitmap bitmap = SvgResources.getBitmapFromDrawable(R.drawable.ic_sunrise);
        bitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true);
        canvas.drawBitmap(bitmap, baseLineX, baseLineY - 32, null);

        // 绘制日落时间
        mLabelPaint.setTextAlign(Paint.Align.RIGHT);
        String sunsetStr = mLabelFormatter.formatSunsetLabel(mSunsetTime);
        baseLineX = mBoardRectF.right - mSunRadius - mLabelHorizontalOffset;
        canvas.drawText(sunsetStr, baseLineX, baseLineY, mLabelPaint);

        Bitmap bitmap0 = SvgResources.getBitmapFromDrawable(R.drawable.ic_sunset);
        bitmap0 = Bitmap.createScaledBitmap(bitmap0, 40, 40, true);
        canvas.drawBitmap(bitmap0, baseLineX - 140, baseLineY - 32, null);

        canvas.restore();
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
        invalidate();
    }

    public void setSunriseTime(Time sunriseTime) {
        mSunriseTime = sunriseTime;
    }

    public Time getSunriseTime() {
        return mSunriseTime;
    }

    public void setSunsetTime(Time sunsetTime) {
        mSunsetTime = sunsetTime;
    }

    public Time getSunsetTime() {
        return mSunsetTime;
    }

    public float getSunRadius() {
        return mSunRadius;
    }

    public SunriseSunsetLabelFormatter getLabelFormatter() {
        return mLabelFormatter;
    }

    public void setLabelFormatter(SunriseSunsetLabelFormatter labelFormatter) {
        mLabelFormatter = labelFormatter;
    }

    public void setTrackColor(int trackColor) {
        mTrackColor = trackColor;
    }

    public void setTrackWidth(int trackWidthInPx) {
        mTrackWidth = trackWidthInPx;
    }

    public void setSunRadius(float sunRadius) {
        mSunRadius = sunRadius;
    }

    public void setLabelTextSize(int labelTextSize) {
        mLabelTextSize = labelTextSize;
    }

    public void setLabelTextColor(int labelTextColor) {
        mLabelTextColor = labelTextColor;
    }

    public void setLabelVerticalOffset(int labelVerticalOffset) {
        mLabelVerticalOffset = labelVerticalOffset;
    }

    public void setLabelHorizontalOffset(int labelHorizontalOffset) {
        mLabelHorizontalOffset = labelHorizontalOffset;
    }

    public void startAnimate() {
        if (mSunriseTime == null || mSunsetTime == null) {
            throw new RuntimeException("You need to set both sunrise and sunset time before start animation");
        }
        int sunrise = mSunriseTime.transformToMinutes();
        int sunset = mSunsetTime.transformToMinutes();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        int currentTime = currentHour * Time.MINUTES_PER_HOUR + currentMinute;
        float ratio = 1.0f * (currentTime - sunrise) / (sunset - sunrise);
        ratio = ratio <= 0 ? 0 : (ratio > 1.0f ? 1 : ratio);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "ratio", 0f, ratio);
        animator.setDuration(1500L);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

}
