/*
 *
 */

package jahirfiquitiva.iconshowcase.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class LauncherIconRestorerActivity extends Activity {

    private static Preferences mPrefs;
    private static PackageManager p;
    private static ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = new Preferences(LauncherIconRestorerActivity.this);

        p = getPackageManager();

        Class<?> className = null;

        final String packageName = Utils.getAppPackageName(getApplicationContext());
        String activityName = getResources().getString(R.string.main_activity_name);
        final String componentNameString = packageName + "." + activityName;

        try {
            className = Class.forName(componentNameString);
        } catch (ClassNotFoundException e) {
            //Do nothing
        }

        if (className != null) {
            componentName = new ComponentName(packageName, componentNameString);

            if (!mPrefs.getLauncherIconShown()) {

                mPrefs.setIconShown(true);

                p.setComponentEnabledSetting(componentName,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
                String toastContent = getResources().getString(R.string.launcher_icon_restored,
                        getResources().getString(R.string.app_name));
                Toast.makeText(getApplicationContext(), toastContent, Toast.LENGTH_LONG)
                        .show();

            } else {
                String newToastContent = getResources().getString(R.string.launcher_icon_no_restored,
                        getResources().getString(R.string.app_name));
                Toast.makeText(getApplicationContext(),
                        newToastContent, Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            String errorToastContent = getResources().getString(R.string.launcher_icon_restorer_error,
                    getResources().getString(R.string.app_name));
            Toast.makeText(getApplicationContext(),
                    errorToastContent, Toast.LENGTH_LONG)
                    .show();
        }

        finish();

    }

}
