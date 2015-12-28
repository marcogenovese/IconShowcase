package jahirfiquitiva.apps.iconshowcase.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.LauncherIconRestorerActivity;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;

/**
 * Created by JAHIR on 25/07/2015.
 */
public class IconRestorerWidget extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
    private static Preferences mPrefs;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            try {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.LAUNCHER");

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.setComponent(new ComponentName(context.getPackageName(),
                        "LauncherIconRestorerActivity.class"));
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.icon_restorer_widget);
                views.setOnClickPendingIntent(R.id.appWidget, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.icon_restorer_widget);
            Intent restore = new Intent(context, LauncherIconRestorerActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, restore, 0);
            views.setOnClickPendingIntent(R.id.appWidget, pendingIntent);

            AppWidgetManager
                    .getInstance(context)
                    .updateAppWidget(
                            intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS),
                            views);

        }

    }
}