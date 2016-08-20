/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;

import jahirfiquitiva.iconshowcase.models.WallpaperItem;

public class WallpaperGlideRequest {

    public static class Builder {
        final RequestManager requestManager;
        final WallpaperItem wallpaper;

        public static Builder from(@NonNull RequestManager requestManager, WallpaperItem wallpaper) {
            return new Builder(requestManager, wallpaper);
        }

        private Builder(@NonNull RequestManager requestManager, WallpaperItem wallpaper) {
            this.requestManager = requestManager;
            this.wallpaper = wallpaper;
        }

        public PaletteBuilder generatePalette(Context context) {
            return new PaletteBuilder(this, context);
        }

        public BitmapBuilder asBitmap() {
            return new BitmapBuilder(this);
        }

        public DrawableRequestBuilder<GlideDrawable> build() {
            //noinspection unchecked
            return createBaseRequest(requestManager, wallpaper);
        }
    }

    public static class BitmapBuilder {
        private final Builder builder;

        public BitmapBuilder(Builder builder) {
            this.builder = builder;
        }

        public BitmapRequestBuilder<?, Bitmap> build() {
            //noinspection unchecked
            return createBaseRequest(builder.requestManager, builder.wallpaper).asBitmap();
        }
    }

    public static class PaletteBuilder {
        final Context context;
        private final Builder builder;

        public PaletteBuilder(Builder builder, Context context) {
            this.builder = builder;
            this.context = context;
        }

        public BitmapRequestBuilder<?, BitmapPaletteWrapper> build() {
            //noinspection unchecked
            return createBaseRequest(builder.requestManager, builder.wallpaper).asBitmap();
        }
    }

    public static DrawableTypeRequest createBaseRequest(RequestManager requestManager, WallpaperItem wallpaper) {
        //TODO: Do whatever is needed for Glide to load thumbnails properly
        if (!(wallpaper.getWallThumbUrl().equals("null"))) {
            return (DrawableTypeRequest) requestManager.load(wallpaper.getWallURL())
                    .thumbnail(createBaseThumbnailRequest(requestManager, wallpaper));
        } else {
            return (DrawableTypeRequest) requestManager.load(wallpaper.getWallURL()).thumbnail(0.4f);
        }
    }

    public static DrawableTypeRequest createBaseThumbnailRequest(RequestManager requestManager, WallpaperItem wallpaper) {
        return (DrawableTypeRequest) requestManager.load(wallpaper.getWallURL()).thumbnail(0.3f);
    }

}