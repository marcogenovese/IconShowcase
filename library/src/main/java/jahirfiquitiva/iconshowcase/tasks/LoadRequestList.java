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
package jahirfiquitiva.iconshowcase.tasks;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;
import android.util.DisplayMetrics;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.iconshowcase.models.AppFilterItem;
import jahirfiquitiva.iconshowcase.models.RequestItem;
import jahirfiquitiva.iconshowcase.models.RequestList;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.Utils;


public class LoadRequestList extends AsyncTask<Void, String, Boolean> {

    private ArrayList<RequestItem> appsToTheme;
    private ArrayList<AppFilterItem> appFilterItems;
    private PackageManager pm;
    private WeakReference<Context> context;
    private Resources res;

    private final long startTime;
    private long endTime;

    public LoadRequestList(Context context) {
        this.context = new WeakReference<>(context);
        this.pm = context.getPackageManager();
        this.res = context.getResources();
        this.startTime = System.currentTimeMillis();
        if (res.getBoolean(R.bool.debugging)) {
            appFilterItems = new ArrayList<>();
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        appsToTheme = new ArrayList<>();

        ArrayList<ResolveInfo> rAllActivitiesList =
                (ArrayList<ResolveInfo>) pm.queryIntentActivities(getAllActivitiesIntent(), 0);

        for (ResolveInfo info : rAllActivitiesList) {
            if (info.activityInfo.packageName.equals(context.get().getPackageName())) {
                continue;
            }
            appsToTheme.add(new RequestItem(
                    info.loadLabel(pm).toString(),
                    info.activityInfo.packageName,
                    info.activityInfo.name,
                    getNormalIcon(info, pm),
                    getHiResAppIcon(info)));
        }

        if (appsToTheme == null) return false;

        XmlPullParser xpp;

        try {
            InputStream istr = context.get().getResources().openRawResource(R.raw.appfilter);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(istr, "UTF-8");
            // TODO: Test if this is needed: xpp.setInput(istr, null);
        } catch (XmlPullParserException e) {
            Utils.showLog(e.getMessage());
            return false;
        }

        ArrayList<RequestItem> themedApps = new ArrayList<>();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.END_TAG) {
                    String tagname = xpp.getName();
                    if ("item".equals(tagname)) {
                        String cnstr = xpp.getAttributeValue(null, "component")
                                .replace("ComponentInfo{", "")
                                .replace("}", "");
                        Utils.showLog(cnstr);
                        String drawablename = xpp.getAttributeValue(null, "drawable");
                        if (cnstr != null && !(cnstr.isEmpty())) {
                            if (res.getBoolean(R.bool.debugging)) {
                                if (appFilterItems != null) {
                                    appFilterItems.add(new AppFilterItem(
                                            cnstr,
                                            drawablename
                                    ));
                                }
                            }
                            ResolveInfo ri = getResolveInfo(cnstr);
                            if (ri != null) {
                                themedApps.add(new RequestItem(
                                        ri.loadLabel(pm).toString(),
                                        ri.activityInfo.packageName,
                                        ri.activityInfo.name,
                                        getNormalIcon(ri, pm),
                                        getHiResAppIcon(ri)));
                            }
                        }
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Utils.showLog(e.getMessage());
            return false;
        }

        try {
            appsToTheme.removeAll(themedApps);
            Collections.sort(appsToTheme, new Comparator<RequestItem>() {
                @Override
                public int compare(RequestItem a, RequestItem b) {
                    return a.getAppName().compareToIgnoreCase(b.getAppName());
                }
            });
            endTime = System.currentTimeMillis();
            return true;
        } catch (Exception ex) {
            Utils.showLog(ex.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        Preferences mPrefs = new Preferences(context.get());
        mPrefs.setIfAppsToRequestLoaded(worked);
        RequestList.setRequestList(appsToTheme);
        RequestsFragment.setupContent(RequestsFragment.layout, context.get());
        if (worked) {
            Utils.showLog(context.get(),
                    "Load of request list completed in: " +
                            String.valueOf((endTime - startTime) / 1000) + " secs.");
        }
        if (res.getBoolean(R.bool.debugging)) {
            showAppFilterErrors(context.get());
            showDuplicatedComponentsInLog(context.get());
        }
    }

    private Intent getAllActivitiesIntent() {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mainIntent;
    }

    private ResolveInfo getResolveInfo(String componentString) {
        Intent intent = new Intent();

        // Example format:
        //intent.setComponent(new ComponentName("com.myapp", "com.myapp.launcher.settings"));

        if (componentString != null) {
            String[] split;
            try {
                split = componentString.split("/");
                try {
                    intent.setComponent(new ComponentName(split[0], split[1]));
                } catch (ArrayIndexOutOfBoundsException e1) {
                    return null;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return null;
            }
            return pm.resolveActivity(intent, 0);
        } else {
            return null;
        }
    }

    private Drawable getHiResAppIcon(ResolveInfo info) {
        return getHiResAppIcon(info.activityInfo);
    }

    private Drawable getHiResAppIcon(ActivityInfo info) {
        Resources resources;
        try {
            resources = context.get().getPackageManager().getResourcesForApplication(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            resources = null;
        }
        if (resources != null) {
            int iconId = info.getIconResource();
            if (iconId != 0) {
                return getHiResAppIcon(resources, iconId);
            }
        }
        return getAppDefaultActivityIcon();
    }

    @SuppressWarnings("deprecation")
    private Drawable getHiResAppIcon(Resources resources, int iconId) {
        Drawable d;
        try {
            int iconDpi;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                iconDpi = DisplayMetrics.DENSITY_XXXHIGH;
            } else {
                iconDpi = DisplayMetrics.DENSITY_XXHIGH;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                d = resources.getDrawableForDensity(iconId, iconDpi, null);
            } else {
                d = resources.getDrawableForDensity(iconId, iconDpi);
            }

        } catch (Resources.NotFoundException e) {
            try {
                d = ContextCompat.getDrawable(context.get(), R.drawable.ic_na_launcher);
            } catch (Resources.NotFoundException e1) {
                d = null;
            }
        }

        return (d != null) ? d : getAppDefaultActivityIcon();
    }

    private Drawable getNormalIcon(ResolveInfo info, PackageManager pm) {
        if (info != null) {
            if (info.loadIcon(pm) != null) {
                return info.loadIcon(pm);
            } else {
                return getNormalIcon(context.get(), info.resolvePackageName);
            }
        } else {
            return getAppDefaultActivityIcon();
        }
    }

    public Drawable getNormalIcon(Context context, String pkg) {
        final ApplicationInfo ai = getAppInfo(context, pkg);
        if (ai != null) {
            return ai.loadIcon(pm);
        } else {
            return getAppDefaultActivityIcon();
        }
    }

    @Nullable
    public ApplicationInfo getAppInfo(Context context, String pkg) {
        try {
            return context.getPackageManager().getApplicationInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private Drawable getAppDefaultActivityIcon() {
        return getHiResAppIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

    private void showAppFilterErrors(Context context) {
        Utils.showAppFilterLog(context, "----- START OF APPFILTER DEBUG -----");
        for (AppFilterItem error : appFilterItems) {
            String iconName = error.getIconName();
            if (iconName.equals("")) {
                Utils.showAppFilterLog(context, "Found empty drawable for component: \'" + error.getCompleteComponent() + "\'");
            } else {
                if (error.getCompleteComponent().isEmpty()) {
                    Utils.showAppFilterLog(context, "Found empty ComponentInfo for icon: \'" + iconName + "\'");
                } /* else {
                    String[] comp = error.getCompleteComponent().split("/");
                    if (comp[0].isEmpty()) {
                        Utils.showAppFilterLog(context, "Found empty component package for icon: \'" + iconName + "\'");
                    } else if (comp[1].isEmpty()) {
                        Utils.showAppFilterLog(context, "Found empty component for icon: \'" + iconName + "\'");
                    }
                } */
                if (Utils.getIconResId(context.getResources(), context.getPackageName(), iconName) == 0) {
                    Utils.showAppFilterLog(context, "Icon \'" + iconName + "\' is mentioned in appfilter.xml but could not be found in the app resources.");
                }
            }
        }
    }

    private void showDuplicatedComponentsInLog(Context context) {
        SimpleArrayMap<String, Integer> occurrences = new SimpleArrayMap<>();
        int count = 0;
        for (int i = 0; i < appFilterItems.size(); i++) {
            String word = appFilterItems.get(i).getCompleteComponent();
            count = occurrences.get(word) == null ? 0 : occurrences.get(word);
            occurrences.put(word, count + 1);
        }
        for (int j = 0; j < occurrences.size(); j++) {
            String word = occurrences.keyAt(j);
            if (count > 0) {
                Utils.showAppFilterLog(context, "Duplicated component: \'" + word + "\' - " + String.valueOf(count) + " times.");
            }
        }
        Utils.showAppFilterLog(context, "----- END OF APPFILTER DEBUG -----");
    }

}