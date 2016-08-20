/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.glide;

import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class BitmapPaletteTarget extends ImageViewTarget<BitmapPaletteWrapper> {
    public BitmapPaletteTarget(ImageView view) {
        super(view);
    }

    @Override
    protected void setResource(BitmapPaletteWrapper bitmapPaletteWrapper) {
        view.setImageBitmap(bitmapPaletteWrapper.getBitmap());
    }
}