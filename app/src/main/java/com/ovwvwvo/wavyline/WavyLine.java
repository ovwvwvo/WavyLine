package com.ovwvwvo.wavyline;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public final class WavyLine extends View {
    private Path mPath;
    private Paint mPaint;
    private Random random;

    private final static int period_default = 80;
    private final static int period_small = 20;

    private final static float maxamplitude_default = 0.7f;

    private float maxamplitude; //最大振幅的0.0`1.0倍
    private int period; //周期
    private int fluctuate;//该属性存在时，maxamplitude、period不起作用

    public WavyLine(Context context) {
        super(context);
        init(context, null);
    }

    public WavyLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        random = new Random();
        mPath = new Path();

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.WavyLine);
        int color = array.getColor(R.styleable.WavyLine_Color, Color.BLACK);
        float size = array.getDimension(R.styleable.WavyLine_StrokeWidth, 3.0f);
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(size);

        fluctuate = array.getInt(R.styleable.WavyLine_fluctuate, -1);
        if (fluctuate == -1) {
            maxamplitude = Math.min(array.getFloat(R.styleable.WavyLine_maxamplitude, maxamplitude_default), maxamplitude_default);
            period = array.getInt(R.styleable.WavyLine_period, period_default);
        } else if (fluctuate == 1) {
            maxamplitude = maxamplitude_default;
            period = period_small;
        } else {
            maxamplitude = maxamplitude_default;
            period = period_default;
        }
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        float x = 0.0f, y = height / 2;
        if (mPath.isEmpty()) {
            mPath.reset();
            mPath.moveTo(x, y);
            float nextX, nextY;
            while (x < width) {
                if (x + period < width) {
                    nextX = x + period;
                } else {
                    nextX = width;
                }
                double coefficient = random.nextDouble() * 2 - 1;
                nextY = (float) (height / 2 + maxamplitude * coefficient * height / 2);
                mPath.quadTo(x, y, (x + nextX) / 2, (y + nextY) / 2);
                x = nextX;
                y = nextY;
            }
        }
        canvas.drawPath(mPath, mPaint);
    }
}
