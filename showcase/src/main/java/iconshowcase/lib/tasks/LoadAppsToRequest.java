package iconshowcase.lib.tasks;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import iconshowcase.lib.R;
import iconshowcase.lib.fragments.RequestsFragment;
import iconshowcase.lib.models.RequestItem;
import iconshowcase.lib.utilities.ApplicationBase;
import iconshowcase.lib.utilities.Utils;

public class LoadAppsToRequest extends AsyncTask<Void, String, ArrayList<RequestItem>> {

    private static PackageManager mPackageManager;

    final static ArrayList<RequestItem> appsList = new ArrayList<>();

    private Context context;

    long startTime, endTime;

    public LoadAppsToRequest(Context context) {
        startTime = System.currentTimeMillis();
        this.context = context;
        mPackageManager = context.getPackageManager();

        ArrayList<ResolveInfo> rAllActivitiesList =
                (ArrayList<ResolveInfo>) context.getPackageManager().queryIntentActivities(
                        getAllActivitiesIntent(), 0);

        for (ResolveInfo info : rAllActivitiesList) {

            if (info.activityInfo.packageName.equals(context.getApplicationContext().getPackageName())) {
                continue;
            }

            Drawable icon;
            try {
                icon = info.loadIcon(mPackageManager);
            } catch (Resources.NotFoundException e) {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_launcher);
            }

            RequestItem appInfo = new RequestItem(
                    info.loadLabel(mPackageManager).toString(),
                    info.activityInfo.packageName,
                    info.activityInfo.name,
                    icon);

            appsList.add(appInfo);
        }

        ApplicationBase.allApps = appsList;

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected ArrayList<RequestItem> doInBackground(Void... params) {

        final ArrayList<RequestItem> list = ApplicationBase.allApps;

        list.removeAll(createListFromXML());

        Collections.sort(list, new Comparator<RequestItem>() {
            @Override
            public int compare(RequestItem a, RequestItem b) {
                return a.getAppName().compareToIgnoreCase(b.getAppName());
            }
        });

        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<RequestItem> list) {
        ApplicationBase.allAppsToRequest = list;
        RequestsFragment.setupContent();
        endTime = System.currentTimeMillis();
        Utils.showLog("Apps to Request Task completed in: " + String.valueOf((endTime - startTime) / 1000) + " secs.");
    }

    private static ResolveInfo getResolveInfo(String componentString) {
        Intent intent = new Intent();

        // Example format:
        //intent.setComponent(new ComponentName("com.myapp", "com.myapp.launcher.settings"));

        String[] split = componentString.split("/");
        intent.setComponent(new ComponentName(split[0], split[1]));
        return mPackageManager.resolveActivity(intent, 0);
    }

    private static String gComponentString(XmlPullParser xmlParser) {

        try {

            final String initialComponent = xmlParser.getAttributeValue(null, "component").split("/")[1];
            final String finalComponent = initialComponent.substring(0, initialComponent.length() - 1);
            final String initialComponentPackage = xmlParser.getAttributeValue(null, "component").split("/")[0];
            final String finalComponentPackage = initialComponentPackage.substring(14, initialComponentPackage.length());

            return finalComponentPackage + "/" + finalComponent;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public Intent getAllActivitiesIntent() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mainIntent;
    }

    private ArrayList<RequestItem> createListFromXML() {

        ArrayList<RequestItem> activitiesToRemove = new ArrayList<>();

        try {

            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlFactory.newPullParser();
            InputStream inputStream = context.getResources().openRawResource(R.raw.appfilter);
            xmlParser.setInput(inputStream, null);

            int activity = xmlParser.getEventType();

            while (activity != XmlPullParser.END_DOCUMENT) {

                String name = xmlParser.getName();

                switch (activity) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:

                        if (name.equals("item")) {

                            ResolveInfo info = getResolveInfo(gComponentString(xmlParser));

                            if (info != null) {
                                Drawable icon;
                                try {
                                    icon = info.loadIcon(mPackageManager);
                                } catch (Resources.NotFoundException e) {
                                    icon = ContextCompat.getDrawable(context, R.drawable.ic_launcher);
                                }
                                RequestItem appInfo = new RequestItem(
                                        info.loadLabel(mPackageManager).toString(),
                                        info.activityInfo.packageName,
                                        info.activityInfo.name,
                                        icon);

                                activitiesToRemove.add(appInfo);
                            }
                        }

                        break;
                }

                activity = xmlParser.next();
            }

        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return activitiesToRemove;
    }

}