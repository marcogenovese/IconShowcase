package jahirfiquitiva.apps.iconshowcase.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by JAHIR on 30/06/2015.
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int position = parent.getChildAdapterPosition(view); // item position
//        int row = position % (parent.getAdapter().getItemCount() / spanCount);
//        int column = position % spanCount; // item column
        outRect.left += spacing;
        outRect.right += spacing;
        outRect.top += spacing;
        outRect.bottom += spacing;
    }
}
