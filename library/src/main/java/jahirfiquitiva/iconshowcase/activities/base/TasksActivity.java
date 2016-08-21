package jahirfiquitiva.iconshowcase.activities.base;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.CallSuper;

import com.pitchedapps.butler.library.icon.request.App;
import com.pitchedapps.butler.library.icon.request.AppsLoadCallback;
import com.pitchedapps.butler.library.icon.request.AppsSelectionListener;
import com.pitchedapps.butler.library.icon.request.IconRequest;
import com.pitchedapps.butler.library.icon.request.RequestSendCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jahirfiquitiva.iconshowcase.BuildConfig;
import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.enums.DrawerType;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;
import jahirfiquitiva.iconshowcase.models.RequestItem;
import jahirfiquitiva.iconshowcase.tasks.LoadIconsLists;
import jahirfiquitiva.iconshowcase.tasks.LoadRequestList;
import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-08-20.
 */
public abstract class TasksActivity extends BaseActivity implements LoadIconsLists.IIconList, LoadRequestList.IRequestList, AppsLoadCallback, RequestSendCallback, AppsSelectionListener {

    protected ArrayList<IconItem> mPreviewIconList;
    protected ArrayList<IconsCategory> mCategoryList;
    protected ArrayList<RequestItem> mRequestList;
    private boolean tasksExecuted = false;
    private long start, end;

    protected abstract HashMap<DrawerType, Integer> getDrawerMap();

    protected abstract void iconsLoaded();

    protected abstract void requestListLoaded();

    @Override
    public void onLoadComplete(ArrayList<IconItem> previewIcons, ArrayList<IconsCategory> categoryList) {
        mPreviewIconList = previewIcons;
        mCategoryList = categoryList;
        iconsLoaded();
    }

    @Override
    public void onListLoaded(ArrayList<RequestItem> appList) {
        mRequestList = appList;
        requestListLoaded();
    }

    //TODO fix up booleans
    protected void startTasks(boolean justIcons, boolean justWalls) {
        if (tasksExecuted)
            Timber.w("startTasks() executed more than once; please remove duplicates");
        tasksExecuted = true;
        HashMap<DrawerType, Integer> drawerMap = getDrawerMap();

        if (justIcons) new LoadIconsLists(this, this).execute();
        if (getDrawerMap().containsKey(DrawerType.REQUESTS)) {
            if (IconRequest.get() == null) {
                start = System.currentTimeMillis();
                Timber.e("HERE");
                IconRequest request = IconRequest.start(this)
                        .withHeader("Hey, testing Icon Request!")
                        .withFooter("%s Version: %s", getString(R.string.app_name), BuildConfig.VERSION_NAME)
                        .withSubject("Icon Request - Just a Test")
                        .toEmail("fake-email@fake-website.com")
                        .saveDir(new File(Environment.getExternalStorageDirectory(), "Icons_Showcase/Icon_Request")) //TODO update
                        .includeDeviceInfo(true) // defaults to true anyways
                        .generateAppFilterXml(true) // defaults to true anyways
                        .generateAppFilterJson(false)
                        .loadCallback(this)
                        .sendCallback(this)
                        .selectionCallback(this)
                        .filterOff()
                        .build();
                request.loadApps();
            }
        }

    }

    @Override
    public void onLoadingFilter() {
    }

    @Override
    public void onAppsLoaded(ArrayList<App> apps, Exception e) {
        end = System.currentTimeMillis();
        Timber.e("LOAD TIME %d MS", end - start);
        requestListLoaded();
    }

    @Override
    public void onAppsLoadProgress(int percent) {
    }

    @Override
    public void onRequestPreparing() {

    }

    @Override
    public void onRequestError(Exception e) {

    }

    @Override
    public void onRequestSent() {

    }

    @Override
    public void onAppSelectionChanged(int selectedCount) {

    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        IconRequest.saveInstanceState(outState);
    }

}
