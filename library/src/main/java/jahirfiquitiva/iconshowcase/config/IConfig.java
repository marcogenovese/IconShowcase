package jahirfiquitiva.iconshowcase.config;

import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 * Created by Allan Wang on 2016-08-19.
 *
 * With reference to Polar
 * https://github.com/afollestad/polar-dashboard/blob/master/app/src/main/java/com/afollestad/polar/config/IConfig.java
 */
public interface IConfig {

    //General Functions

    boolean getBool2(@BoolRes int id);

    String getString2(@StringRes int id);

    String[] getStringArray2(@ArrayRes int id);

    int getInt2(@IntegerRes int id);

    boolean hasString(@StringRes int id);

    boolean hasArray(@ArrayRes int id);

    //Main Configs

    boolean allowDebugging();

    int appTheme();

    boolean hasGoogleDonations();

    boolean hasPaypal();

    @NonNull
    String getPaypalCurrency();

    boolean devOptions();

    //Home Configs

    boolean shuffleToolbarIcons();

    boolean userWallpaperInToolbar();

    boolean hidePackInfo();

    //Amounts interface?

    //TODO home card getter
}