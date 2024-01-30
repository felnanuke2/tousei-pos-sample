
/*
 * Copyright 2018 F1ReKing.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.centerm.oversea.sample.payment.module.transaction.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.centerm.oversea.sample.payment.R;

import java.io.ByteArrayOutputStream;

import androidx.annotation.ColorInt;

public class SignatureView extends View {

    private static final String TAG = "SignatureView";
    public static final int PEN_WIDTH = 10;
    public static final int PEN_COLOR = Color.BLACK;
    public static final int BACK_COLOR = Color.WHITE;

    private float mPenX;
    private float mPenY;
    private Paint mPaint = new Paint();
    private Path mPath = new Path();
    private Canvas mCanvas;
    private Bitmap cacheBitmap;
    private int mPentWidth = PEN_WIDTH;
    private int mPenColor = PEN_COLOR;
    private int mBackColor = BACK_COLOR;
    private boolean isTouched = false;

    public SignatureView(Context context) {
        this(context, null);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SignatureView);
        mPenColor = typedArray.getColor(R.styleable.SignatureView_penColor, PEN_COLOR);
        mBackColor = typedArray.getColor(R.styleable.SignatureView_backColor, BACK_COLOR);
        mPentWidth = typedArray.getInt(R.styleable.SignatureView_penWidth, PEN_WIDTH);
        typedArray.recycle();
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPentWidth);
        mPaint.setColor(mPenColor);
    }

    public boolean getTouched() {
        return isTouched;
    }

    public void setPentWidth(int pentWidth) {
        mPentWidth = pentWidth;
    }

    public void setPenColor(@ColorInt int penColor) {
        mPenColor = penColor;
    }

    public void setBackColor(@ColorInt int backColor) {
        mBackColor = backColor;
    }

    /**
     * clear sign pad
     */
    public void clear() {
        if (mCanvas != null) {
            isTouched = false;
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas.drawColor(mBackColor);
            invalidate();
        }
    }

    /**
     * get sign pad bitmap format
     */
    public byte[] getBitmap() {
        Bitmap bitmap = cacheBitmap;
        Bitmap result = clearBlank(bitmap, 10);
        if (result == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }


    private Bitmap clearBlank(Bitmap bmp, int blank) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[width];
        boolean isStop;
        //扫描上边距不等于背景颜色的第一个点
        for (int i = 0; i < height; i++) {
            bmp.getPixels(pixs, 0, width, 0, i, width, 1);
            isStop = false;
            for (int pix :
                    pixs) {
                if (pix != mBackColor) {
                    top = i;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (int i = height - 1; i >= 0; i--) {
            bmp.getPixels(pixs, 0, width, 0, i, width, 1);
            isStop = false;
            for (int pix :
                    pixs) {
                if (pix != mBackColor) {
                    bottom = i;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[height];
        //扫描左边距不等于背景颜色的第一个点
        for (int x = 0; x < width; x++) {
            bmp.getPixels(pixs, 0, 1, x, 0, 1, height);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (int x = width - 1; x > 0; x--) {
            bmp.getPixels(pixs, 0, 1, x, 0, 1, height);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        //计算加上保留空白距离之后的图像大小
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > width - 1 ? width - 1 : right + blank;
        bottom = bottom + blank > height - 1 ? height - 1 : bottom + blank;
        return Bitmap.createBitmap(bmp, left, top, right - left, bottom - top);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(cacheBitmap);
        mCanvas.drawColor(mBackColor);
        isTouched = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(cacheBitmap, 0, 0, mPaint);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPenX = event.getX();
                mPenY = event.getY();
                mPath.moveTo(mPenX, mPenY);
                return true;
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                float x = event.getX();
                float y = event.getY();
                float penX = mPenX;
                float penY = mPenY;
                float dx = Math.abs(x - penX);
                float dy = Math.abs(y - penY);
                if (dx >= 3 || dy >= 3) {
                    float cx = (x + penX) / 2;
                    float cy = (y + penY) / 2;
                    mPath.quadTo(penX, penY, cx, cy);
                    mPenX = x;
                    mPenY = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);
                mPath.reset();
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }
}
