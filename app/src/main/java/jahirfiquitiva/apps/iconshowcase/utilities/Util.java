package jahirfiquitiva.apps.iconshowcase.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.Set;

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

    public static void launchActivity(Context context, Class className) {
        Intent intent = new Intent(context, className);
        context.startActivity(intent);
    }

    public static void launchLink(Context context, String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void showLog(String s) {
        Log.d("IconShowcase", s);
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
}
