package jahirfiquitiva.iconshowcase.logging;

import android.util.Log;

import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-08-20.
 */
public class CrashReportingTree extends Timber.Tree {

    private static final String TAG = "IconShowcase";

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return;
        }

        //TODO add better logging?
        switch (priority) {
            case Log.WARN:
                Log.w(TAG, message);
                break;
            case Log.ERROR:
                Log.e(TAG, message);
        }

        if (t != null) {
            if (priority == Log.ERROR) {
                Log.e(TAG, message, t);
            } else if (priority == Log.WARN) {
                Log.w(TAG, message, t);
            }
        }
    }
}