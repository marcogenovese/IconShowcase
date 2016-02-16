/*
 *
 */

package jahirfiquitiva.iconshowcase.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Calendar;

import jahirfiquitiva.iconshowcase.R;

public class ThemeUtils {

    public final static int NAV_BAR_DEFAULT = 0;
    public final static int NAV_BAR_BLACK = 1;

    public final static int LIGHT = 0;
    public final static int DARK = 1;
    public final static int AUTO = 2;

    public static boolean darkTheme;
    public static boolean coloredNavBar;

    public static void onActivityCreateSetTheme(Activity activity) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        int mTheme = sp.getInt("theme", (activity.getResources().getInteger(R.integer.default_theme) - 1));
        switch (mTheme) {
            default:
            case LIGHT:
                activity.setTheme(R.style.AppTheme);
                darkTheme = false;
                break;
            case DARK:
                activity.setTheme(R.style.AppThemeDark);
                darkTheme = true;
                break;
            case AUTO:
                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
                if (timeOfDay >= 7 && timeOfDay < 20) {
                    activity.setTheme(R.style.AppTheme);
                    darkTheme = false;
                } else {
                    activity.setTheme(R.style.AppThemeDark);
                    darkTheme = true;
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void onActivityCreateSetNavBar(Activity activity) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        int navBarState;
        navBarState = activity.getResources().getBoolean(R.bool.color_navbar_by_default) ? 0 : 1;
        int mNavBar = sp.getInt("navBar", navBarState);
        switch (mNavBar) {
            default:
            case NAV_BAR_DEFAULT:
                activity.getWindow().setNavigationBarColor(darkTheme ?
                        ContextCompat.getColor(activity, R.color.dark_theme_primary_dark) :
                        ContextCompat.getColor(activity, R.color.light_theme_primary_dark));
                coloredNavBar = true;
                break;
            case NAV_BAR_BLACK:
                activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, android.R.color.black));
                coloredNavBar = false;
                break;
        }
    }

    public static void changeNavBar(Activity activity, int mNavBar) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        final Editor editor = sp.edit();
        editor.putInt("navBar", mNavBar).apply();
        restartActivity(activity);
    }

    public static void changeToTheme(Activity activity, int mTheme) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        final Editor editor = sp.edit();
        editor.putInt("theme", mTheme).apply();
    }

    public static void restartActivity(Activity activity) {
        activity.recreate();
    }

    public static void restartActivity(final Activity activity, MaterialDialog dialog) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.recreate();
            }
        });
    }

}