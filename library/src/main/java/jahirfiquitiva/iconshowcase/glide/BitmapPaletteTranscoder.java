/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class BitmapPaletteTranscoder implements ResourceTranscoder<Bitmap, BitmapPaletteWrapper> {
    private final BitmapPool bitmapPool;

    public BitmapPaletteTranscoder(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public BitmapPaletteTranscoder(BitmapPool bitmapPool) {
        this.bitmapPool = bitmapPool;
    }

    @Override
    public Resource<BitmapPaletteWrapper> transcode(Resource<Bitmap> bitmapResource) {
        Bitmap bitmap = bitmapResource.get();
        BitmapPaletteWrapper bitmapPaletteWrapper = new BitmapPaletteWrapper(bitmap,
                Palette.from(bitmap).generate());
        return new BitmapPaletteResource(bitmapPaletteWrapper, bitmapPool);
    }

    @Override
    public String getId() {
        return "BitmapPaletteTranscoder.jahirfiquitiva.iconshowcase.glide";
    }
}