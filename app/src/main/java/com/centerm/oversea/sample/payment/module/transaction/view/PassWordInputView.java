package com.centerm.oversea.sample.payment.module.transaction.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Qzhhh on 2016/10/10.
 */

public class PassWordInputView extends View {
    private Paint mPaint;
    /**
     * 密码圆的个数
     */
    private int count = 6;
    /**
     * 密码圆的半径
     */
    private float rad = 12;
    /**
     * 单个密码的宽度
     */
    private float aWith = 40;
    /**
     * 模式为横线模式时的密码圆点和底下横线的距离
     */
    private float bHeight = 10;
    /**
     * 隔开模式 true为底下为横线 false为画框框
     */
    private boolean isLines = true;
    /**
     * 整个控件的宽度
     */
    private int viewWidth = 0;
    private int viewHeight = 0;
    /**
     * 自动分配宽度 框是连在一起的
     */
    private boolean autoWidth = false;

    /**
     * 分隔符是否存在
     */
    private boolean isExistSeparator = false;
    /**
     * 当前选中的个数
     */
    private int nowSelectedCount = 0;
    private int circleColor = Color.BLACK;
    private int rectColor = Color.BLACK;
    private int lineColor = Color.BLACK;
    private PassWordInputViewListener mListener;

    public PassWordInputView(Context context) {
        super(context);
        Log.d("qzh", "PassWordInputView。");
    }

    public PassWordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PassWordInputView);
//        Log.d("qzh", "开始。。。");
//        count = mTypedArray.getInt(R.styleable.PassWordInputView_count, 6);
//        rad = mTypedArray.getDimension(R.styleable.PassWordInputView_rad, 20);
//        aWith = mTypedArray.getDimension(R.styleable.PassWordInputView_aWith, 40);
//        bHeight = mTypedArray.getDimension(R.styleable.PassWordInputView_bHeight, 10);
//        isLines = mTypedArray.getBoolean(R.styleable.PassWordInputView_isLines, true);
//        circleColor = mTypedArray.getColor(R.styleable.PassWordInputView_circleColor, Color.BLACK);
//        rectColor = mTypedArray.getColor(R.styleable.PassWordInputView_rectColor, Color.BLACK);
//        lineColor = mTypedArray.getColor(R.styleable.PassWordInputView_lineColor, Color.BLACK);
//        autoWidth = mTypedArray.getBoolean(R.styleable.PassWordInputView_autoWidth, false);
//        isExistSeparator = mTypedArray.getBoolean(R.styleable.PassWordInputView_isExistSeparator, false);
//        mTypedArray.recycle();
    }

    public PassWordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
//        Log.d("qzh", "width:" + viewWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int mWide = viewWidth / count;
        if (autoWidth) {
            aWith = viewWidth / count;
        }
        int x = 0;
        int y = 0;
        if (!isExistSeparator) {
            if (isLines) {
                mPaint.setColor(lineColor);
                canvas.drawLine(x, y + viewHeight, x + viewWidth, y + viewHeight, mPaint);
            } else {
                mPaint.setColor(rectColor);
                mPaint.setStyle(Paint.Style.STROKE);
                RectF mRectF = new RectF(x, y, x + viewWidth, y + viewHeight);
                canvas.drawRect(mRectF, mPaint);
            }
        }
        for (int i = 0; i < count; i++) {
            if (i < nowSelectedCount) {
                mPaint.setColor(circleColor);
                mPaint.setStyle(Paint.Style.FILL);
                if (isExistSeparator) {
                    canvas.drawCircle(x + aWith / 2, y + aWith, rad, mPaint);
                } else {
                    canvas.drawCircle(x + aWith / 2, y + viewHeight / 2, rad, mPaint);
                }
            }
            if (isExistSeparator) {
                if (isLines) {
                    mPaint.setColor(lineColor);
                    canvas.drawLine(x, y + aWith * 2 + bHeight, x + aWith, y + aWith * 2 + bHeight, mPaint);
                } else {
                    mPaint.setColor(rectColor);
                    mPaint.setStyle(Paint.Style.STROKE);
                    RectF mRectF = new RectF(x, y + aWith / 2, x + aWith, y + aWith / 2 + aWith);
                    canvas.drawRect(mRectF, mPaint);
                }
            }
            x += mWide;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mListener != null) {
                mListener.onClick();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnclickListener(PassWordInputViewListener mListener) {
        this.mListener = mListener;
    }

    public int getNowSelectedCount() {
        return nowSelectedCount;
    }

    public synchronized void setNowSelectedCount(int nowSelectedCount) {
        if (nowSelectedCount < 0 || nowSelectedCount > count) {
            return;
        }
        this.nowSelectedCount = nowSelectedCount;
        postInvalidate();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public void setbHeight(int bHeight) {
        this.bHeight = bHeight;
    }

    public boolean isLines() {
        return isLines;
    }

    public void setLines(boolean lines) {
        isLines = lines;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public float getRad() {
        return rad;
    }

    public void setRad(float rad) {
        this.rad = rad;
    }

    public float getaWith() {
        return aWith;
    }

    public void setaWith(float aWith) {
        this.aWith = aWith;
    }

    public float getbHeight() {
        return bHeight;
    }

    public void setbHeight(float bHeight) {
        this.bHeight = bHeight;
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
    }

    public int getRectColor() {
        return rectColor;
    }

    public void setRectColor(int rectColor) {
        this.rectColor = rectColor;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public boolean isAutoWidth() {
        return autoWidth;
    }

    public void setAutoWidth(boolean autoWidth) {
        this.autoWidth = autoWidth;
    }
}
