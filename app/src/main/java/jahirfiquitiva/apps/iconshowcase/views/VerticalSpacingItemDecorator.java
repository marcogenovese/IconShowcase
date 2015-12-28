package jahirfiquitiva.apps.iconshowcase.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by JAHIR on 03/07/2015.
 */

public class VerticalSpacingItemDecorator extends RecyclerView.ItemDecoration {

    private int space;
    private int lateralSpace = 0;
    private boolean withLateralSpace = false;

    public VerticalSpacingItemDecorator(int space, boolean withLateralSpace) {
        this.space = space;
        this.withLateralSpace = withLateralSpace;
    }

    public VerticalSpacingItemDecorator(int space, int lateralSpace) {
        this.space = space;
        this.withLateralSpace = true;
        this.lateralSpace = lateralSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (withLateralSpace) {
            if (lateralSpace != 0) {
                outRect.left = lateralSpace;
                outRect.right = lateralSpace;
            } else {
                outRect.left = space;
                outRect.right = space;
            }

            outRect.bottom = space;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = space;
        } else {
            outRect.bottom = space;
            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0)
                outRect.top = space;
        }
    }

}

