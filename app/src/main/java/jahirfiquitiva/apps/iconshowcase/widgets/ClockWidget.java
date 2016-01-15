package jahirfiquitiva.apps.iconshowcase.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.StartClockAppActivity;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class ClockWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            try {
                Intent clockApp = new Intent("android.intent.action.MAIN");
                clockApp.addCategory("android.intent.category.LAUNCHER");

                clockApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                clockApp.setComponent(new ComponentName(context.getPackageName(),
                        "StartClockAppActivity.class"));

                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.clock_widget);

                views.setOnClickPendingIntent(R.id.clockWidget,
                        PendingIntent.getActivity(context, 0, clockApp, 0));

                appWidgetManager.updateAppWidget(appWidgetId, views);

            } catch (ActivityNotFoundException e) {
                Util.showLog("App not found!");
            }

        }
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.clock_widget);

            Intent clockApp = new Intent(context, StartClockAppActivity.class);

            views.setOnClickPendingIntent(R.id.clockWidget,
                    PendingIntent.getActivity(context, 0, clockApp, 0));

            AppWidgetManager
                    .getInstance(context)
                    .updateAppWidget(
                            clockApp.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS),
                            views);

        }

    }
}