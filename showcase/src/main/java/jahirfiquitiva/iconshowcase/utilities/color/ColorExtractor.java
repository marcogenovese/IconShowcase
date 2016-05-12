/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.utilities.color;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class ColorExtractor {

    public static void setupToolbarIconsAndTextsColors(Context context, AppBarLayout appbar,
                                                       final Toolbar toolbar, final Bitmap bitmap,
                                                       boolean forViewer) {

        final int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(context, R.color.toolbar_text_dark) :
                ContextCompat.getColor(context, R.color.toolbar_text_light);

        int paletteGeneratedColor;

        if (context.getResources().getBoolean(R.bool.use_palette_api_in_toolbar)) {
            paletteGeneratedColor = getIconsColorFromBitmap(bitmap, context, forViewer);
            if (paletteGeneratedColor == 0 && bitmap != null) {
                if (ColorUtils.isDark(bitmap)) {
                    paletteGeneratedColor = Color.parseColor("#59ffffff");
                } else {
                    paletteGeneratedColor = Color.parseColor("#59000000");
                }
            }
        } else {
            paletteGeneratedColor = Color.parseColor("#8cffffff");
        }

        final int finalPaletteGeneratedColor = paletteGeneratedColor;

        if (appbar != null) {
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @SuppressWarnings("ResourceAsColor")
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    double alpha = round(((double) (verticalOffset * -1) / 288.0), 1);
                    int paletteColor = ColorUtils.blendColors(
                            finalPaletteGeneratedColor != 0 ? finalPaletteGeneratedColor : iconsColor,
                            iconsColor, alpha > 1.0 ? 1.0f : (float) alpha);
                    if (toolbar != null) {
                        ToolbarColorizer.colorizeToolbar(toolbar, paletteColor);
                    }
                }
            });
        }
    }

    public static int getIconsColorFromBitmap(Bitmap bitmap, Context context, boolean forViewer) {
        int color = 0;
        boolean isDark;

        if (bitmap != null) {

            boolean swatchNotNull = true;

            Palette palette = new Palette.Builder(bitmap)
                    .generate();

            isDark = ColorUtils.isDark(bitmap);

            Palette.Swatch swatch1, swatch2, swatch3, swatch4;

            if (isDark) {
                swatch1 = palette.getLightVibrantSwatch();
                swatch2 = palette.getLightMutedSwatch();
            } else {
                swatch1 = palette.getVibrantSwatch();
                swatch2 = palette.getMutedSwatch();
            }

            swatch3 = palette.getDarkVibrantSwatch();
            swatch4 = palette.getDarkMutedSwatch();

            if (swatch1 != null) {
                color = swatch1.getRgb();
            } else if (swatch2 != null) {
                color = swatch2.getRgb();
            } else if (swatch3 != null) {
                color = swatch3.getRgb();
            } else if (swatch4 != null) {
                color = swatch4.getRgb();
            } else {
                swatchNotNull = false;
            }

            if (swatchNotNull) {
                float[] values = getActualSValues(ColorUtils.S, forViewer);
                float colorAlpha = values[0], tintFactor = values[1];
                int colorToBlend =
                        ColorUtils.adjustAlpha(
                                ContextCompat.getColor(context,
                                        isDark ? android.R.color.white : android.R.color.black),
                                colorAlpha);
                color = ColorUtils.blendColors(color, colorToBlend, tintFactor);
            }

        }

        return color;
    }

    private static float[] getActualSValues(float s, boolean forViewer) {
        float[] values = new float[2];
        float alpha, factor;
        if (s < 0.51f) {
            alpha = (s + 1.0f) - (s * 3.0f);
            alpha += 0.2f;
        } else {
            alpha = ((s * 2.0f) - 1.0f) + 0.1f;
        }

        if (forViewer) {
            factor = 0.8f;
        } else {
            factor = 0.5f;
        }

        if (s < 0.0f) {
            alpha = 0.0f;
        } else if (s > 1.0f) {
            alpha = 1.0f;
        }

        values[0] = alpha;
        values[1] = factor;

        return values;
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int getPreferredColor(Drawable drawable, Context context, boolean allowAccent) {
        return getPreferredColor(Utils.drawableToBitmap(drawable), context, allowAccent);
    }

    public static int getPreferredColor(Bitmap bitmap, Context context, boolean allowAccent) {
        Palette.Swatch prominentColor = getProminentSwatch(bitmap);
        int accent = ContextCompat.getColor(context, ThemeUtils.darkTheme ?
                R.color.dark_theme_accent : R.color.light_theme_accent);
        return prominentColor != null ? prominentColor.getRgb() : allowAccent ? accent : 0;
    }

    public static Palette.Swatch getProminentSwatch(Drawable drawable) {
        return getProminentSwatch(Utils.drawableToBitmap(drawable));
    }

    public static Palette.Swatch getProminentSwatch(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        return getProminentSwatch(palette);
    }

    public static Palette.Swatch getProminentSwatch(Palette palette) {
        if (palette == null) return null;
        List<Palette.Swatch> swatches = getSwatchesList(palette);
        return Collections.max(swatches,
                new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch opt1, Palette.Swatch opt2) {
                        int a = opt1 == null ? 0 : opt1.getPopulation();
                        int b = opt2 == null ? 0 : opt2.getPopulation();
                        return a - b;
                    }
                });
    }

    public static Palette.Swatch getLessProminentSwatch(Drawable drawable) {
        return getLessProminentSwatch(Utils.drawableToBitmap(drawable));
    }

    public static Palette.Swatch getLessProminentSwatch(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        return getLessProminentSwatch(palette);
    }

    public static Palette.Swatch getLessProminentSwatch(Palette palette) {
        if (palette == null) return null;
        List<Palette.Swatch> swatches = getSwatchesList(palette);
        return Collections.max(swatches,
                new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch opt1, Palette.Swatch opt2) {
                        int a = opt1 == null ? 0 : opt1.getPopulation();
                        int b = opt2 == null ? 0 : opt2.getPopulation();
                        return b - a;
                    }
                });
    }

    private static List<Palette.Swatch> getSwatchesList(Palette palette) {
        List<Palette.Swatch> swatches = new ArrayList<>();

        Palette.Swatch vib = palette.getVibrantSwatch();
        Palette.Swatch vibLight = palette.getLightVibrantSwatch();
        Palette.Swatch vibDark = palette.getDarkVibrantSwatch();
        Palette.Swatch muted = palette.getMutedSwatch();
        Palette.Swatch mutedLight = palette.getLightMutedSwatch();
        Palette.Swatch mutedDark = palette.getDarkMutedSwatch();

        swatches.add(vib);
        swatches.add(vibLight);
        swatches.add(vibDark);
        swatches.add(muted);
        swatches.add(mutedLight);
        swatches.add(mutedDark);

        return swatches;
    }

}