/*
 *
 */

package jahirfiquitiva.iconshowcase.models;

import android.graphics.Bitmap;

public class ZooperWidget {

    private final String name;
    private final Bitmap preview;

    public ZooperWidget(String name, Bitmap preview) {
        this.name = name;
        this.preview = preview;
    }

    public String getName() {
        return this.name;
    }

    public Bitmap getPreview() {
        return this.preview;
    }

}
