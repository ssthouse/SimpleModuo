package com.mingko.simplemoduo.model;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
    private int innerRadius, outerRadius;
    private int centerX, centerY;

    //当前方向(0 - 360°)
    private int currentAngle;

    //*******************************角度变化监听器**************
    public interface AngleChangeListener {
        void onAngleChange(int newAngle);
    }

    private AngleChangeListener angleChangeListener;

    //********************构造方法*****************************************
    public CircleView(Context context) {
        super(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //*****************初始化view尺寸****************************************
    private void initDimen() {
        isInited = true;

        ringWidth = dimen / 10;
        centerX = centerY = dimen / 2;

        innerRadius = dimen / 2 - ringWidth;
        outerRadius = dimen / 2;


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
        if (!isInited) {
            initDimen();
        }
    }


    //*************************************画出view*********************************
    @Override
    protected void onDraw(Canvas canvas) {
        //画黄色圆环
        mPaint.setColor(Color.YELLOW);
        Path path = new Path();
        path.addCircle(centerX, centerY, outerRadius, Path.Direction.CCW);
        path.close();
        canvas.drawPath(path, mPaint);

        //画出灰色部分
        getSectorClip(canvas, 60, 60);

        //画出白色圆心
        path.reset();
        path.addCircle(centerX, centerY, innerRadius, Path.Direction.CCW);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(path, mPaint);

        //画出当前魔哆指示的方向
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth((outerRadius - innerRadius) / 2);
        //找到原点
        int pointerX = dimen / 2 - (int) (Math.cos((currentAngle + 90) * Math.PI / 180) * ((innerRadius + outerRadius) / 2));
        int pointerY = dimen / 2 - (int) (Math.sin((currentAngle + 90) * Math.PI / 180) * ((innerRadius + outerRadius) / 2));
        canvas.drawCircle(pointerX, pointerY, (outerRadius - innerRadius) / 2, mPaint);
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

    //******************************点击事件********************************

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int newAngle;
            float touchX = event.getX();
            float touchY = event.getY();
            //计算出当前角度(0-360)
            //以圆形为原点的坐标
            float indexX = touchX - dimen / 2;
            float indexY = dimen / 2 - touchY;
            //Timber.e("X: " + indexX + "    Y:" + indexY);
            if (indexX >= 0 && indexY >= 0) {
                //第一象限
                newAngle = (int) (90 - Math.atan(Math.abs(indexY / indexX)) * 180 / Math.PI);
            } else if (indexX >= 0 && indexY <= 0) {
                //第四象限
                newAngle = (int) (90 + Math.atan(Math.abs(indexY / indexX)) * 180 / Math.PI);
            } else if (indexX <= 0 && indexY >= 0) {
                //第二象限
                newAngle = (int) (270 + Math.atan(Math.abs(indexY / indexX)) * 180 / Math.PI);
            } else {
                //第三象限
                newAngle = (int) (270 - Math.atan(Math.abs(indexY / indexX)) * 180 / Math.PI);
            }
            //Timber.e("angle: " + currentAngle);
            //如果点击的位置不对---需要手动修正
            if (newAngle > 150 && newAngle < 180) {
                newAngle = 150;
            }
            if (newAngle > 180 && newAngle < 210) {
                newAngle = 210;
            }
            setCurrentAngle(newAngle);
        }
        return true;
    }

    //更新当前angle
    public void setCurrentAngle(int newAngle) {
        if (currentAngle == newAngle) {
            return;
        }
        currentAngle = newAngle;
        //像监听器发送消息
        if (angleChangeListener != null) {
            angleChangeListener.onAngleChange(currentAngle);
        }
        invalidate();
    }

    public void setAngleChangeListener(AngleChangeListener listener) {
        this.angleChangeListener = listener;
    }

    public int getCurrentAngle() {
        return currentAngle;
    }
}
