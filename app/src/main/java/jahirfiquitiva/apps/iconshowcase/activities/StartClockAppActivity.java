package jahirfiquitiva.apps.iconshowcase.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import java.util.HashMap;

import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class StartClockAppActivity extends Activity {

    public HashMap<String, String> activityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setupHashMap();
        boolean appFound = false;

        try {

            Intent clockApp = new Intent("android.intent.action.MAIN");
            clockApp.addCategory("android.intent.category.LAUNCHER");

            clockApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            for (String packageName : activityMap.keySet()) {
                Util.showLog("Checking if " + packageName + " is installed...");
                if (Util.isAppInstalled(this, packageName)) {
                    ComponentName cn = new ComponentName(packageName, activityMap.get(packageName));
                    clockApp.setComponent(cn);
                    Util.showLog(packageName + " is installed!! :D ");
                    appFound = true;
                    break;
                } else {
                    Util.showLog(packageName + " is not installed!! D: ");
                }
            }

            if (appFound) {
                startActivity(clockApp);
            }

        } catch (ActivityNotFoundException e) {
            Util.showLog("App not found!");
        }

        finish();

    }

    private void setupHashMap() {
        activityMap = new HashMap<>();
        activityMap.put("com.android.alarmclock", "com.android.alarmclock.AlarmClock");
        activityMap.put("com.android.deskclock", "com.android.deskclock.DeskClock");
        activityMap.put("com.google.android.deskclock", "com.google.android.deskclock.DeskClock");
        activityMap.put("com.google.android.deskclock", "com.android.deskclock.AlarmClock");
        activityMap.put("com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage");
        activityMap.put("com.sonyericsson.alarm", "com.sonyericsson.alarm.Alarm");
        activityMap.put("com.sonyericsson.organizer", "com.sonyericsson.organizer.Organizer_WorldClock");
        activityMap.put("com.asus.alarmclock", "com.asus.alarmclock.AlarmClock");
        activityMap.put("com.asus.deskclock", "com.asus.deskclock.DeskClock");
        activityMap.put("com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl");
        activityMap.put("com.motorola.blur.alarmclock", "com.motorola.blur.alarmclock.AlarmClock");
        activityMap.put("com.lge.clock", "com.lge.clock.AlarmClockActivity");
    }

}
