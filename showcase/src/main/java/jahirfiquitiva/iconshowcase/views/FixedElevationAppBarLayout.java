/*
 *
 */

package jahirfiquitiva.iconshowcase.views;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import jahirfiquitiva.iconshowcase.R;

public class FixedElevationAppBarLayout extends AppBarLayout {

    /**
     * The pixel elevation of the {@link FixedElevationAppBarLayout}.
     */
    private int fElevation;

    public FixedElevationAppBarLayout(Context context) {
        super(context);
        setupElevation();
    }

    public FixedElevationAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupElevation();
    }

    @Override
    public void setElevation(float ignored) {
        super.setElevation(fElevation);
    }

    /**
     * A method for setting up the elevation. Improves performance if only done once.
     */
    private void setupElevation() {
        fElevation = dpToPx(getResources().getInteger(R.integer.toolbar_elevation));
    }

    /**
     * A helper method for converting dps to pixels.
     *
     * @param dp The dp parameters
     * @return The pixel-converted result
     */
    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
