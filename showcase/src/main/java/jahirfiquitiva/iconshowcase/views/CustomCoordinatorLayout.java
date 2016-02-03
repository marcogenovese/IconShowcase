package jahirfiquitiva.iconshowcase.views;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomCoordinatorLayout extends CoordinatorLayout {

    private boolean allowScroll = false;

    public CustomCoordinatorLayout(Context context) {
        super(context);
    }

    public CustomCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return allowScroll && super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return allowScroll && super.onInterceptTouchEvent(ev);
    }

    public boolean isScrollAllowed() {
        return allowScroll;
    }

    public void setScrollAllowed(boolean allowScroll) {
        this.allowScroll = allowScroll;
    }
}