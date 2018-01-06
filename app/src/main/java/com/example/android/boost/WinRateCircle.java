package com.example.android.boost;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class WinRateCircle extends View {

    private List<individualStrokeObject> strokeObjects;

    private ArrayList<Long> mMatchArrayList;
    private HashMap<Long, Boolean> mGameIdsAndWinLossMap;

    private int centerPointX;
    private int centerPointY;

    private int innerCircleRadius;

    private int strokeWidth;

    public WinRateCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        strokeWidth = 30;   // TODO: Make these two responsive
        innerCircleRadius = 200;

        QueryResultsScreen parentScreen = (QueryResultsScreen) context;
       // QueryResultsScreen parentScreen = ((QueryResultsScreen) getActivity());
//        QueryResultsScreen parentScreen = ((QueryResultsScreen)getContext());
        mMatchArrayList = parentScreen.getmMatchArrayList();
        mGameIdsAndWinLossMap  = parentScreen.getmGameIdsAndWinLossMap();

        // initializing strokeObjects
        strokeObjects = new ArrayList<>();
        if (mMatchArrayList == null) System.out.println("null");
        int numGames = mMatchArrayList.size();
        float percent = 1/numGames;

        Integer color;
        int i = 1;
        Iterator<Long> itr = mMatchArrayList.iterator();
        while(itr.hasNext()) {
            Long key = itr.next();
            float startingPercent = 0 + (i*percent);
            // if won
            if (mGameIdsAndWinLossMap.get(key)) {
                color = Color.parseColor("#09FF00");   // green
            } else {
                color = Color.parseColor("FF0000");
            }
            strokeObjects.add(new individualStrokeObject(startingPercent, percent, color));
            i++;
        }
        System.out.println("end of ctor");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // setting the center
        centerPointX = getWidth()/2;
        centerPointY = getHeight()/2;

        drawStrokes(canvas);
    }

    private void drawStrokes(Canvas canvas) {
        for (individualStrokeObject stroke : strokeObjects) {
            stroke.drawIndividualStrokes(canvas);
        }
    }

    // used to dynamically set number of split up sections and number of colors needed
    private void setStrokeObjects(List<individualStrokeObject> strokeObjects) {
        this.strokeObjects = strokeObjects;
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    private class individualStrokeObject {
        float percentToStartAt;
        float percentOfCircle;
        Integer color;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        public individualStrokeObject(float percentToStartAt, float percentOfCircle, Integer color) {
            this.percentToStartAt = percentToStartAt;
            this.percentOfCircle = percentOfCircle;
            this.color = color;

            // changing percentOfCircle to usable values
            if(this.percentOfCircle < 0 || this.percentOfCircle > 100){
                this.percentOfCircle = 100; //Default to 100%
            }
            this.percentOfCircle = (float)((360 * (percentOfCircle + 0.1)) / 100);

            // changing percentToStartAt to usable values
            if(this.percentToStartAt < 0 || this.percentToStartAt > 100){
                this.percentToStartAt = 0;
            }
            this.percentToStartAt = (float)((360 * (percentToStartAt - 0.1)) / 100) - 90;

            this.paint.setColor(color);   // parse before giving this
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.STROKE);
        }

        private void drawIndividualStrokes(Canvas canvas) {
            paint.setStrokeWidth(strokeWidth);

            int leftEdge = centerPointX - innerCircleRadius;
            int rightEdge = centerPointX + innerCircleRadius;
            int topEdge = centerPointY + innerCircleRadius;
            int bottomEdge = centerPointY - innerCircleRadius;

            RectF rectF = new RectF(leftEdge, topEdge, rightEdge, bottomEdge);
            canvas.drawArc(rectF, percentToStartAt, percentOfCircle, false, paint);
        }
    }
}
