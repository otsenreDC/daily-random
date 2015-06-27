package io.bananalabs.dailyrandom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by EDC on 6/15/15.
 */
public class DailyListView extends ListView {

    public DailyListView(Context context) {
        super(context);
    }

    public DailyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DailyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }
}
