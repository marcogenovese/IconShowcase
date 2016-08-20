/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.glide;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;

import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;

public abstract class WallpaperTarget extends BitmapPaletteTarget {

    public WallpaperTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        super.onLoadFailed(e, errorDrawable);
    }

    @Override
    public void onResourceReady(BitmapPaletteWrapper resource, GlideAnimation<? super BitmapPaletteWrapper> glideAnimation) {
        super.onResourceReady(resource, glideAnimation);
        onColorReady(ColorUtils.getPaletteSwatch(resource.getPalette()).getRgb());
    }

    public abstract void onColorReady(int color);
}