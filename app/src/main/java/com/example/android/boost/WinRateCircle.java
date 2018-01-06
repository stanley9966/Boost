package com.example.android.boost;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class WinRateCircle extends View {


    private int centerPointX;
    private int centerPointY;

    private int innerCircleColor;
    private int innerCircleRadius;

    //private int outerCircleColor;
    //private int outerCircleRadius;

    private int strokeColor;
    private int strokeWidth;
    private int strokeLength;
    private int distanceBetweenStrokes;

    private int textColor;
    private int textSize;

    // TODO
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

    public WinRateCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeAttributes(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // setting the center here
        centerPointX = getWidth()/2;
        centerPointY = getHeight()/2;

        super.onDraw(canvas);
        drawInnerCircle(paint, canvas);
        //drawOuterCircle(paint, canvas);
        drawStrokes(paint, canvas);
    }

    private void drawStrokes(Paint paint, Canvas canvas) {
        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);

        // on and off is the float array
        paint.setPathEffect(new DashPathEffect(new float[]{strokeLength, distanceBetweenStrokes}, 0));

        int leftEdge = centerPointX - innerCircleRadius;
        int rightEdge = centerPointX + innerCircleRadius;
        int topEdge = centerPointY + innerCircleRadius;
        int bottomEdge = centerPointY - innerCircleRadius;

        RectF rectF = new RectF(leftEdge, topEdge, rightEdge, bottomEdge);
        canvas.drawArc(rectF, 270f, 360f, false, paint);


    }

//    private void drawOuterCircle(Paint paint, Canvas canvas) {
//        paint.setDither(true);
//        paint.setColor(outerCircleColor);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(5f);
//        canvas.drawCircle(centerPointX, centerPointY, outerCircleRadius, paint);
//    }

    private void drawInnerCircle(Paint paint, Canvas canvas) {
        paint.setDither(true);
        paint.setColor(innerCircleColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);
        canvas.drawCircle(centerPointX, centerPointY, innerCircleRadius, paint);
    }

    // TODO: make color defaults based on color schemes in general, center points chosen dynamically, number of dashes
    private void initializeAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WinRateCircle);

        //centerPointX = typedArray.getInteger(R.styleable.WinRateCircle_centerPointX, 500);
        //centerPointY = typedArray.getInteger(R.styleable.WinRateCircle_centerPointY, 200);

        innerCircleColor = typedArray.getColor(R.styleable.WinRateCircle_innerCircleColor, Color.parseColor("#CBF5FF"));
        innerCircleRadius = typedArray.getInteger(R.styleable.WinRateCircle_innerCircleRadius, 100);

        //outerCircleColor = typedArray.getColor(R.styleable.WinRateCircle_outerCircleColor, Color.parseColor("black"));
        //outerCircleRadius = typedArray.getInteger(R.styleable.WinRateCircle_outerCircleRadius, 205);

        strokeColor = typedArray.getColor(R.styleable.WinRateCircle_strokeColor, Color.parseColor("black"));
        strokeWidth = typedArray.getInteger(R.styleable.WinRateCircle_strokeWidth, 20);
        strokeLength = typedArray.getInteger(R.styleable.WinRateCircle_strokeLength, 30);
        distanceBetweenStrokes = typedArray.getInteger(R.styleable.WinRateCircle_distanceBetweenStrokes, 10);

        textColor = typedArray.getColor(R.styleable.WinRateCircle_textColor, Color.parseColor("#75F061"));
        textSize = typedArray.getInteger(R.styleable.WinRateCircle_textSize, 20);

        typedArray.recycle();
    }
}
