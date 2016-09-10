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
import android.content.res.Resources;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.holders.FullListHolder;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import timber.log.Timber;

public class LoadIconsLists extends AsyncTask<Void, String, Boolean> {

    private final WeakReference<Context> mContext;

    private ArrayList<IconItem> mPreviewIcons = new ArrayList<>();
    private ArrayList<IconsCategory> mCategoryList = new ArrayList<>();
    private long startTime, endTime;

    public LoadIconsLists (Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute () {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground (Void... params) {

        boolean worked;

        Resources r = mContext.get().getResources();
        String p = mContext.get().getPackageName();

        int iconResId;

        String[] prev = r.getStringArray(R.array.preview);
        List<String> previewIconsL = sortList(prev);

        for (String icon : previewIconsL) {
            iconResId = Utils.getIconResId(r, p, icon);
            if (iconResId != 0) {
                mPreviewIcons.add(new IconItem(icon, iconResId));
            }
        }

        String[] tabsNames = r.getStringArray(R.array.tabs);
        ArrayList<IconItem> allIcons = new ArrayList<>();

        for (String tabName : tabsNames) {
            int arrayId = r.getIdentifier(tabName, "array", p);
            String[] icons = null;

            try {
                icons = r.getStringArray(arrayId);
            } catch (Resources.NotFoundException e) {
                Timber.d("Couldn't find array: " + tabName);
            }

            if (icons != null && icons.length > 0) {
                List<String> iconsList = sortList(icons);

                ArrayList<IconItem> iconsArray = new ArrayList<>();

                for (int j = 0; j < iconsList.size(); j++) {
                    iconResId = Utils.getIconResId(r, p, iconsList.get(j));
                    if (iconResId != 0) {
                        iconsArray.add(new IconItem(iconsList.get(j), iconResId));
                        if (mContext.get().getResources().getBoolean(R.bool.auto_generate_all_icons)) {
                            allIcons.add(new IconItem(iconsList.get(j), iconResId));
                        }
                    }
                }

                mCategoryList.add(new IconsCategory(Utils.makeTextReadable(tabName), iconsArray));
            }
        }

        if (mContext.get().getResources().getBoolean(R.bool.auto_generate_all_icons)) {
            ArrayList<IconItem> allTheIcons = getAllIconsList(r, p, allIcons);
            if (allTheIcons.size() > 0) {
                mCategoryList.add(new IconsCategory("All", allTheIcons));
                worked = true;
            } else {
                worked = false;
            }
        } else {
            String[] allIconsArray = r.getStringArray(R.array.icon_pack);
            if (allIconsArray.length > 0) {
                mCategoryList.add(new IconsCategory("All", sortAndOrganizeList(r, p, allIconsArray)));
                worked = true;
            } else {
                worked = false;
            }
        }
        endTime = System.currentTimeMillis();
        return worked;
    }

    @Override
    protected void onPostExecute (Boolean worked) {
        //TODO onPostExecute only executes if task is not cancelled, worked boolean may not be necessary
        if (worked) {
            Timber.d("Load of icons task completed successfully in: %d milliseconds", (endTime - startTime));
        }

        FullListHolder.get().preview().createList(mCategoryList);
        FullListHolder.get().home().createList(mPreviewIcons);
    }

    private List<String> sortList (String[] array) {
        List<String> list = new ArrayList<>(Arrays.asList(array));
        Collections.sort(list);
        return list;
    }

    private ArrayList<IconItem> sortAndOrganizeList (Resources r, String p, String[] array) {

        List<String> list = sortList(array);

        Set<String> noDuplicates = new HashSet<>();
        noDuplicates.addAll(list);
        list.clear();
        list.addAll(noDuplicates);
        Collections.sort(list);

        ArrayList<IconItem> sortedListArray = new ArrayList<>();

        for (int j = 0; j < list.size(); j++) {
            int resId = Utils.getIconResId(r, p, list.get(j));
            if (resId != 0) {
                sortedListArray.add(new IconItem(list.get(j), resId));
            }
        }

        return sortedListArray;
    }

    private ArrayList<IconItem> getAllIconsList (Resources r, String p,
                                                 ArrayList<IconItem> initialList) {

        String[] allIconsNames = new String[initialList.size()];

        for (int i = 0; i < initialList.size(); i++) {
            allIconsNames[i] = initialList.get(i).getName();
        }

        return sortAndOrganizeList(r, p, allIconsNames);
    }

}