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
    public void onLoadComplete (ArrayList<IconItem> previewIcons, ArrayList<IconsCategory> categoryList) {
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
