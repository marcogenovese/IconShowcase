/*
 * Copyright (c) 2015. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.apps.iconshowcase.utilities;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.apps.iconshowcase.models.RequestItem;
import jahirfiquitiva.apps.iconshowcase.models.WallpapersList;
import jahirfiquitiva.apps.iconshowcase.tasks.LoadAppsToRequest;
import jahirfiquitiva.apps.iconshowcase.tasks.LoadIconsLists;

/**
 * Created by Jahir on 18/12/2015.
 */
public class ApplicationBase extends Application {

    private Context context;
    private Preferences mPrefs;

    /**
     * Stores the list of all applications for the all apps view.
     */

    // Main list off all apps.
    public static ArrayList<RequestItem> allApps;

    // Main list off all apps to request.
    public static ArrayList<RequestItem> allAppsToRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        //Fabric.with(this, new Crashlytics());

        this.context = getApplicationContext();

        mPrefs = new Preferences(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                new LoadIconsLists(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                loadWallsList();
                loadAppsForRequest();
            }
        }).start();

    }

    public Context getAppContext() {
        return this.context;
    }

    private void loadAppsForRequest() {
        if (mPrefs.getAppsToRequestLoaded()) {
            mPrefs.setAppsToRequestLoaded(!mPrefs.getAppsToRequestLoaded());
        }
        new LoadAppsToRequest(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void loadWallsList() {
        if (mPrefs.getWallsListLoaded()) {
            WallpapersList.clearList();
            mPrefs.setWallsListLoaded(!mPrefs.getWallsListLoaded());
        }
        WallpapersFragment.DownloadJSON downloadJSON = new WallpapersFragment.DownloadJSON(new ShowcaseActivity.WallsListInterface() {
            @Override
            public void checkWallsListCreation(boolean result) {
                mPrefs.setWallsListLoaded(result);
                if (WallpapersFragment.mSwipeRefreshLayout != null) {
                    WallpapersFragment.mSwipeRefreshLayout.setEnabled(false);
                    WallpapersFragment.mSwipeRefreshLayout.setRefreshing(false);
                }
                if (WallpapersFragment.mAdapter != null) {
                    WallpapersFragment.mAdapter.notifyDataSetChanged();
                }
            }
        }, context);
        downloadJSON.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}