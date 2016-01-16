package jahirfiquitiva.apps.iconshowcase.views;

/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import android.content.Context;
        import android.support.v7.widget.LinearLayoutManager;
        import android.util.AttributeSet;
        import android.view.View;

public class SimpleFastScrollRecyclerView extends BaseRecyclerView {
    private ScrollPositionState mScrollPosState = new ScrollPositionState();

    public SimpleFastScrollRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleFastScrollRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Returns the available scroll bar height:
     *   AvailableScrollBarHeight = Total height of the visible view - thumb height
     */
    protected int getAvailableScrollBarHeight() {
        int visibleHeight = getHeight() - mBackgroundPadding.top - mBackgroundPadding.bottom;
        int availableScrollBarHeight = visibleHeight - mScrollbar.getThumbHeight();
        return availableScrollBarHeight;
    }

    /**
     * Maps the touch (from 0..1) to the adapter position that should be visible.
     */
    @Override
    public String scrollToPositionAtProgress(float touchFraction) {
        int rowCount = getAdapter().getItemCount();
        if (rowCount == 0) {
            return "";
        }

        // Stop the scroller if it is scrolling
        stopScroll();

        getCurScrollState(mScrollPosState);
        float pos = rowCount * touchFraction;
        int availableScrollHeight = getAvailableScrollHeight(rowCount, mScrollPosState.rowHeight, 1);
        LinearLayoutManager layoutManager = ((LinearLayoutManager) getLayoutManager());
        layoutManager.scrollToPositionWithOffset(0, (int) -(availableScrollHeight * touchFraction));

        int posInt = (int) ((touchFraction == 1)? pos -1 : pos);
        String result = "";
        if (getAdapter() instanceof FastScrollTitleSectionProvider) {
            result = ((FastScrollTitleSectionProvider) getAdapter()).getSectionTitle(posInt);
        }
        return result;
    }

    /**
     * Updates the bounds for the scrollbar.
     */
    @Override
    public void onUpdateScrollbar() {

        // Skip early if not bound.
        if (getAdapter() == null) {
            return;
        }

        // Skip early if empty
        int rowCount = getAdapter().getItemCount();
        if (rowCount == 0) {
            mScrollbar.setScrollbarThumbOffset(-1, -1);
            return;
        }

        // Skip early if, there no child laid out in the container.
        getCurScrollState(mScrollPosState);
        if (mScrollPosState.rowIndex < 0) {
            mScrollbar.setScrollbarThumbOffset(-1, -1);
            return;
        }

        synchronizeScrollBarThumbOffsetToViewScroll(mScrollPosState, rowCount, 1);
    }

    /**
     * Returns the current scroll state.
     */
    protected void getCurScrollState(ScrollPositionState stateOut) {
        stateOut.rowIndex = -1;
        stateOut.rowTopOffset = -1;
        stateOut.rowHeight = -1;

        // Skip early if not bound
        if (getAdapter() == null) {
            return;
        }

        // Skip early if empty
        int rowCount = getAdapter().getItemCount();
        if (rowCount == 0) {
            return;
        }
        View child = getChildAt(0);
        int position = getChildAdapterPosition(child);

        stateOut.rowIndex = position;
        stateOut.rowTopOffset = getLayoutManager().getDecoratedTop(child);
        stateOut.rowHeight = child.getHeight();
    }

    public interface FastScrollTitleSectionProvider {
        String getSectionTitle(int position);
    }
}
