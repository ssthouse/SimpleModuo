package com.mingko.simplemoduo.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ssthouse on 16/4/21.
 */
public class CircleView extends View {

    //是否初始化了大小
    private boolean isInited = false;

    //正方形外圈大小
    private int dimen;
    //画笔
    private Paint mPaint;
    //圆环大小---默认为宽度1/10
    private int ringWidth;

    //内环 外环 半径
    private int innerRadius, outterRadius;
    private int centerX, centerY;

    //当前方向
    private int currentAngle;

    public CircleView(Context context) {
        super(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initDimen() {
        isInited = true;

        ringWidth = dimen / 10;
        centerX = centerY = dimen/2;

        innerRadius = dimen/2 - ringWidth;
        outterRadius = dimen/2;


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(ringWidth);
        mPaint.setColor(Color.YELLOW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        dimen = widthSize;
        setMeasuredDimension(widthSize, widthSize);
        if(!isInited){
            initDimen();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画黄色圆环
        mPaint.setColor(Color.YELLOW);
        Path path = new Path();
        path.addCircle(centerX, centerY, outterRadius, Path.Direction.CCW);
        path.close();
        canvas.drawPath(path, mPaint);

        //画出灰色部分
        getSectorClip(canvas, 60, 60);

        //画出白色圆心
        path.reset();
        path.addCircle(centerX, centerY, innerRadius, Path.Direction.CCW);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(path, mPaint);

        super.onDraw(canvas);
    }

    /**
     * 返回一个扇形的剪裁区
     *
     * @param canvas     //画笔
     * @param startAngle //起始角度
     */
    private void getSectorClip(Canvas canvas, float startAngle, float sweepAngle) {
        mPaint.setColor(Color.GRAY);
        canvas.drawArc(new RectF(0, 0, dimen, dimen), startAngle, sweepAngle, true, mPaint);
    }
}
