package jahirfiquitiva.iconshowcase.config;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import jahirfiquitiva.iconshowcase.BuildConfig;
import jahirfiquitiva.iconshowcase.R;

/**
 * Created by Allan Wang on 2016-08-19.
 * <p>
 * With reference to Polar
 * https://github.com/afollestad/polar-dashboard/blob/master/app/src/main/java/com/afollestad/polar/config/Config.java
 */
public class Config implements IConfig {

    private Config(@Nullable Context context) {
        mR = null;
        mContext = context;
        if (context != null)
            mR = context.getResources();
    }

    private static Config mConfig;
    private Context mContext;
    private Resources mR;

    public static void init(@NonNull Context context) {
        mConfig = new Config(context);
    }

    public static void setContext(Context context) {
        if (mConfig != null) {
            mConfig.mContext = context;
            if (context != null)
                mConfig.mR = context.getResources();
        }
    }

    private void destroy() {
        mContext = null;
        mR = null;
    }

    public static void deinit() {
        if (mConfig != null) {
            mConfig.destroy();
            mConfig = null;
        }
    }

    @NonNull
    public static IConfig get() {
        if (mConfig == null)
            return new Config(null); // shouldn't ever happen, but avoid crashes
        return mConfig;
    }

    public static boolean getBool(@BoolRes int id) {
        return get().getBool2(id);
    }

    public static String getString(@StringRes int id) {
        return get().getString2(id);
    }

    public static String[] getStringArray(@ArrayRes int id) {
        return get().getStringArray2(id);
    }

    public static int getInt(@IntegerRes int id) {
        return get().getInt2(id);
    }

    // Getters

    private Preference prefs() {
        return new Preference(mContext);
    }

    @Override
    public boolean getBool2(@BoolRes int id) {
        return mR != null && mR.getBoolean(id);
    }

    @Override
    @Nullable
    public String getString2(@StringRes int id) {
        if (mR == null) return null;
        return mR.getString(id);
    }

    @Override
    @Nullable
    public String[] getStringArray2(@ArrayRes int id) {
        if (mR == null) return null;
        return mR.getStringArray(id);
    }

    @Override
    public int getInt2(@IntegerRes int id) {
        if (mR == null) return 0;
        return mR.getInteger(id);
    }

    @Override
    public boolean hasString(@StringRes int id) {
        String s = getString2(id);
        return (s != null && !s.isEmpty());
    }

    @Override
    public boolean hasArray(@ArrayRes int id) {
        String[] s = getStringArray2(id);
        return (s != null && s.length != 0);
    }

    @Override
    public boolean allowDebugging() {
        return BuildConfig.DEBUG || mR == null || mR.getBoolean(R.bool.debugging);
    }

    @Override
    public int appTheme() {
        return getInt2(R.integer.app_theme);
    }

    @Override
    public boolean hasGoogleDonations() {
        return hasArray(R.array.google_donations_catalog) && hasArray(R.array.consumable_google_donation_items) && hasArray(R.array.nonconsumable_google_donation_items);
    }

    @Override
    public boolean hasPaypal() {
        return hasString(R.string.paypal_user);
    }

    @NonNull
    @Override
    public String getPaypalCurrency() {
        String s = getString(R.string.paypal_currency_code);
        if (s == null || s.length() != 3) return "USD"; //TODO log currency issue
        return s;
    }

    @Override
    public boolean devOptions() {
        return getBool2(R.bool.dev_options);
    }

    @Override
    public boolean shuffleToolbarIcons() {
        return getBool2(R.bool.shuffle_toolbar_icons);
    }

    @Override
    public boolean userWallpaperInToolbar() {
        return getBool2(R.bool.enable_user_wallpaper_in_toolbar);
    }

    @Override
    public boolean hidePackInfo() {
        return getBool2(R.bool.hide_pack_info);
    }
}
