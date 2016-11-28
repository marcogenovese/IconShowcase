/*
 * Copyright (c) 2016 Jahir Fiquitiva
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
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jahirfiquitiva.iconshowcase.utilities.color;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.utils.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.utils.Utils;

public class ColorUtils {

    @ColorInt
    private static int blendColors(@ColorInt int color1,
                                   @ColorInt int color2,
                                   @FloatRange(from = 0f, to = 1f) float ratio) {
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio);
        float r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio);
        float g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio);
        float b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @SuppressWarnings("SameParameterValue")
    @ColorInt
    public static int adjustAlpha(@ColorInt int color, @FloatRange(from = 0.0, to = 1.0) float
            factor) {
        float a = Color.alpha(color) * factor;
        float r = Color.red(color);
        float g = Color.green(color);
        float b = Color.blue(color);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int changeAlpha(@ColorInt int color, @FloatRange(from = 0.0, to = 1.0) float
            newAlpha) {
        float a = 255 * newAlpha;
        float r = Color.red(color);
        float g = Color.green(color);
        float b = Color.blue(color);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int shiftColor(@ColorInt int color, @FloatRange(from = 0.0f, to = 2.0f) float
            by) {
        if (by == 1f) return color;
        int alpha = Color.alpha(color);
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= by; // value component
        return (alpha << 24) + (0x00ffffff & Color.HSVToColor(hsv));
    }

    @ColorInt
    public static int darkenColor(@ColorInt int color) {
        return shiftColor(color, 0.9f);
    }

    @ColorInt
    public static int lightenColor(@ColorInt int color) {
        return shiftColor(color, 1.1f);
    }

    public static boolean isLightColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        if (palette.getSwatches().size() > 0) {
            return isLightColor(palette);
        }
        return isLightColor(palette);
    }

    private static boolean isLightColor(Palette palette) {
        return isLightColor(ColorUtils.getPaletteSwatch(palette).getRgb());
    }

    public static boolean isLightColor(@ColorInt int color) {
        return getColorDarkness(color) < 0.4;
    }

    public static double getColorDarkness(@ColorInt int color) {
        if (color == Color.BLACK) return 1.0;
        else if (color == Color.WHITE || color == Color.TRANSPARENT) return 0.0;
        return (1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue
                (color)) / 255);
    }

    @ColorInt
    public static int shiftLightTextColor(@ColorInt int textColor) {
        if (isLightColor(textColor)) {
            textColor = shiftColor(textColor, (float) getColorDarkness(textColor));
        }
        return textColor;
    }

    @ColorInt
    public static int shiftDarkTextColor(@ColorInt int textColor) {
        if (!isLightColor(textColor)) {
            textColor = shiftColor(textColor, (float) getColorDarkness(textColor));
        }
        return textColor;
    }

    public static Drawable getTintedDrawable(@NonNull Context context, String name) {
        final int light = ContextCompat.getColor(context, R.color.drawable_tint_dark);
        final int dark = ContextCompat.getColor(context, R.color.drawable_tint_light);
        return ColorUtils.getTintedIcon(context,
                Utils.getIconResId(context.getResources(), context.getPackageName(), name),
                ThemeUtils.darkTheme ? light : dark);
    }

    public static Drawable getTintedIcon(@NonNull Context context, @DrawableRes int drawable,
                                         @ColorInt int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getTintedIcon(ContextCompat.getDrawable(context, drawable), color);
            } else {
                Drawable icon = VectorDrawableCompat.create(context.getResources(), drawable, null);
                return getTintedIcon(icon, color);
            }
        } catch (Resources.NotFoundException ex) {
            return getTintedIcon(ContextCompat.getDrawable(context, R.drawable.iconshowcase_logo)
                    , color);
        }
    }

    @CheckResult
    @Nullable
    public static Drawable getTintedIcon(Drawable drawable, int color) {
        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (drawable instanceof VectorDrawable) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
                drawable = DrawableCompat.wrap(drawable.mutate());
            } else {
                drawable = DrawableCompat.wrap(drawable);
            }
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(drawable, color);
            return drawable;
        } else {
            return null;
        }
    }

    public static void setupToolbarIconsAndTextsColors(final Context context, AppBarLayout appbar,
                                                       final Toolbar toolbar) {

        final int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(context, R.color.toolbar_text_dark) :
                ContextCompat.getColor(context, R.color.toolbar_text_light);

        final int defaultIconsColor = ContextCompat.getColor(context, android.R.color.white);

        if (appbar != null) {
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @SuppressWarnings("ResourceAsColor")
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    double ratio = Utils.round(((double) (verticalOffset * -1) / 255.0), 1);
                    if (ratio > 1) {
                        ratio = 1;
                    } else if (ratio < 0) {
                        ratio = 0;
                    }
                    int paletteColor = ColorUtils.blendColors(defaultIconsColor, iconsColor,
                            (float) ratio);
                    if (toolbar != null) {
                        // Collapsed offset = -352
                        ToolbarColorizer.colorizeToolbar(toolbar, paletteColor);
                    }
                }
            });
        }
    }

    public static Palette.Swatch getPaletteSwatch(Bitmap bitmap) {
        //Test areas of 10 and 50*50
        return getPaletteSwatch(Palette.from(bitmap).resizeBitmapArea(50 * 50).generate());
    }

    private static Palette.Swatch getPaletteSwatch(Palette palette) {
        if (palette != null) {
            if (palette.getVibrantSwatch() != null) {
                return palette.getVibrantSwatch();
            } else if (palette.getMutedSwatch() != null) {
                return palette.getMutedSwatch();
            } else if (palette.getDarkVibrantSwatch() != null) {
                return palette.getDarkVibrantSwatch();
            } else if (palette.getDarkMutedSwatch() != null) {
                return palette.getDarkMutedSwatch();
            } else if (palette.getLightVibrantSwatch() != null) {
                return palette.getLightVibrantSwatch();
            } else if (palette.getLightMutedSwatch() != null) {
                return palette.getLightMutedSwatch();
            } else if (!palette.getSwatches().isEmpty()) {
                return getPaletteSwatch(palette.getSwatches());
            }
        }
        return null;
    }

    private static Palette.Swatch getPaletteSwatch(List<Palette.Swatch> swatches) {
        if (swatches == null) return null;
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

    public static int getColorFromIcon(final Context context, Drawable icon) {
        if (icon == null) return getAccentColor(context);
        Palette palette = Palette.from(Utils.drawableToBitmap(icon)).generate();
        return getColorFromIcon(context, palette);
    }

    public static int getColorFromIcon(Context context, Palette palette) {
        int resultColor = getBetterColor(context, palette.getVibrantColor(0));
        if (resultColor == 0) {
            resultColor = getBetterColor(context, palette.getMutedColor(0));
        }
        if (resultColor == 0) {
            resultColor = getAccentColor(context);
        }
        return resultColor;
    }

    public static int getBetterColor(Context context, @ColorInt int color) {
        if (color == 0) return 0;
        return ThemeUtils.darkTheme ? shiftDarkTextColor(color) : shiftLightTextColor(color);
    }

    public static int getAccentColor(Context context) {
        return ContextCompat.getColor(context, ThemeUtils.darkTheme ?
                R.color.dark_theme_accent : R.color.light_theme_accent);
    }

    public static int getMaterialPrimaryTextColor(boolean dark) {
        if (dark) {
            // 100%
            return Color.parseColor("#ffffffff");
        } else {
            // 87%
            return Color.parseColor("#de000000");
        }
    }

    public static int getMaterialSecondaryTextColor(boolean dark) {
        if (dark) {
            // 70%
            return Color.parseColor("#b3ffffff");
        } else {
            // 54%
            return Color.parseColor("#8a000000");
        }
    }

    public static int getMaterialTertiaryColor(boolean dark) {
        if (dark) {
            // 50%
            return Color.parseColor("#80ffffff");
        } else {
            // 38%
            return Color.parseColor("#61000000");
        }
    }

}