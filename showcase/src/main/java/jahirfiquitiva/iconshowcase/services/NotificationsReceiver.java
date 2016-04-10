/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jahirfiquitiva.iconshowcase.utilities.Preferences;

public class NotificationsReceiver extends BroadcastReceiver {

    public static void scheduleAlarms(Context context, boolean cancel) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationsService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        Preferences mPrefs = new Preferences(context);

        if (mPrefs.getNotifsEnabled()) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 5000,
                    mPrefs.getNotifsUpdateInterval(), pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onReceive(Context context, Intent i) {
        scheduleAlarms(context, false);
    }
}
