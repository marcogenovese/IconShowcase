package jahirfiquitiva.iconshowcase.activities.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public abstract class TasksActivity extends BaseActivity implements LoadIconsLists.IIconList, LoadRequestList.IRequestList {

    protected ArrayList<IconItem> mPreviewIconList;
    protected ArrayList<IconsCategory> mCategoryList;
    protected ArrayList<RequestItem> mRequestList;
    private boolean tasksExecuted = false;

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
        if (getDrawerMap().containsKey(DrawerType.REQUESTS))
            new LoadRequestList(this, this).execute();
    }

}
