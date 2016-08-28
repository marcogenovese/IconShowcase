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

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;

import com.pitchedapps.butler.library.icon.request.AppLoadedEvent;
import com.pitchedapps.butler.library.icon.request.IconRequest;
import com.pitchedapps.capsule.library.activities.CapsuleActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jahirfiquitiva.iconshowcase.BuildConfig;
import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.config.Config;
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
public abstract class TasksActivity extends CapsuleActivity implements LoadIconsLists.IIconList {

    protected ArrayList<IconItem> mPreviewIconList;
    protected ArrayList<IconsCategory> mCategoryList;
    private boolean tasksExecuted = false;

    protected abstract HashMap<DrawerType, Integer> getDrawerMap();

    protected abstract void iconsLoaded();

    @Override
    public void onLoadComplete(ArrayList<IconItem> previewIcons, ArrayList<IconsCategory> categoryList) {
        mPreviewIconList = previewIcons;
        mCategoryList = categoryList;
        iconsLoaded();
    }

    //TODO fix up booleans
    protected void startTasks() {
        Timber.d("Starting tasks");
        if (tasksExecuted)
            Timber.w("startTasks() executed more than once; please remove duplicates");
        tasksExecuted = true;
        if (getDrawerMap().containsKey(DrawerType.PREVIEWS))
            new LoadIconsLists(this, this).execute();
        if (getDrawerMap().containsKey(DrawerType.REQUESTS)) {
            IconRequest.start(this)
//                        .withHeader("Hey, testing Icon Request!")
                    .withFooter("%s Version: %s", getString(R.string.app_name), BuildConfig.VERSION_NAME)
                    .withSubject(s(R.string.request_title))
                    .toEmail(s(R.string.email_id))
                    .saveDir(new File(getString(R.string.request_save_location, Environment.getExternalStorageDirectory())))
                    .includeDeviceInfo(true) // defaults to true anyways
                    .generateAppFilterXml(true) // defaults to true anyways
                    .generateAppFilterJson(false)
//                        .sendCallback(this)
//                        .selectionCallback(this) //TODO add this? and add max cap
                    .debugMode(Config.get().allowDebugging())
                    .filterOff() //TODO switch
                    .build().loadApps();
        }

    }

    @Subscribe
    public void onAppsLoaded(AppLoadedEvent event) {
        IconRequest.get().loadHighResIcons();
    }

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (savedInstanceState != null)
            IconRequest.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    @CallSuper
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        IconRequest.saveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
