package jahirfiquitiva.apps.iconshowcase.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.HashMap;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class ClockWidget extends AppWidgetProvider {

    public HashMap<String, String> activityMap;

    @Override
    public void onReceive(Context context, Intent intent) {
        setupHashMap();
        String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

            Intent clockAppIntent = new Intent();
            clockAppIntent.setAction(Intent.ACTION_MAIN);
            clockAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            for (String packageName : activityMap.keySet()) {

                if (Util.isAppInstalled(context, packageName)) {

                    clockAppIntent.setPackage(packageName);
                    clockAppIntent.setComponent(new ComponentName(packageName,
                            activityMap.get(packageName)));
                    clockAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    break;

                }
            }

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.clock_widget);

            views.setOnClickPendingIntent(R.id.clockWidget,
                    PendingIntent.getActivity(context, 0, clockAppIntent, 0));

            AppWidgetManager.getInstance(context)
                    .updateAppWidget(intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS), views);

        }
    }

    private void setupHashMap() {
        activityMap = new HashMap<>();
        activityMap.put("com.android.alarmclock", "com.android.alarmclock.AlarmClock");
        activityMap.put("com.android.deskclock", "com.android.deskclock.DeskClock");
        activityMap.put("com.google.android.deskclock", "com.google.android.deskclock.DeskClock");
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