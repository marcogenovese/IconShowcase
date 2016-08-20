/*
 * Copyright (c) 2016.  Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

package jahirfiquitiva.iconshowcase.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import jahirfiquitiva.iconshowcase.utilities.Utils;

public class StaggeredGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spanCount;
    private final int spacing;
    private boolean extrasAtLeft = false;

    public StaggeredGridSpacingItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount;
        int itemsCount = parent.getAdapter().getItemCount();
        int itemsWithoutExtraCedits = itemsCount - 5;

        if (position < spanCount) { // top edge
            outRect.top = spacing;
        }

        if (spanCount % 2 != 0) {
            if (spanCount > 2) {
                if (column == 0) {
                    // on first column, adjust. Left magically adjusts bottom, so adjust it too...
                    outRect.left = spacing;
                    outRect.right = spacing / 2;
                }

                if (column == spanCount - 1) {
                    // on last column, adjust. Right magically adjusts bottom, so adjust it too...
                    outRect.left = spacing / 2;
                    outRect.right = spacing;
                }
            } else {
                outRect.right = spacing;
                outRect.left = spacing;
            }
        } else {
            if (position <= itemsWithoutExtraCedits - 1) {
                outRect.right = spacing / 2;
                outRect.left = spacing / 2;

                if (column == 0) {
                    // on first column, adjust. Left magically adjusts bottom, so adjust it too...
                    outRect.left = spacing;
                }

                if (column == spanCount - 1) {
                    // on last column, adjust. Right magically adjusts bottom, so adjust it too...
                    outRect.right = spacing;
                }

                if ((position > spanCount - 1) && (itemsWithoutExtraCedits % 2 != 0) &&
                        (position == itemsWithoutExtraCedits - 1)) {
                    if (extrasAtLeft) {
                        outRect.right = spacing;
                        outRect.left = spacing / 2;
                    } else {
                        outRect.right = spacing / 2;
                        outRect.left = spacing;
                    }
                }
            } else {
                if (position == itemsWithoutExtraCedits) {
                    extrasAtLeft = column == 0;
                }

                if (extrasAtLeft) {
                    outRect.right = spacing / 2;
                    outRect.left = spacing;
                } else {
                    outRect.right = spacing;
                    outRect.left = spacing / 2;
                }
            }
        }

        outRect.bottom = spacing;

    }
}