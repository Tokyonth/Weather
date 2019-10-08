package com.tokyonth.weather.view.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.sundry.DisplayUtil;

public class SemicircleProgressView extends View {

    private Context mContext;

    private int semicircletitleColor;
    // 默认Padding值
    private final static int defaultPadding = 20;
    // view宽度
    private int width;
    // view高度
    private int height;
    //  圆环起始角度
    private final static float mStartAngle = 125f;
    // 圆环结束角度
    private final static float mEndAngle = 290f;
    //外层圆环画笔
    private Paint mMiddleArcPaint;
    //title文本画笔
    private Paint mTextPaint;
    //subtitle文本画笔
    private Paint mTextPaint2;
    //进度圆环画笔
    private Paint mArcProgressPaint;
    //半径
    private int radius;
    //外层矩形
    private RectF mMiddleRect;
    //进度矩形
    private RectF mMiddleProgressRect;
    // 最小数字
    private int mMinNum = 0;
    // 最大数字
    private int mMaxNum = 40;
    // 当前进度
    private float mCurrentAngle = 0f;
    //总进度
    private float mTotalAngle = 290f;
    //等级
    private String sesameLevel = "";
    //标题
    private String Title = "";
    //副标题
    private String SubTile = "";
    //当前点的实际位置
    private float[] pos;
    //当前点的tangent值
    private float[] tan;
    //矩阵
    private Matrix matrix;

    private int SemicircleSize;
    private int SemicirclelineSize;
    private int backgroundLineColor;

    private int frontLineColor;
    private int titleColor;
    private int subtitleColor;
    private int maxWidth;
    private int maxHeight;
    private int semicircletitleSize;
    private int semicirclesubtitleSize;

    public SemicircleProgressView(Context context) {
        this(context, null);
    }

    public SemicircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SemicircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }


    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SemicircleProgressView);
        SemicircleSize = typedArray.getDimensionPixelSize(R.styleable.SemicircleProgressView_semicircleSize, DisplayUtil.dip2px(getContext(), 100));
        SemicirclelineSize = typedArray.getDimensionPixelSize(R.styleable.SemicircleProgressView_semicirclelineSize, DisplayUtil.dip2px(getContext(), 3));
        backgroundLineColor = typedArray.getColor(R.styleable.SemicircleProgressView_semicirclebackgroundLineColor, getResources().getColor(android.R.color.darker_gray));
        frontLineColor = typedArray.getColor(R.styleable.SemicircleProgressView_semicirclefrontLineColor, getResources().getColor(android.R.color.holo_orange_dark));
        titleColor = typedArray.getColor(R.styleable.SemicircleProgressView_semicircletitleColor, getResources().getColor(android.R.color.holo_orange_dark));
        subtitleColor = typedArray.getColor(R.styleable.SemicircleProgressView_semicirclesubtitleColor, getResources().getColor(android.R.color.darker_gray));
        semicircletitleSize = typedArray.getDimensionPixelSize(R.styleable.SemicircleProgressView_semicircletitleSize, DisplayUtil.sp2px(getContext(), 20));
        semicirclesubtitleSize = typedArray.getDimensionPixelSize(R.styleable.SemicircleProgressView_semicirclesubtitleSize, DisplayUtil.sp2px(getContext(), 17));
        Title = typedArray.getString(R.styleable.SemicircleProgressView_semicircletitleText);
        SubTile = typedArray.getString(R.styleable.SemicircleProgressView_semicirclesubtitleText);
        if (TextUtils.isEmpty(Title)) {
            Title = "";
        }
        if (TextUtils.isEmpty(SubTile)) {
            SubTile = "";
        }

        //外层圆环画笔
        mMiddleArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMiddleArcPaint.setStrokeWidth(SemicirclelineSize);
        mMiddleArcPaint.setColor(backgroundLineColor);
        mMiddleArcPaint.setStyle(Paint.Style.STROKE);
        mMiddleArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mMiddleArcPaint.setAlpha(90);

        //正中间字体画笔
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(titleColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        //hour字体画笔
        mTextPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint2.setColor(subtitleColor);
        mTextPaint2.setTextAlign(Paint.Align.CENTER);

        //外层进度画笔
        mArcProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcProgressPaint.setStrokeWidth(SemicirclelineSize);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        //初始化小圆点图片
        //bitmap = BitmapFactory.decodeResource(getResources(), io.netopen.hotbitmapgg.view.R.drawable.ic_circle);
        pos = new float[2];
        tan = new float[2];
        matrix = new Matrix();
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTile() {
        return SubTile;
    }

    public void setSubTile(String subTile) {
        SubTile = subTile;
    }

    public int getSemicircletitleColor() {
        return semicircletitleColor;
    }

    public void setSemicircletitleColor(int semicircletitleColor) {
        this.semicircletitleColor = semicircletitleColor;
    }

    public int getFrontLineColor() {
        return frontLineColor;
    }

    public void setFrontLineColor(int frontLineColor) {
        this.frontLineColor = frontLineColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int computedWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int computedHeight = resolveMeasured(heightMeasureSpec, minimumHeight);

        setMeasuredDimension(computedWidth, computedHeight);
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxWidth = w;
        maxHeight = h;
        width = SemicircleSize;
        radius = width / 2;

        mMiddleRect = new RectF((maxWidth / 2) - radius, (maxHeight / 2) - radius, (maxWidth / 2)
                + radius, (maxHeight / 2) + radius);

        mMiddleProgressRect = new RectF((maxWidth / 2) - radius, (maxHeight / 2) - radius, (maxWidth / 2)
                + radius, (maxHeight / 2) + radius);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMiddleArc(canvas);
        drawCenterText(canvas);
        drawRingProgress(canvas);
    }

    /**
     * 绘制外层圆环进度和小圆点
     */
    private void drawRingProgress(Canvas canvas) {
        Path path = new Path();
        path.addArc(mMiddleProgressRect, mStartAngle, mCurrentAngle);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        pathMeasure.getPosTan(pathMeasure.getLength() * 1, pos, tan);
        matrix.reset();
    //        matrix.postTranslate(pos[0] - bitmap.getWidth() / 2, pos[1] - bitmap.getHeight() / 2);
        canvas.drawPath(path, mArcProgressPaint);

    }

    /**
     * 绘制中间文本
     */
    private void drawCenterText(Canvas canvas) {
        //绘制Title
        mTextPaint.setTextSize(semicircletitleSize);
        mTextPaint.setColor(semicircletitleColor);
        canvas.drawText(Title, (maxWidth / 2), (maxHeight / 2) - DisplayUtil.dip2px(getContext(), 10), mTextPaint);
        //绘制SubTile
        mTextPaint2.setTextSize(semicirclesubtitleSize);
        canvas.drawText(SubTile, (maxWidth / 2), (maxHeight / 2) + DisplayUtil.dip2px(getContext(), 10), mTextPaint2);
        canvas.drawText(sesameLevel, (maxWidth / 2), radius + (maxHeight / 2), mTextPaint2);
    }


    /**
     * 绘制外层圆环
     */
    private void drawMiddleArc(Canvas canvas) {

        mArcProgressPaint.setColor(frontLineColor);
        canvas.drawArc(mMiddleRect, mStartAngle, mEndAngle, false, mMiddleArcPaint);
    }

    public void setSesameValues(int values, int totel) {
        if (values >= 0) {
            mMaxNum = values;
            //  mTotalAngle = 290f;
            sesameLevel = values + "/" + totel;
           // sesameLevel = values + "";
            mTotalAngle = ((float) values / (float) totel) * 290f;
            startAnim();
        }
    }

    public void startAnim() {
        ValueAnimator mAngleAnim = ValueAnimator.ofFloat(mCurrentAngle, mTotalAngle);
        mAngleAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAngleAnim.setDuration(2000);
        mAngleAnim.addUpdateListener(valueAnimator -> {

            mCurrentAngle = (float) valueAnimator.getAnimatedValue();
            postInvalidate();
        });
        mAngleAnim.start();

        // mMinNum = 350;
        ValueAnimator mNumAnim = ValueAnimator.ofInt(mMinNum, mMaxNum);
        mNumAnim.setDuration(2000);
        mNumAnim.setInterpolator(new LinearInterpolator());
        mNumAnim.addUpdateListener(valueAnimator -> {
            mMinNum = (int) valueAnimator.getAnimatedValue();
            postInvalidate();
        });
        mNumAnim.start();
    }

}