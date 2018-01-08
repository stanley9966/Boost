package com.example.android.boost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WinRateCircle extends View {

    private List<individualStrokeObject> strokeObjects;

    private Paint paint;
    private Paint textPaint;

    private RectF rectF;

    private int centerPointX;
    private int centerPointY;

    private int innerCircleRadius;

    private int strokeWidth;

    public WinRateCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = 30;   // TODO: Make these two responsive
        innerCircleRadius = 150;

        ArrayList<Long> mMatchArrayList = ConnectToServerTask.mMatchArrayList;
        HashMap<Long, Boolean> mGameIdsAndWinLossMap = ConnectToServerTask.mGameIdsAndWinLossMap;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);     // TODO: should be based on color scheme
        textPaint.setStrokeWidth(1);         // TODO: RESPONSIVE
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(75);
        textPaint.setStyle(Paint.Style.FILL);

        // initializing strokeObjects
        strokeObjects = new ArrayList<>();

        int numGames = mMatchArrayList.size();
        float amountOfCircle = 360/numGames;

        Integer color;
        int i = 0;
        Iterator<Long> itr = mMatchArrayList.iterator();
        while(itr.hasNext()) {
            Long key = itr.next();
            // if won
            if (mGameIdsAndWinLossMap.get(key)) {
                color = Color.GREEN;   // green
            } else {
                color = ContextCompat.getColor(getContext(), R.color.lightBackgroundColorRed);    // red
            }
            strokeObjects.add(new individualStrokeObject(i, amountOfCircle, color));
            i++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // setting the center
        centerPointX = getWidth()/2;
        centerPointY = getHeight()/2;

        drawStrokes(canvas);
        canvas.drawText(Integer.toString(ConnectToServerTask.numWins*100/ConnectToServerTask.numMatches) + "%",
                centerPointX,
                centerPointY+(textPaint.getTextSize()/2),
                textPaint);
    }

    private void drawStrokes(Canvas canvas) {
        for (individualStrokeObject stroke : strokeObjects) {
            stroke.drawIndividualStrokes(canvas);
        }
    }

    private class individualStrokeObject {
        float amountToStartAt;
        float amountCircle;
        Integer color;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        public individualStrokeObject(int index, float amount, Integer color) {
            this.color = color;

            // changing amount to usable values
            this.amountCircle = amount;

            this.amountToStartAt = (index * amount) + 270;

            if (this.amountToStartAt >= 360) this.amountToStartAt -= 360;

            this.paint.setColor(color);   // parse before giving this
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
        }

        private void drawIndividualStrokes(Canvas canvas) {
            paint.setStrokeWidth(strokeWidth);

            int leftEdge = centerPointX - innerCircleRadius;
            int rightEdge = centerPointX + innerCircleRadius;
            int topEdge = centerPointY - innerCircleRadius;
            int bottomEdge = centerPointY + innerCircleRadius;

            rectF = new RectF(leftEdge, topEdge, rightEdge, bottomEdge);

           // canvas.drawCircle(centerPointX, centerPointY, innerCircleRadius, paint);
            canvas.drawArc(rectF, amountToStartAt, amountCircle, false, paint);
        }
    }
}
