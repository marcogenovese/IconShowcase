package jahirfiquitiva.apps.iconshowcase.models.requests;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

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

public class RequestAppsList {

    final static ArrayList<RequestItem> appsList = new ArrayList<RequestItem>();
    final static ArrayList<String> listOfActivities = new ArrayList<String>();

    public static boolean createRequestAppsList(Context context) {

        boolean worked = false;

        try {
            XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser = xmlFactory.newPullParser();
            InputStream inputStream =
                    context.getResources().openRawResource(R.raw.appfilter);
            xmlParser.setInput(inputStream, null);
            int activity = xmlParser.getEventType();
            while (activity != XmlPullParser.END_DOCUMENT) {
                String name = xmlParser.getName();
                switch (activity) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("item")) {
                            try {
                                String initialComponent = xmlParser.getAttributeValue(null, "component").split("/")[1];
                                String finalComponent = initialComponent.substring(0, initialComponent.length() - 1);
                                String initialComponentPackage = xmlParser.getAttributeValue(null, "component").split("/")[0];
                                String finalComponentPackage = initialComponentPackage.substring(14, initialComponentPackage.length());
                                listOfActivities.add(finalComponentPackage + "/" + finalComponent);

                            } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                activity = xmlParser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> packageList =
                packageManager.queryIntentActivities(
                        new Intent("android.intent.action.MAIN")
                                .addCategory("android.intent.category.LAUNCHER"), 0);

        Collections.sort(packageList, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                String initialName = packageManager.getApplicationLabel(a.activityInfo.applicationInfo).toString();
                String finalName = packageManager.getApplicationLabel(b.activityInfo.applicationInfo).toString();
                return initialName.compareToIgnoreCase(finalName);
            }
        });

        for (ResolveInfo appInfo : packageList) {
            String packageName = appInfo.activityInfo.packageName;
            if (packageName.equals(context.getPackageName())) {
                continue;
            }
        }

        if (packageList != null && !packageList.isEmpty()) {
            try {
                for (final ResolveInfo info : packageList) {
                    if (!listOfActivities.contains(info.activityInfo.packageName + "/" + info.activityInfo.name)) {
                        final String name = info.loadLabel(packageManager).toString();
                        final String pkgName = info.activityInfo.packageName;
                        final String className = info.activityInfo.name;
                        Drawable icon = packageManager.getApplicationIcon(pkgName);
                        RequestItem App_info = new RequestItem(name, pkgName, className, icon, false);
                        appsList.add(App_info);
                    }
                }
                worked = true;
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        }

        return worked;
    }

    public static ArrayList<RequestItem> getRequestAppsList() {
        return appsList;
    }

    public static void clearLists() {
        listOfActivities.clear();
        appsList.clear();
    }

}
