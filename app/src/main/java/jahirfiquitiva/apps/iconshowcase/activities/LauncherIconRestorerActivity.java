package jahirfiquitiva.apps.iconshowcase.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;

public class LauncherIconRestorerActivity extends Activity {

    private static Preferences mPrefs;
    private static PackageManager p;
    private static ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mPrefs = new Preferences(LauncherIconRestorerActivity.this);

        p = getPackageManager();
        componentName = new ComponentName(this, ShowcaseActivity.class);

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

        finish();

    }

}
