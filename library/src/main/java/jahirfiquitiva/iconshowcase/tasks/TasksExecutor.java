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

import android.content.Context;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.Preferences;

public class TasksExecutor {

    // Context is always useful for some reason.
    private final Context context;

    // For storing & reading read data
    private final Preferences mPrefs;

    // Global singleton instance
    private static TasksExecutor singleton = null;

    private boolean justIcons, justWallpapers, includeZooper = false, includeKustom = false;

    public static TasksExecutor with (Context context) {
        if (singleton == null)
            singleton = new Builder(context).build();
        return singleton;
    }

    public void loadJust (boolean loadIcons, boolean loadWallpapers) {
        justIcons = loadIcons;
        justWallpapers = loadWallpapers;
    }

    protected static TasksExecutor getInstance () {
        return singleton;
    }

    public static void setSingleton (TasksExecutor singleton) {
        TasksExecutor.singleton = singleton;
    }

    private TasksExecutor (Context context, boolean justIcons, boolean justWallpapers) {
        this.context = context;
        this.mPrefs = new Preferences(context);
        this.justIcons = justIcons;
        this.justWallpapers = justWallpapers;
        for (String item : context.getResources().getStringArray(R.array.drawer_sections)) {
            if (!includeZooper) {
                includeZooper = item.equals("Zooper");
            }
            if (!includeKustom) {
                includeKustom = item.equals("Kustom");
            }
        }
        executeTasks();
    }

    private void executeTasks () {

        /*
        TODO: Optimize the order of execution and the moment these tasks are executed...
        Duration of tasks:
        * Load of icons: ~300-1200 millisecs.
        * Load of wallpapers: ~2 seconds.
        * Load of widgets: ~2000-3500 millisecs.
        * Load of kustom files: ~3500-5000 millisecs.
        * Load of apps to request: ~10 seconds.

         */

        if (justIcons) {
            //            new LoadIconsLists(context).execute();
        } else if (justWallpapers) {
            loadWallsList();
        } else {
            new LoadIconsLists(context).execute();
            loadWallsList();
            if (includeZooper) {
                new LoadZooperWidgets(context).execute();
            }
            if (includeKustom) {
                new LoadKustomFiles(context).execute();
            }

            //            if (!mPrefs.didAppsToRequestLoad() || RequestList.getRequestList() == null) {
            //                RequestsFragment.loadAppsToRequest = new LoadRequestList(context);
            //                RequestsFragment.loadAppsToRequest.execute();
            //            }
        }
    }

    private void loadWallsList () {
//        if (mPrefs.getWallsListLoaded()) {
//            WallpapersList.clearList();
//            mPrefs.setWallsListLoaded(!mPrefs.getWallsListLoaded());
//        }
//        new WallpapersFragment.DownloadJSON(new ShowcaseActivity.WallsListInterface() {
//            @Override
//            public void checkWallsListCreation (boolean result) {
//                mPrefs.setWallsListLoaded(result);
//                if (WallpapersFragment.mSwipeRefreshLayout != null) {
//                    WallpapersFragment.mSwipeRefreshLayout.setEnabled(false);
//                    WallpapersFragment.mSwipeRefreshLayout.setRefreshing(false);
//                }
//                if (WallpapersFragment.mAdapter != null) {
//                    WallpapersFragment.mAdapter.notifyDataSetChanged();
//                }
//            }
//        }, context).execute();
    }

    /**
     * Fluent API for creating {@link TasksExecutor} instances.
     */
    public static class Builder {

        private final Context context;
        private boolean justIcons, justWallpapers;

        /**
         * Start building a new {@link TasksExecutor} instance.
         */
        public Builder (Context context) {
            if (context == null)
                throw new IllegalArgumentException("Context must not be null!");

            this.context = context.getApplicationContext();
        }

        public Builder justIcons (boolean justIcons) {
            this.justIcons = justIcons;
            return this;
        }

        public Builder justWallpapers (boolean justWallpapers) {
            this.justWallpapers = justWallpapers;
            return this;
        }

        /**
         * Creates a {@link TasksExecutor} instance.
         */
        public TasksExecutor build () {
            return new TasksExecutor(context, justIcons, justWallpapers);
        }
    }

}
