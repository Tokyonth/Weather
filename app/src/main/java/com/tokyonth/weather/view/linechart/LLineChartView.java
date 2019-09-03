package com.tokyonth.weather.view.linechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author lollipop
 * @date 2019.06.12 20:10
 * 折线图的 View
 */
public class LLineChartView extends View {

    public LLineChartView(Context context) {
        super(context, null);
    }

    public LLineChartView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public LLineChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr, 0);
    }

    public LLineChartView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    private Builder options = null;
    private ArrayList<Point> pointList = new ArrayList<>();
    private boolean isLayouted = false;
    private float frameRight = 0;
    private float frameTop = 0;
    private float baseLine = 0;
    private RectF ranges = new RectF();
    private ArrayList<Scale> xScaleList = new ArrayList<>();
    private ArrayList<Scale> yScaleList = new ArrayList<>();
    private Path linePath = new Path();
    private Paint paint = new Paint();
    private Path dashedLine = new Path();

    public void init(Builder builder) {
        this.options = builder;
        onDataChange();
    }

    public void putData(List<Point> data) {
        pointList.clear();
        addData(data);
    }

    public void addData(List<Point> data) {
        pointList.addAll(data);
        sort();
        onDataChange();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        isLayouted = true;
        onDataChange();
    }

    private void onDataChange() {
        if (!isLayouted) {
            return;
        }
        if (options == null) {
            throw new RuntimeException("view is not init, please init(Builder builder)");
        }
        float minValue = 0;
        float maxValue = 0;
        if (options.isAutoScaleY) {
            maxValue = Float.MIN_VALUE;
            minValue = Float.MAX_VALUE;
            for (Point p : pointList) {
                if (p.value > maxValue) {
                    maxValue = p.value;
                }
                if (p.value < minValue) {
                    minValue = p.value;
                }
            }
            float range = maxValue - minValue;
            maxValue += range * options.autoScaleYMax;
            minValue -= range * options.autoScaleYMin;
        } else {
            maxValue = options.scaleYMax;
            minValue = options.scaleYMin;
        }
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int height = getHeight() - getPaddingBottom() - getPaddingTop() ;
        float left = getPaddingLeft();
        float right = width - getPaddingRight();
        float bottom = height - getPaddingBottom();
        float top = getPaddingTop();
        baseLine = bottom - options.scaleXFontSize + options.scaleXOffset;
        frameTop = top + options.legendFontSize;
        ranges.set(left, top, right, bottom);

        if (options.scaleYValueProvider == null) {
            throw new RuntimeException("builder.scaleXValueProvider is null");
        }
        float yScaleStep = (baseLine - frameTop) * 1F / (options.scaleYCount - 1);
        float yStep = (maxValue - minValue) / (options.scaleYCount - 1);
        String maxLengthValue = "";
        yScaleList.clear();
        for (int index = 0, count = options.scaleYCount; index < count; index++) {
            String value = options.scaleYValueProvider.getScaleValue(index, maxValue, yStep * index);
            Scale scale = new Scale();
            scale.value = value;
            scale.y = baseLine - yScaleStep * index;
            if (value.length() > maxLengthValue.length()) {
                maxLengthValue = value;
            }
            yScaleList.add(scale);
        }

        paint.setTextSize(options.scaleYFontSize);
        float strWidth = paint.measureText(maxLengthValue);
        frameRight = right - strWidth + options.scaleYOffset;

        if (options.scaleXValueProvider == null) {
            throw new RuntimeException("builder.scaleXValueProvider is null");
        }
        xScaleList.clear();
        float xScaleStep = (frameRight - left) * 1F / (options.scaleXCount - 1);
        for (int index = 0, count = options.scaleXCount; index < count; index++) {
            String value = options.scaleXValueProvider.getScaleValue(index, count, index);
            Scale scale = new Scale();
            scale.value = value;
            scale.x = xScaleStep * index + left;
            xScaleList.add(scale);
        }

        float frameWidth = frameRight - left;
        float frameHeight = baseLine - top;
        linePath.reset();
        for (int index = 0, count = pointList.size(); index < count; index++) {
            Point point = pointList.get(index);
            point.setup(frameWidth, frameHeight,
                    options.isAutoLocation, index, count, maxValue, minValue);
            float x = point.x + left;
            float y = point.y + top;
            Log.d("Lollipop", "frameWidth:" + frameWidth + ", frameHeight:" + frameHeight + ", left:" + left + ", top:" + top);
            Log.d("Lollipop", "point:[" + x + "," + y + "]");
            if (index == 0) {
                linePath.moveTo(x, y);
            } else {
                linePath.lineTo(x, y);
            }
        }

        dashedLine.reset();
        if (options.latticeDashedLineY != null
                && options.latticeDashedLineY.length > 0) {
            float y = baseLine;
            int index = 0;
            while (y > frameTop) {
                index %= options.latticeDashedLineY.length;
                float length = options.latticeDashedLineY[index];
                for (int i = 0, count = options.scaleXCount; i < count; i++) {
                    float x = xScaleStep * i + left;
                    if (y - length < frameTop) {
                        length = y - frameTop;
                    }
                    dashedLine.moveTo(x, y);
                    dashedLine.rLineTo(0, length * -1);
                }
                y -= length;
                index++;
                // 空白部分
                y -= options.latticeDashedLineY[index];
                index++;
            }
        }
        if (options.latticeDashedLineX != null
                && options.latticeDashedLineX.length > 0) {
            float x = left;
            int index = 0;
            while (x < frameRight) {
                index %= options.latticeDashedLineX.length;
                float length = options.latticeDashedLineX[index];
                for (int i = 0, count = options.scaleYCount; i < count; i++) {
                    float y = yScaleStep * i + frameTop;
                    if (x + length > frameRight) {
                        length = frameRight - x;
                    }
                    dashedLine.moveTo(x, y);
                    dashedLine.rLineTo(length, 0);
                }
                x += length;
                index++;
                // 空白部分
                x += options.latticeDashedLineX[index];
                index++;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (options == null) {
            return;
        }
        paint.setStyle(Paint.Style.FILL);
        // 画边框
      //  paint.setColor(options.frameColor);
       // paint.setStrokeWidth(options.frameStrokeWidth);
       // canvas.drawLine(ranges.left, baseLine, frameRight, baseLine, paint);
       // canvas.drawLine(frameRight, baseLine, frameRight, frameTop, paint);

        // 刻度文字: X
        paint.setTextSize(options.scaleXFontSize);
        paint.setColor(options.scaleXColor);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float xScaleY = baseLine + options.scaleXFontSize / 2 - fm.descent + (fm.descent - fm.ascent) / 2 + options.scaleXOffset;
        paint.setTextAlign(Paint.Align.CENTER);
        for (Scale scale : xScaleList) {
            canvas.drawText(scale.value, scale.x, xScaleY, paint);
        }
        // 刻度文字: Y
        /*paint.setTextSize(options.scaleYFontSize);
        paint.setColor(options.scaleYColor);
        fm = paint.getFontMetrics();
        float yScaleY = (fm.descent - fm.ascent) / 2 - fm.descent;
        float yScaleX = frameRight + options.scaleYOffset;
        paint.setTextAlign(Paint.Align.LEFT);
        for (Scale scale : yScaleList) {
            canvas.drawText(scale.value, yScaleX, scale.y + yScaleY, paint);
        }*/

        // 画图例
       // paint.setTextSize(options.legendFontSize);
      //  paint.setColor(options.legendColor);
        fm = paint.getFontMetrics();
        paint.setTextAlign(Paint.Align.RIGHT);
        float legendCenterY = ranges.top + options.legendFontSize / 2;
        float legendY = (fm.descent - fm.ascent) / 2 - fm.descent + legendCenterY;
        canvas.drawText(options.legendValue, frameRight, legendY, paint);
        float legendLeft = frameRight - paint.measureText(options.legendValue);
        paint.setColor(options.lineColor);
        paint.setStrokeWidth(options.lineDiameter);
      //  canvas.drawLine(legendLeft, legendCenterY, legendLeft - options.legendLineLength, legendCenterY, paint);

        paint.setStyle(Paint.Style.STROKE);

        // 画格子
     //   paint.setColor(options.latticeColor);
       // paint.setStrokeWidth(options.latticeDashedLineStrokeWidth);
      //  canvas.drawPath(dashedLine, paint);

        // 画折线
        paint.setColor(options.lineColor);
        paint.setStrokeWidth(options.lineDiameter);
        canvas.drawPath(linePath, paint);

    }

    private void sort() {
        Collections.sort(pointList);
    }

    private static class Scale {
        float x = 0;
        float y = 0;
        String value = "";
    }

    public static class Builder {

        private Builder() {}

        public static Builder create() {
            return new Builder();
        }

        /**
         * 图例的文字大小
         */
        public float legendFontSize = 0;
        /**
         * 图例的文字内容
         */
        public String legendValue = "";

        /**
         * 图例的颜色
         */
        public int legendColor = Color.GRAY;

        /**
         * 图例线条的长度
         */
        public float legendLineLength = 0;

        /**
         * 线条的颜色
         */
        public int lineColor = Color.WHITE;

        /**
         * 边框的颜色
         */
        public int frameColor = Color.GRAY;

        /**
         * 边框的线条宽度
         */
        public float frameStrokeWidth = 0;

        /**
         * 格子颜色
         */
        public int latticeColor = Color.GRAY;

        /**
         * 格子线条的宽度
         */
        public float latticeDashedLineStrokeWidth = 0;

        /**
         * 格子的横向虚线
         */
        public int[] latticeDashedLineX = new int[0];

        /**
         * 格子的纵向虚线
         */
        public int[] latticeDashedLineY = new int[0];

        /**
         * X轴刻度的文字大小
         */
        public float scaleXFontSize = 0;

        /**
         * Y轴刻度的文字大小
         */
        public float scaleYFontSize = 0;

        /**
         * x 轴刻度文字的颜色
         */
        public int scaleXColor = Color.GRAY;

        /**
         * y 轴刻度文字的颜色
         */
        public int scaleYColor = Color.GRAY;

        /**
         * X轴的刻度数量
         */
        public int scaleXCount = 0;

        /**
         * Y 轴刻度数量
         */
        public int scaleYCount = 0;

        /**
         * X轴的刻度偏移量
         */
        public float scaleXOffset = 0;

        /**
         * Y 轴刻度偏移量
         */
        public float scaleYOffset = 0;

        /**
         * Y轴刻度的最大值
         */
        public float scaleYMax = 0;

        /**
         * Y 轴刻度最小值
         */
        public float scaleYMin = 0;

        /**
         * 自动设置 Y 轴的刻度区间
         */
        public boolean isAutoScaleY = true;

        /**
         * 根据区间，自动上浮 30% 作为上限
         */
        public float autoScaleYMax = 0.3F;

        /**
         * 根据区间，自动下浮 30% 作为下限
         */
        public float autoScaleYMin = 0.3F;

        /**
         * X 轴的刻度值
         */
        public ScaleValueProvider scaleXValueProvider = null;

        /**
         * Y 轴的刻度值
         */
        public ScaleValueProvider scaleYValueProvider = null;

        /**
         * 折线的直径
         */
        public float lineDiameter = 0;

        /**
         * 是否自动计算位置
         * 如果为 true，那么将自动将每个点平均分布在 X 轴上
         */
        public boolean isAutoLocation = false;

    }

    public static class Point implements Comparable<Point> {

        /**
         * 当前点的数值
         */
        public float value = 0;

        /**
         * 点的位置
         * 表示 X 轴从左到右的百分比数值
         */
        public float location = 0;

        private float x = 0;

        private float y = 0;

        private void setup(float width, float height, boolean isAutoLocation, int index,
                           int count, float maxValue, float minValue) {
            Log.d("Lollipop", "value: " + value + "width: " + width + ", height: " + height + ", isAutoLocation: " + isAutoLocation + ", index: " + index + " ," +
                    "count: " + count + ", maxValue: " + maxValue + ", minValue: " + minValue);
            if (isAutoLocation) {
                location = 1F * index / count;
            }
            x = width * location;
            y = (value - minValue) / (maxValue - minValue) * height;
        }

        @Override
        public int compareTo(Point o) {
            return Float.compare(this.location, o.location);
        }
    }

    public interface ScaleValueProvider {
        String getScaleValue(int index, float maxValue, float value);
    }

}
