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

    public static void setupToolbarIconsAndTextsColors(final Context context, AppBarLayout appbar,
                                                       final Toolbar toolbar, final Bitmap bitmap) {

        final int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(context, R.color.toolbar_text_dark) :
                ContextCompat.getColor(context, R.color.toolbar_text_light);

        final int finalPaletteGeneratedColor = getFinalGeneratedIconsColorFromPalette(bitmap,
                context.getResources().getBoolean(R.bool.use_palette_api_in_toolbar));

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
                        // Collapsed offset = -352
                        ToolbarColorizer.colorizeToolbar(toolbar, paletteColor);
                    }
                }
            });
        }
    }

    public static int getFinalGeneratedIconsColorFromPalette(Bitmap bitmap, boolean usePalette) {
        int generatedIconsColorFromPalette;
        if (usePalette) {
            generatedIconsColorFromPalette = getIconsColorFromBitmap(bitmap);
            if (generatedIconsColorFromPalette == 0) {
                if (bitmap != null) {
                    if (ColorUtils.isDark(bitmap)) {
                        generatedIconsColorFromPalette = Color.parseColor("#80ffffff");
                    } else {
                        generatedIconsColorFromPalette = Color.parseColor("#66000000");
                    }
                } else {
                    generatedIconsColorFromPalette = Color.parseColor("#99ffffff");
                }
            }
        } else {
            generatedIconsColorFromPalette = Color.parseColor("#99ffffff");
        }
        return generatedIconsColorFromPalette;
    }

    public static int getIconsColorFromBitmap(Bitmap bitmap) {
        int color = 0;
        if (bitmap != null) {
            Palette.Swatch swatch = getProminentSwatch(bitmap);
            if (swatch != null) {
                color = swatch.getBodyTextColor();
            }
        }
        return color;
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

    public static int getPreferredColor(Drawable drawable, Context context, boolean allowAccent, boolean forIcons) {
        return getPreferredColor(Utils.drawableToBitmap(drawable), context, allowAccent, forIcons);
    }

    public static int getPreferredColor(Bitmap bitmap, Context context, boolean allowAccent) {
        Palette.Swatch prominentColor = getProminentSwatch(bitmap);
        int accent = ContextCompat.getColor(context, ThemeUtils.darkTheme ?
                R.color.dark_theme_accent : R.color.light_theme_accent);
        return prominentColor != null ? prominentColor.getRgb() : allowAccent ? accent : 0;
    }

    public static int getPreferredColor(Bitmap bitmap, Context context, boolean allowAccent, boolean forIcons) {
        Palette.Swatch prominentColor = getProminentSwatch(bitmap);
        int accent = ContextCompat.getColor(context, ThemeUtils.darkTheme ?
                R.color.dark_theme_accent : R.color.light_theme_accent);
        return prominentColor != null ? prominentColor.getRgb() : allowAccent ? accent : 0;
    }

    public static Palette.Swatch getProminentSwatch(Drawable drawable) {
        return getProminentSwatch(Utils.drawableToBitmap(drawable));
    }

    public static Palette.Swatch getProminentSwatch(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).clearFilters().generate();
        return getProminentSwatch(palette);
    }

    public static Palette.Swatch getProminentSwatch(Palette palette) {
        if (palette == null) return null;
        List<Palette.Swatch> swatches = getSwatchesList(palette, false);
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
        Palette palette = Palette.from(bitmap).clearFilters().generate();
        return getLessProminentSwatch(palette);
    }

    public static Palette.Swatch getLessProminentSwatch(Palette palette) {
        if (palette == null) return null;
        List<Palette.Swatch> swatches = getSwatchesList(palette, false);
        return Collections.min(swatches,
                new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch opt1, Palette.Swatch opt2) {
                        int a = opt1 == null ? 0 : opt1.getPopulation();
                        int b = opt2 == null ? 0 : opt2.getPopulation();
                        return a - b;
                    }
                });
    }

    private static List<Palette.Swatch> getSwatchesList(Palette palette, boolean forIcons) {
        List<Palette.Swatch> swatches = new ArrayList<>();

        Palette.Swatch vib = palette.getVibrantSwatch();
        Palette.Swatch vibLight = palette.getLightVibrantSwatch();
        Palette.Swatch vibDark = palette.getDarkVibrantSwatch();

        Palette.Swatch muted = palette.getMutedSwatch();
        Palette.Swatch mutedLight = palette.getLightMutedSwatch();
        Palette.Swatch mutedDark = palette.getDarkMutedSwatch();

        swatches.add(vib);

        if (forIcons) {
            if (ThemeUtils.darkTheme) {
                swatches.add(vibLight);
            } else {
                swatches.add(vibDark);
            }
        } else {
            swatches.add(vibLight);
            swatches.add(vibDark);
        }

        swatches.add(muted);

        if (forIcons) {
            if (ThemeUtils.darkTheme) {
                swatches.add(mutedLight);
            } else {
                swatches.add(mutedDark);
            }
        } else {
            swatches.add(mutedLight);
            swatches.add(mutedDark);
        }

        return swatches;
    }

}