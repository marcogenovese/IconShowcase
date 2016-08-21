/*
 * Copyright (c) 2016 Jahir Fiquitiva
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
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
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
import jahirfiquitiva.iconshowcase.config.Config;
import jahirfiquitiva.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.iconshowcase.models.AppFilterItem;
import jahirfiquitiva.iconshowcase.models.RequestItem;
import jahirfiquitiva.iconshowcase.models.RequestList;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import timber.log.Timber;

public class LoadRequestList extends AsyncTask<Void, String, Boolean> {

    private ArrayList<RequestItem> mAppsToTheme = new ArrayList<>();
    private ArrayList<AppFilterItem> mAppfilterItems;
    private PackageManager pm;
    private WeakReference<Context> context;

    private final long startTime;
    private long endTime;
    private IRequestList mCallback;
    
    public interface IRequestList {
        void onListLoaded(ArrayList<RequestItem> appList);
    }

    public LoadRequestList(Context context, IRequestList callback) {
        this.context = new WeakReference<>(context);
        this.pm = context.getPackageManager();
        mCallback = callback;
        this.startTime = System.currentTimeMillis();
        if (Config.get().allowDebugging()) {
            mAppfilterItems = new ArrayList<>();
        }
    }

    @Override
    protected Boolean doInBackground (Void... voids) {

        ArrayList<ResolveInfo> rAllActivitiesList =
                (ArrayList<ResolveInfo>) pm.queryIntentActivities(getAllActivitiesIntent(), 0);

        for (ResolveInfo info : rAllActivitiesList) {
            if (info.activityInfo.packageName.equals(context.get().getPackageName())) {
                continue;
            }
            mAppsToTheme.add(new RequestItem(
                    info.loadLabel(pm).toString(),
                    info.activityInfo.packageName,
                    info.activityInfo.name,
                    getNormalIcon(info, pm),
                    info));
//                    getHiResAppIcon(info)));
        }

        if (mAppsToTheme == null) return false;

        XmlPullParser xpp;

        try {
            InputStream istr = context.get().getResources().openRawResource(R.raw.appfilter);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
            xpp.setInput(istr, "UTF-8");
            // TODO: Test if this is needed: xpp.setInput(istr, null);
        } catch (XmlPullParserException e) {
            Timber.d(e.getMessage());
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
                        String drawablename = xpp.getAttributeValue(null, "drawable");
                        if (cnstr != null && !(cnstr.isEmpty())) {
                            if (Config.get().allowDebugging()) {
                                if (mAppfilterItems != null) {
                                    mAppfilterItems.add(new AppFilterItem(
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
                                        //No Need to load these portions TODO check if other strings are all necessary
                                        null, null));
//                                        getNormalIcon(ri, pm),
//                                        ri));
//                                        getHiResAppIcon(ri)));
                            }
                        }
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            Timber.d(e.getMessage());
            return false;
        }

        try {
            mAppsToTheme.removeAll(themedApps);
            Collections.sort(mAppsToTheme, new Comparator<RequestItem>() {
                @Override
                public int compare (RequestItem a, RequestItem b) {
                    return a.getAppName().compareToIgnoreCase(b.getAppName());
                }
            });
            endTime = System.currentTimeMillis();
            return true;
        } catch (Exception ex) {
            Timber.d(ex.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        mCallback.onListLoaded(mAppsToTheme);
        //TODO remove
//        Preferences mPrefs = new Preferences(context.get());
//        mPrefs.setIfAppsToRequestLoaded(worked);
        if (worked) {
            Timber.d("Load of request list completed in: %d milliseconds", (endTime - startTime));
        }
        if (Config.get().allowDebugging()) {
            showAppFilterErrors();
            showDuplicatedComponentsInLog();
        }
    }

    private Intent getAllActivitiesIntent () {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return mainIntent;
    }

    private ResolveInfo getResolveInfo (String componentString) {
        Intent intent = new Intent();

        // Example format:
        //intent.setComponent(new ComponentName("com.myapp", "com.myapp.launcher.settings"));

        if (componentString != null) {
            String[] split = getSplitComponent(componentString);
            try {
                if (split != null) {
                    intent.setComponent(new ComponentName(split[0], split[1]));
                }
            } catch (ArrayIndexOutOfBoundsException e1) {
                return null;
            }
            return pm.resolveActivity(intent, 0);
        } else {
            return null;
        }
    }

    private String[] getSplitComponent (String componentString) {
        String[] split;
        try {
            split = componentString.split("/");
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
        return split;
    }

    private Drawable getHiResAppIcon (ResolveInfo info) {
        return getHiResAppIcon(info.activityInfo);
    }

    private Drawable getHiResAppIcon (ActivityInfo info) {
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
    private Drawable getHiResAppIcon (Resources resources, int iconId) {
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

    private Drawable getNormalIcon (ResolveInfo info, PackageManager pm) {
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

    public Drawable getNormalIcon (Context context, String pkg) {
        final ApplicationInfo ai = getAppInfo(context, pkg);
        if (ai != null) {
            return ai.loadIcon(pm);
        } else {
            return getAppDefaultActivityIcon();
        }
    }

    @Nullable
    public ApplicationInfo getAppInfo (Context context, String pkg) {
        try {
            return context.getPackageManager().getApplicationInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private Drawable getAppDefaultActivityIcon () {
        return getHiResAppIcon(Resources.getSystem(), android.R.mipmap.sym_def_app_icon);
    }

    private void showAppFilterErrors () {
        Timber.d("----- START OF APPFILTER DEBUG -----");
        for (AppFilterItem error : mAppfilterItems) {
            String iconName = error.getIconName();
            if (iconName.equals("")) {
                Timber.d("Found empty drawable for component: \'%s\'", error.getCompleteComponent());
            } else {
                Timber.d("COMPONENT: " + error.getCompleteComponent());
                if (error.getCompleteComponent().isEmpty() ||
                        (error.getCompleteComponent().contains("/") && error.getCompleteComponent().length() <= 1)) {
                    Timber.d("Found empty ComponentInfo for icon: \'%s\'", iconName);
                } else {
                    String[] comp = getSplitComponent(error.getCompleteComponent());
                    if (comp != null) {
                        try {
                            if (comp[0].isEmpty()) {
                                Timber.d("Found empty component package for icon: \'%s\'", iconName);
                            }
                            if (comp[1].isEmpty()) {
                                Timber.d("Found empty component for icon: \'%s\'", iconName);
                            }
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            //Do nothing
                        }
                    }
                }
                if (Config.get().getIconResId(iconName) == 0) {
                    Timber.d("Icon \'%s\' is mentioned in appfilter.xml but could not be found in the app resources.", iconName);
                }
            }
        }
    }

    private void showDuplicatedComponentsInLog () {
        SimpleArrayMap<String, Integer> occurrences = new SimpleArrayMap<>();
        String[] components = new String[mAppfilterItems.size()];
        for (int i = 0; i < mAppfilterItems.size(); i++) {
            components[i] = mAppfilterItems.get(i).getCompleteComponent();
        }
        // TODO Make this work properly
        int count = 0;
        for (String word : components) {
            count = occurrences.get(word) == null ? 0 : occurrences.get(word);
            occurrences.put(word, count + 1);
        }
        for (int j = 0; j < occurrences.size(); j++) {
            String word = occurrences.keyAt(j);
            if (count > 0) {
                Timber.d("Duplicated component: \'%s\' - %d times", word, count);
            }
        }
        Timber.d("----- END OF APPFILTER DEBUG -----");
    }

}