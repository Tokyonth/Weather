package com.tokyonth.weather.view.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

import com.tokyonth.weather.model.bean.entity.Daily;
import com.tokyonth.weather.utils.tools.SvgResources;
import com.tokyonth.weather.utils.helper.WeatherInfoHelper;
import com.tokyonth.weather.utils.sundry.DisplayUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WeekWeatherView extends View {
    /**
     * 重要参数，两点之间分为几段描画，数字愈大分段越多，描画的曲线就越精细.
     */
    private static final int STEPS = 12;
    List<Integer> points_x;
    List<Integer> points_y;
    /**
     * x轴集合
     */
    private float mXAxis[] = new float[7];
    /**
     * 白天y轴集合
     */
    private float mYAxisDay[] = new float[7];
    /**
     * 夜间y轴集合
     */
    private float mYAxisNight[] = new float[7];
    /**
     * x,y轴集合数
     */
    private static final int LENGTH = 7;

    private List<Daily> data_list = null;
    private float clipWidth;

    private int width;
    private int height;
    private Context context;
    private Paint textPaint;
    private Paint tempPaint;
    private Paint pointPaint;
    private Paint tempLinePaint;


    public WeekWeatherView(Context context) {
        this(context, null);
    }

    public WeekWeatherView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekWeatherView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        points_x = new LinkedList<>();
        points_y = new LinkedList<>();

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(DisplayUtil.dip2px(context, 12));
        textPaint.setColor(Color.parseColor("#FFFFFF"));

        tempPaint = new Paint();
        tempPaint.setAntiAlias(true);
        tempPaint.setTextSize(DisplayUtil.dip2px(context, 12));
        tempPaint.setColor(Color.parseColor("#FFFFFF"));

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setColor(Color.RED);

        tempLinePaint = new Paint();
        tempLinePaint.setAntiAlias(true);
        tempLinePaint.setColor(Color.RED);
        tempLinePaint.setStrokeWidth(DisplayUtil.dip2px(context, 1));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        float wh = width / 14;
        mXAxis[0] = wh;
        mXAxis[1] = wh * 3;
        mXAxis[2] = wh * 5;
        mXAxis[3] = wh * 7;
        mXAxis[4] = wh * 9;
        mXAxis[5] = wh * 11;
        mXAxis[6] = wh * 13;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data_list != null) {
            // 计算y轴集合数值
            computeYAxisValues();
            // 画底部文字图标
            drawText(canvas);
            // 画布裁剪实现折线图动画
            canvas.clipRect(0,getHeight(),clipWidth,0);
            // 画白天折线图
            drawChart(canvas, Color.WHITE, data_list, mYAxisDay, 0);
            // 画夜间折线图
            drawChart(canvas, Color.WHITE, data_list, mYAxisNight, 1);
        }

    }

    /**
     * 计算y轴集合数值
     */
    private void computeYAxisValues() {
        // 存放白天最低温度
        int minTempDay = Integer.parseInt(data_list.get(0).getDay().getTempHigh());
        // 存放白天最高温度
        int maxTempDay = Integer.parseInt(data_list.get(0).getDay().getTempHigh());
        for (Daily item : data_list) {
            int item_temp = Integer.parseInt(item.getDay().getTempHigh());
            if (item_temp < minTempDay) {
                minTempDay = item_temp;
            }
            if (item_temp > maxTempDay) {
                maxTempDay = item_temp;
            }
        }

        // 存放夜间最低温度
        int minTempNight = Integer.parseInt(data_list.get(0).getNight().getTempLow());
        // 存放夜间最高温度
        int maxTempNight = Integer.parseInt(data_list.get(0).getNight().getTempLow());
        for (Daily item : data_list) {
            int item_temp = Integer.parseInt(item.getNight().getTempLow());
            if (item_temp < minTempNight) {
                minTempNight = item_temp;
            }
            if (item_temp > maxTempNight) {
                maxTempNight = item_temp;
            }
        }
        // 白天，夜间中的最低温度
        int minTemp = minTempNight < minTempDay ? minTempNight : minTempDay;
        // 白天，夜间中的最高温度
        int maxTemp = maxTempDay > maxTempNight ? maxTempDay : maxTempNight;
        // 份数（白天，夜间综合温差）
        float parts = maxTemp - minTemp;
        // y轴一端到控件一端的距离
        float length = DisplayUtil.dip2px(context,20);
        // y轴高度
        float yAxisHeight = height / 6 * 5 - length * 2;
        // 当温度都相同时（被除数不能为0）
        if (parts == 0) {
            for (int i = 0; i < LENGTH; i++) {
                mYAxisDay[i] = height / 6 * 5 / 2;
                mYAxisNight[i] = height / 6 * 5 / 2;
            }
        } else {

            float partValue = yAxisHeight / parts;

            for (int i = 0; i < LENGTH; i++) {
                mYAxisDay[i] = height - length - partValue * (Integer.parseInt(data_list.get(i).getDay().getTempHigh()) - minTemp);
                mYAxisNight[i] = height - length - partValue * (Integer.parseInt(data_list.get(i).getNight().getTempLow()) - minTemp);

            }

        }
    }

    /**
     * 画折线图
     *
     * @param canvas 画布
     * @param color  画图颜色
     * @param list   温度集合
     * @param yAxis  y轴集合
     * @param type   折线种类：0，白天；1，夜间
     */
    private void drawChart(Canvas canvas, int color, List<Daily> list, float[] yAxis, int type) {
        List<Point> point = new ArrayList<>();

        pointPaint.setColor(color);
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 20f}, 0);
        tempLinePaint.setColor(color);
        tempLinePaint.setStrokeWidth(DisplayUtil.dip2px(context, 2));
        tempLinePaint.setStyle(Paint.Style.STROKE);
        tempLinePaint.setStrokeCap(Paint.Cap.ROUND);
        tempLinePaint.setAntiAlias(true);
        tempLinePaint.setDither(true);
        tempLinePaint.setPathEffect(dashPathEffect);

        for (int i = 0; i < LENGTH; i++) {
            point.add(new Point((int) mXAxis[i], (int) yAxis[i] / 2));
            canvas.drawCircle(mXAxis[i], yAxis[i] / 2, DisplayUtil.dip2px(context, 3), pointPaint);
            if (type == 0) {
                drawText(canvas, tempPaint, i, list.get(i).getDay().getTempHigh(), yAxis, type);
            } else if (type == 1) {
                drawText(canvas, tempPaint, i, list.get(i).getNight().getTempLow(), yAxis, type);
            }

        }

        point.add(new Point((int) mXAxis[mXAxis.length - 1], (int) yAxis[yAxis.length - 1]));
        drawCurve(canvas, point);

    }

    /**
     * 绘制文字
     *
     * @param canvas    画布
     * @param textPaint 画笔
     * @param i         索引
     * @param str_temp  温度集合
     * @param yAxis     y轴集合
     * @param type      折线种类：0，白天；1，夜间
     */
    private void drawText(Canvas canvas, Paint textPaint, int i, String str_temp, float[] yAxis, int type) {
        // 显示白天气温
        Rect rect = new Rect();
        textPaint.getTextBounds(str_temp, 0, str_temp.length(), rect);
        float strWidth = rect.width();//字符串的宽度
        switch (type) {
            case 0:
                canvas.drawText(str_temp + "°", mXAxis[i] - strWidth/2, yAxis[i] / 2 - DisplayUtil.dip2px(context, 8), textPaint);
                break;
            case 1:
                canvas.drawText(str_temp + "°", mXAxis[i]- strWidth/2, yAxis[i] / 2 + DisplayUtil.dip2px(context, 15), textPaint);
                break;
        }
    }


    /*
    *
    * 此处定义较多
    *
    * 后期将会调整
    *
     */
    private void drawText(Canvas canvas) {

        for (int i = 0; i < data_list.size(); i++) {
            Rect rect = new Rect();
            data_list.get(0).setWeek("今 天");
            String str = data_list.get(i).getWeek();
            String str_date = data_list.get(i).getDate();

            textPaint.getTextBounds(str, 0, str.length(), rect);
            float strWidth = rect.width();//字符串的宽度
            float strHeight = rect.height();//字符串的高度

            canvas.drawText(str, width / 14 - strWidth / 2 + width / 7 * i, height / 2 + 100, textPaint);

            int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(data_list.get(i).getDay().getImg());
            Bitmap bitmap = SvgResources.getBitmapFromDrawable(weatherImagePath);
            bitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, true);

            canvas.drawBitmap(bitmap, width / 13.8f - strWidth / 2 + width / 7 * i, height/2 + 160, null);
            canvas.drawText(str_date.substring(5), width / 13 - strWidth / 2 + width / 7 * i, height/2 + 310, textPaint);
        }
    }


    /**
     * 画曲线.
     *
     * @param canvas
     */
    private void drawCurve(Canvas canvas,List<Point> points) {
        Path curvePath = new Path();
        points_x.clear();
        points_y.clear();
        for (int i = 0; i < points.size() - 1; i++) {
            points_x.add(points.get(i).x);
            points_y.add(points.get(i).y);
        }
        List<Cubic> calculate_x = calculate(points_x);
        List<Cubic> calculate_y = calculate(points_y);

        for (int i = 0; i < calculate_x.size(); i++) {
            if (i==0){
                curvePath.moveTo(calculate_x.get(0).eval(0), calculate_y.get(0).eval(0));
                for (int j = 1; j <= STEPS; j++) {
                    float u = j / (float) STEPS;
                    curvePath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                            .eval(u));
                }
                canvas.drawPath(curvePath, tempLinePaint);
            } else {
                if (i==1){
                    curvePath.reset();
                    curvePath.moveTo(calculate_x.get(1).eval(0), calculate_y.get(1).eval(0));
                    tempLinePaint.setPathEffect(null);
                }

                for (int j = 1; j <= STEPS; j++) {
                    float u = j / (float) STEPS;
                    curvePath.lineTo(calculate_x.get(i).eval(u), calculate_y.get(i)
                            .eval(u));
                }
            }

        }

        canvas.drawPath(curvePath, tempLinePaint);
    }

    private List<Cubic> calculate(List<Integer> x) {
        int n = x.size() - 1;
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];
        int i;

        gamma[0] = 1.0f / 2.0f;
        for (i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
        for (i = 1; i < n; i++) {
            delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
                    * gamma[i];
        }
        delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

        List<Cubic> cubics = new LinkedList<Cubic>();
        for (i = 0; i < n; i++) {
            Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
                    - 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
                    + D[i + 1]);
            cubics.add(c);
        }
        return cubics;
    }

    public void setData(List<Daily> list) {
        data_list = new ArrayList<>();
        data_list = list;
        startAnim();
    }

    public void startAnim(){
        float width = getWidth();
        ValueAnimator va = ValueAnimator.ofFloat(0, width);
        va.setDuration(1000);
        va.addUpdateListener(animation -> {
            clipWidth = (float) animation.getAnimatedValue();
            invalidate();
        });
        va.start();
    }

}
