/*
 * Copyright (c) 2017 Jahir Fiquitiva
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

package jahirfiquitiva.iconshowcase.activities.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.CallSuper;

import com.pitchedapps.butler.iconrequest.IconRequest;
import com.pitchedapps.butler.iconrequest.RequestsCallback;

import java.io.File;
import java.util.concurrent.TimeUnit;

import jahirfiquitiva.iconshowcase.BuildConfig;
import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.config.Config;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.logging.CrashReportingTree;
import jahirfiquitiva.iconshowcase.tasks.DownloadJSON;
import jahirfiquitiva.iconshowcase.tasks.LoadIconsLists;
import jahirfiquitiva.iconshowcase.tasks.LoadKustomFiles;
import jahirfiquitiva.iconshowcase.tasks.LoadZooperWidgets;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import timber.log.Timber;

import static com.pitchedapps.butler.iconrequest.IconRequest.STATE_TIME_LIMITED;

/**
 * Created by Allan Wang on 2016-08-20.
 */
public abstract class TasksActivity extends DrawerActivity {

    protected Preferences mPrefs;
    private boolean tasksExecuted = false;
    private DownloadJSON jsonTask;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Config.init(this);

        if (BuildConfig.DEBUG || Config.get().allowDebugging()) {
            Timber.plant(new Timber.DebugTree());
        } else {
            //Disable debug & verbose logging on release
            Timber.plant(new CrashReportingTree());
        }

        mPrefs = new Preferences(this);
        if (savedInstanceState != null)
            IconRequest.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        Config.deinit();
        super.onDestroy();
    }

    //TODO fix up booleans
    protected void startTasks() {
        Timber.d("Starting tasks");
        if (tasksExecuted)
            Timber.w("startTasks() executed more than once; please remove duplicates");
        tasksExecuted = true;
        if (drawerHas(DrawerItem.PREVIEWS))
            new LoadIconsLists(this).execute();
        if (drawerHas(DrawerItem.WALLPAPERS)) {
            jsonTask = new DownloadJSON(
                    //                    new ShowcaseActivity.WallsListInterface() {
                    //                @Override
                    //                public void checkWallsListCreation(boolean result) {
                    //                    if (WallpapersFragment.mSwipeRefreshLayout != null) {
                    //                        WallpapersFragment.mSwipeRefreshLayout.setEnabled
                    // (false);
                    //                        WallpapersFragment.mSwipeRefreshLayout
                    // .setRefreshing(false);
                    //                    }
                    //                    if (WallpapersFragment.mAdapter != null) {
                    //                        WallpapersFragment.mAdapter.notifyDataSetChanged();
                    //                    }
                    //                }
                    //            },
                    this);
            try {
                jsonTask.execute();
            } catch (Exception e) {
            }
        }
        if (drawerHas(DrawerItem.REQUESTS)) {
            //mPrefs.resetRequestsLeft(this);
            PackageInfo appInfo = null;
            try {
                appInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                Timber.d(e.getMessage());
            }
            Preferences mPrefs = new Preferences(this);
            IconRequest.start(this)
                    .withAppName(getString(R.string.app_name))
                    .withFooter("%s Version: %s \n IconShowcase lib version: %s", getString(R
                                    .string.app_name), appInfo != null ? appInfo.versionName : "1" +
                                    ".0",
                            BuildConfig.VERSION_NAME)
                    .withSubject(s(R.string.request_title))
                    .toEmail(s(R.string.email_id))
                    .saveDir(new File(getString(R.string.request_save_location, Environment
                            .getExternalStorageDirectory())))
                    .generateAppFilterJson(false)
                    .debugMode(Config.get().allowDebugging())
                    .filterXmlId(R.xml.appfilter)
                    //.filterOff() //TODO switch
                    .withTimeLimit(getResources().getInteger(R.integer.time_limit_in_minutes),
                            mPrefs.getPrefs())
                    .maxSelectionCount(getResources().getInteger(R.integer.max_apps_to_request))
                    .setCallback(new RequestsCallback() {
                        @Override
                        public void onRequestLimited(Context context, int reason, int appsLeft,
                                                     long millis) {
                            try {
                                if (reason == STATE_TIME_LIMITED && millis > 0) {
                                    ISDialogs.showRequestTimeLimitDialog(context, getResources()
                                                    .getInteger(R.integer.time_limit_in_minutes),
                                            TimeUnit.MILLISECONDS.toMinutes(millis));
                                } else {
                                    ISDialogs.showRequestLimitDialog(context, appsLeft);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestEmpty(Context context) {
                            try {
                                ISDialogs.showNoSelectedAppsDialog(context);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    })
                    .build().loadApps();
        }
        if (drawerHas(DrawerItem.ZOOPER)) {
            WITH_ZOOPER_SECTION = true;
            new LoadZooperWidgets(this, null).execute();
        }
        if (drawerHas(DrawerItem.KUSTOM)) {
            new LoadKustomFiles(this).execute();
        }
    }

    private boolean drawerHas(DrawerItem item) {
        return mDrawerMap.containsKey(item);
    }

    public DownloadJSON getJsonTask() {
        return jsonTask;
    }

    public void setJsonTask(DownloadJSON jsonTask) {
        this.jsonTask = jsonTask;
    }

    //    @Subscribe
    //    public void onAppsLoaded(AppLoadedEvent event) {
    //        IconRequest.get().loadHighResIcons(); //Takes too much memory
    //    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        IconRequest.saveInstanceState(outState);
    }

    //    @Override
    //    public void onStart() {
    //        super.onStart();
    //        EventBus.getDefault().register(this);
    //    }
    //
    //    @Override
    //    public void onStop() {
    //        EventBus.getDefault().unregister(this);
    //        super.onStop();
    //    }

}
