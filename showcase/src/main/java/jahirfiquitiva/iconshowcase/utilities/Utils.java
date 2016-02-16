/*
 *
 */

package jahirfiquitiva.iconshowcase.utilities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.florent37.glidepalette.GlidePalette;

import java.util.concurrent.Callable;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.WallpapersAdapter;
import jahirfiquitiva.iconshowcase.views.CustomCoordinatorLayout;

/**
 * With a little help from Aidan Follestad (afollestad)
 */
public class Utils {

    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // this should never happen
            return "Unknown";
        }
    }

    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public static void showSimpleSnackbar(Context context, View location, String text, int duration) {
        final int snackbarLight = ContextCompat.getColor(context, R.color.snackbar_light);
        final int snackbarDark = ContextCompat.getColor(context, R.color.snackbar_dark);

        switch (duration) {
            case 1:
                Snackbar shortSnackbar = Snackbar.make(location, text,
                        Snackbar.LENGTH_SHORT);
                ViewGroup shortGroup = (ViewGroup) shortSnackbar.getView();
                shortGroup.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark : snackbarLight);
                shortSnackbar.show();
                break;
            case 2:
                Snackbar longSnackbar = Snackbar.make(location, text,
                        Snackbar.LENGTH_LONG);
                ViewGroup longGroup = (ViewGroup) longSnackbar.getView();
                longGroup.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark : snackbarLight);
                longSnackbar.show();
                break;
            case 3:
                Snackbar indefiniteSnackbar = Snackbar.make(location, text,
                        Snackbar.LENGTH_INDEFINITE);
                ViewGroup indefiniteGroup = (ViewGroup) indefiniteSnackbar.getView();
                indefiniteGroup.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark : snackbarLight);
                indefiniteSnackbar.show();
                break;
        }
    }

    public static void openLink(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openLinkInChromeCustomTab(Context context, String link) {
        final CustomTabsClient[] mClient = new CustomTabsClient[1];
        final CustomTabsSession[] mCustomTabsSession = new CustomTabsSession[1];
        CustomTabsServiceConnection mCustomTabsServiceConnection;
        CustomTabsIntent customTabsIntent;

        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                mClient[0] = customTabsClient;
                mClient[0].warmup(0L);
                mCustomTabsSession[0] = mClient[0].newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient[0] = null;
            }

        };

        CustomTabsClient.bindCustomTabsService(context, "com.android.chrome", mCustomTabsServiceConnection);
        customTabsIntent = new CustomTabsIntent.Builder(mCustomTabsSession[0])
                .setToolbarColor(ThemeUtils.darkTheme ?
                        ContextCompat.getColor(context, R.color.dark_theme_primary_dark) :
                        ContextCompat.getColor(context, R.color.light_theme_primary_dark))
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl((Activity) context, Uri.parse(link));

    }

    public static void showLog(Context context, String s) {
        String tag = "IconShowcase + " + context.getResources().getString(R.string.app_name);
        Log.d(tag, s);
    }

    public static void showAppFilterLog(Context context, String s) {
        String tag = context.getResources().getString(R.string.app_name) + " AppFilter";
        Log.d(tag, s);
    }

    public static void showLog(String s) {
        Log.d("IconShowcase ", s);
    }

    public static String getStringFromResources(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static String makeTextReadable(String name) {
        String partialConvertedText = name.replaceAll("_", " ");
        String[] text = partialConvertedText.split("\\s+");
        StringBuilder sb = new StringBuilder();
        if (text[0].length() > 0) {
            sb.append(Character.toUpperCase(text[0].charAt(0))).append(text[0].subSequence(1, text[0].length()).toString().toLowerCase());
            for (int i = 1; i < text.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(text[i].charAt(0))).append(text[i].subSequence(1, text[i].length()).toString().toLowerCase());
            }
        }
        return sb.toString();
    }

    public static void forceCrash() {
        throw new RuntimeException("This is a crash");
    }

    public static void sendEmailWithDeviceInfo(Context context) {
        StringBuilder emailBuilder = new StringBuilder();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + context.getResources().getString(R.string.email_id)));
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.email_subject));
        emailBuilder.append("\n \n \nOS Version: ").append(System.getProperty("os.version")).append("(").append(Build.VERSION.INCREMENTAL).append(")");
        emailBuilder.append("\nOS API Level: ").append(Build.VERSION.SDK_INT);
        emailBuilder.append("\nDevice: ").append(Build.DEVICE);
        emailBuilder.append("\nManufacturer: ").append(Build.MANUFACTURER);
        emailBuilder.append("\nModel (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")");
        PackageInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert appInfo != null;
        emailBuilder.append("\nApp Version Name: ").append(appInfo.versionName);
        emailBuilder.append("\nApp Version Code: ").append(appInfo.versionCode);
        intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString());
        context.startActivity(Intent.createChooser(intent, (context.getResources().getString(R.string.send_title))));
    }

    public static GlidePalette getGlidePalette(String profile, boolean withTintedTexts, Preferences mPrefs,
                                               String wallUrl, WallpapersAdapter.WallsHolder holder) {

        GlidePalette palette;

        int colorSwatch = GlidePalette.Profile.VIBRANT;

        switch (profile) {
            case "VIBRANT":
                colorSwatch = GlidePalette.Profile.VIBRANT;
                break;
            case "VIBRANT_LIGHT":
                colorSwatch = GlidePalette.Profile.VIBRANT_LIGHT;
                break;
            case "VIBRANT_DARK":
                colorSwatch = GlidePalette.Profile.VIBRANT_LIGHT;
                break;
            case "MUTED":
                colorSwatch = GlidePalette.Profile.MUTED;
                break;
            case "MUTED_LIGHT":
                colorSwatch = GlidePalette.Profile.MUTED_LIGHT;
                break;
            case "MUTED_DARK":
                colorSwatch = GlidePalette.Profile.MUTED_LIGHT;
                break;
        }

        if (withTintedTexts) {
            palette = GlidePalette.with(wallUrl)
                    .use(colorSwatch)
                    .intoBackground(holder.titleBg, GlidePalette.Swatch.RGB)
                    .intoTextColor(holder.name, GlidePalette.Swatch.TITLE_TEXT_COLOR)
                    .intoTextColor(holder.authorName, GlidePalette.Swatch.BODY_TEXT_COLOR)
                    .crossfade(mPrefs.getAnimationsEnabled());
        } else {
            palette = GlidePalette.with(wallUrl)
                    .use(colorSwatch)
                    .intoBackground(holder.titleBg, GlidePalette.Swatch.RGB)
                    .crossfade(mPrefs.getAnimationsEnabled());
        }

        return palette;

    }

    /***
     * Method gets executed once the view is displayed
     *
     * @param view   A view Object
     * @param method A callable method implementation
     */
    public static void triggerMethodOnceViewIsDisplayed(final View view, final Callable<Void> method) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                try {
                    method.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void collapseToolbar(Context context) {
        Preferences mPrefs = new Preferences(context);
        AppBarLayout appbar = (AppBarLayout) ((Activity) context).findViewById(R.id.appbar);
        CustomCoordinatorLayout coordinatorLayout = (CustomCoordinatorLayout) ((Activity) context).findViewById(R.id.mainCoordinatorLayout);
        appbar.setExpanded(false, mPrefs.getAnimationsEnabled());
        appbar.setEnabled(false);
        coordinatorLayout.setScrollAllowed(false);
    }

    public static void expandToolbar(Context context) {
        Preferences mPrefs = new Preferences(context);
        AppBarLayout appbar = (AppBarLayout) ((Activity) context).findViewById(R.id.appbar);
        CustomCoordinatorLayout coordinatorLayout = (CustomCoordinatorLayout) ((Activity) context).findViewById(R.id.mainCoordinatorLayout);
        appbar.setExpanded(true, mPrefs.getAnimationsEnabled());
        appbar.setEnabled(true);
        coordinatorLayout.setScrollAllowed(true);
    }

}
