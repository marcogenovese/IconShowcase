/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.glide;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class BitmapPaletteWrapper {
    private final Bitmap mBitmap;
    private final Palette mPalette;

    public BitmapPaletteWrapper(Bitmap bitmap, Palette palette) {
        mBitmap = bitmap;
        mPalette = palette;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Palette getPalette() {
        return mPalette;
    }
}