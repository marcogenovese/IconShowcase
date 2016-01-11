package jahirfiquitiva.apps.iconshowcase.utilities;

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
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import jahirfiquitiva.apps.iconshowcase.R;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Util {

    public static String getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // this should never happen
            return "Unknown";
        }
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

    public static void showSimpleSnackbar(View location, String text, int duration) {
        switch (duration) {
            case 1:
                Snackbar shortSnackbar = Snackbar.make(location, text,
                        Snackbar.LENGTH_SHORT);
                shortSnackbar.show();
                break;
            case 2:
                Snackbar longSnackbar = Snackbar.make(location, text,
                        Snackbar.LENGTH_LONG);
                longSnackbar.show();
                break;
            case 3:
                Snackbar indefiniteSnackbar = Snackbar.make(location, text,
                        Snackbar.LENGTH_INDEFINITE);
                indefiniteSnackbar.show();
                break;
        }
    }

    public static void openActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
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
                .setToolbarColor(ContextCompat.getColor(context, R.color.primary))
                .setShowTitle(true)
                .build();

        customTabsIntent.launchUrl((Activity) context, Uri.parse(link));

    }

    public static void showLog(String s) {
        Log.d("IconShowcase", s);
    }

    public static String getStringFromResources(Context context, int id) {
        return context.getResources().getString(id);
    }

    /**
     * Returns a string representation of {@param set}. Used only for debugging purposes.
     */
    @NonNull
    public static String setToString(@NonNull Set<String> set) {
        Iterator<String> i = set.iterator();
        if (!i.hasNext()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder().append('[');
        while (true) {
            sb.append(i.next());
            if (!i.hasNext()) {
                return sb.append(']').toString();
            }
            sb.append(", ");
        }
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

    /***
     * Method gets executed once the view is displayed
     *
     * @param view A view Object
     * @param method A callable method implementation
     */
    public static void triggerMethodOnceViewIsDisplayed(final View view, final Callable<Void> method) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override public void onGlobalLayout() {
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
}
