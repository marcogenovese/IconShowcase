/*
 * Copyright (c) 2016.  Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

package jahirfiquitiva.iconshowcase.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.models.NotificationItem;
import jahirfiquitiva.iconshowcase.utilities.JSONParser;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class NotificationsService extends Service {

    final static String ACTION = "NotifyServiceAction";
    //final static int RQS_STOP_SERVICE = 1;

    private Handler handler = null;
    private static Runnable runnable = null;

    @Override
    public void onCreate() {

        final Preferences mPrefs = new Preferences(getApplicationContext());

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (Utils.hasNetwork(getApplicationContext())) {
                    new CheckForNotifications(mPrefs.getActivityVisible()).execute();
                }
                handler.postDelayed(runnable, 120000);
            }
        };

        handler.postDelayed(runnable, 3000);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /*
    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE) {
                stopSelf();
            }
        }
    }
    */

    private void Notify(String content, int type, int ID) {

        Preferences mPrefs = new Preferences(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        //registerReceiver(new NotifyServiceReceiver(), intentFilter);

        String appName = Utils.getStringFromResources(this, R.string.app_name);

        String title = appName, notifContent = null;

        switch (type) {
            case 1:
                title = getResources().getString(R.string.new_walls_notif_title, appName);
                notifContent = getResources().getString(R.string.new_walls_notif_content, content);
                break;
            case 2:
                title = appName + " " + getResources().getString(R.string.news).toLowerCase();
                notifContent = content;
                break;
        }

        // Send Notification
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setContentTitle(title);
        if (notifContent != null) {
            notifBuilder.setContentText(notifContent);
        }
        notifBuilder.setTicker(title);

        Uri ringtoneUri = Uri.parse("content://settings/system/notif_sound");
        notifBuilder.setSound(ringtoneUri);

        if (mPrefs.getNotifsVibrationEnabled()) notifBuilder.setVibrate(new long[]{500, 500});

        int ledColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(this, R.color.dark_theme_accent) :
                ContextCompat.getColor(this, R.color.light_theme_accent);

        if (mPrefs.getNotifsLedEnabled()) {
            notifBuilder.setLights(
                    Color.argb(1, Color.red(ledColor), Color.green(ledColor), Color.blue(ledColor)),
                    1, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notifBuilder.setPriority(Notification.PRIORITY_HIGH);

        Intent resultIntent = new Intent(this, ShowcaseActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ShowcaseActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notifBuilder.setContentIntent(resultPendingIntent);

        notifBuilder.setOngoing(false);

        notifBuilder.setSmallIcon(R.drawable.ic_muzei_logo);

        notifManager.notify(ID, notifBuilder.build());
    }

    public class CheckForNotifications extends AsyncTask<Void, String, Boolean> {

        public JSONObject mainObject, tag;
        public JSONArray notifs;
        private boolean hideNotifs = true;

        private ArrayList<NotificationItem> notifsList = new ArrayList<>();

        public CheckForNotifications(boolean hideNotifs) {
            this.hideNotifs = hideNotifs;
        }

        @Override
        protected void onPreExecute() {
            if (notifsList != null) {
                notifsList.clear();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean worked = false;
            try {
                mainObject = JSONParser.getJSONFromURL(getResources().getString(R.string.notifications_file_url));
                if (mainObject != null) {
                    try {
                        notifs = mainObject.getJSONArray("notifs");
                        for (int i = 0; i < notifs.length(); i++) {
                            tag = notifs.getJSONObject(i);
                            String newWalls = tag.getString("new_walls");
                            if (newWalls != null) {
                                notifsList.add(new NotificationItem(newWalls, 1, 97));
                            }
                            String info = tag.getString("general");
                            if (info != null) {
                                notifsList.add(new NotificationItem(info, 2, 19));
                            }
                        }
                        worked = true;
                    } catch (JSONException e) {
                        worked = false;
                        Utils.showLog("Error downloading notifs JSON: " + e.getLocalizedMessage());
                    }
                } else {
                    worked = false;
                }
            } catch (Exception e) {
                worked = false;
                Utils.showLog("Error downloading notifs JSON: " + e.getLocalizedMessage());
            }
            return worked;
        }

        @Override
        protected void onPostExecute(Boolean worked) {
            if (worked) {
                for (int i = 0; i < notifsList.size(); i++) {
                    NotificationItem notif = notifsList.get(i);
                    String notifText = notif.getText();
                    if (notif.getType() == 1) {
                        int number = 0;
                        try {
                            number = Integer.valueOf(notifText);
                        } catch (NumberFormatException numEx) {
                            number = 0;
                        }
                        if (number > 0 && !hideNotifs) {
                            Notify(notif.getText(), notif.getType(), notif.getID());
                        }
                    } else {
                        if (!(notifText.equals("null")) && !hideNotifs) {
                            Notify(notif.getText(), notif.getType(), notif.getID());
                        }
                    }
                }
            }
        }
    }

    public void clearNotification(int ID) {
        NotificationManager notifManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(ID);
    }

}