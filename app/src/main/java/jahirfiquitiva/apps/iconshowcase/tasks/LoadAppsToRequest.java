package jahirfiquitiva.apps.iconshowcase.tasks;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.apps.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.apps.iconshowcase.models.RequestItem;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class LoadAppsToRequest extends AsyncTask<Void, String, Boolean> {

    private static PackageManager mPackageManager;
    public ArrayList<RequestItem> list;

    final static ArrayList<RequestItem> appsList = new ArrayList<>();

    private AllAppsList mAllAppsList;

    private Context context;

    long startTime, endTime;

    public LoadAppsToRequest(Context context) {
        this.context = context;
        mPackageManager = context.getPackageManager();

        ArrayList<ResolveInfo> rAllActivitiesList =
                (ArrayList<ResolveInfo>) context.getPackageManager().queryIntentActivities(
                        getAllActivitiesIntent(), 0);

        for (ResolveInfo info : rAllActivitiesList) {

            if (info.activityInfo.packageName.equals(context.getApplicationContext().getPackageName())) {
                Util.showLog("Found " + context.getApplicationContext().getPackageName() + ", skipping record.");
                continue;
            }

            Drawable icon = info.loadIcon(mPackageManager);

            RequestItem appInfo = new RequestItem(
                    info.loadLabel(mPackageManager).toString(),
                    info.activityInfo.packageName,
                    info.activityInfo.name,
                    icon);

            appsList.add(appInfo);
        }

        mAllAppsList = new AllAppsList(appsList, context.getPackageManager());

    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        list = getAllActivities();

        list.removeAll(createListFromXML());

        Collections.sort(list, new Comparator<RequestItem>() {
            @Override
            public int compare(RequestItem a, RequestItem b) {
                return a.getAppName().compareToIgnoreCase(b.getAppName());
            }
        });

        return true;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        RequestsFragment.setupRequestAdapter(list);
        endTime = System.currentTimeMillis();
        Util.showLog("Apps to Request Task completed in: " + String.valueOf((endTime - startTime) / 1000) + " secs.");
    }

    private static ResolveInfo getResolveInfo(String componentString) {
        Intent intent = new Intent();
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

    public ArrayList<RequestItem> getAllActivities() {
        return mAllAppsList.data;
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
                                Drawable icon = info.loadIcon(mPackageManager);
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

    /**
     * Stores the list of all applications for the all apps view.
     */

    public class AllAppsList {

        // Main list off all apps.
        public ArrayList<RequestItem> data;

        public AllAppsList(ArrayList<RequestItem> initialList, PackageManager pm) {
            if (initialList != null) {
                data = new ArrayList<>(initialList.size());
                for (RequestItem item : initialList) {
                    add(item);
                }
            } else
                data = new ArrayList<>();
        }

        /**
         * Add the supplied ResolveInfo objects to the list, and enqueue it into the
         * list to broadcast when notify() is called.
         * <p/>
         * If the app is already in the list, doesn't add it.
         */
        public void add(RequestItem item) {
            if (findRequest(data, item)) {
                return;
            }
            data.add(item);
        }

        public void clear() {
            data.clear();
        }

        public int size() {
            return data.size();
        }

        public RequestItem get(int index) {
            return data.get(index);
        }

        private boolean findRequest(List<RequestItem> apps, RequestItem newItem) {
            return apps.contains(newItem);
        }

    }
}