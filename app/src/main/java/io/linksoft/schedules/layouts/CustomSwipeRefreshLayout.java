package io.linksoft.schedules.layouts;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {

    private int touchSlop;
    private float prevX;

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            prevX = MotionEvent.obtain(event).getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float eventX = event.getX();
            float xDiff = Math.abs(eventX - prevX);

            if (xDiff > touchSlop) return false;
        }

        return super.onInterceptTouchEvent(event);
    }

}
