package jahirfiquitiva.iconshowcase.utilities;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.iconshowcase.models.RequestItem;
import jahirfiquitiva.iconshowcase.models.WallpapersList;
import jahirfiquitiva.iconshowcase.tasks.LoadAppsToRequest;
import jahirfiquitiva.iconshowcase.tasks.LoadIconsLists;

public class ApplicationBase extends Application {

    private Context context;
    private Preferences mPrefs;

    // Main list off all apps.
    public static ArrayList<RequestItem> allApps;

    // Main list off all apps to request.
    public static ArrayList<RequestItem> allAppsToRequest;

    public static ArrayList<Integer> wallpapersArray;
    public static int wallpaper;

    @Override
    public void onCreate() {
        super.onCreate();

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