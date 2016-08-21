package jahirfiquitiva.iconshowcase.activities.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jahirfiquitiva.iconshowcase.enums.DrawerType;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;
import jahirfiquitiva.iconshowcase.tasks.LoadIconsLists;
import timber.log.Timber;

/**
 * Created by Allan Wang on 2016-08-20.
 */
public abstract class TasksActivity extends BaseActivity implements LoadIconsLists.IIconList {

    protected ArrayList<IconItem> mPreviewIconList;
    protected ArrayList<IconsCategory> mCategoryList;
    private boolean tasksExecuted = false;

    protected abstract void iconsLoaded(List<IconItem> previewIcons, List<IconsCategory> categoryList);
    protected abstract HashMap<DrawerType, Integer> getDrawerMap();

    @Override
    public void onLoadComplete(ArrayList<IconItem> previewIcons, ArrayList<IconsCategory> categoryList) {
        mPreviewIconList = previewIcons;
        mCategoryList = categoryList;
        iconsLoaded(mPreviewIconList, mCategoryList);
    }

    protected void startTasks() {
        if (tasksExecuted) Timber.w("startTasks() executed more than once; please remove duplicates");
        tasksExecuted = true;
        HashMap<DrawerType, Integer> drawerMap = getDrawerMap();

        new LoadIconsLists(this, this).execute();
    }

}
