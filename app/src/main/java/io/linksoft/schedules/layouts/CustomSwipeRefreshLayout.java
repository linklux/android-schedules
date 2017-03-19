package io.linksoft.schedules.layouts;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    private float mPrevX;

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPrevX = MotionEvent.obtain(event).getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float eventX = event.getX();
            float xDiff = Math.abs(eventX - mPrevX);

            if (xDiff > mTouchSlop) return false;
        }

        return super.onInterceptTouchEvent(event);
    }

}
