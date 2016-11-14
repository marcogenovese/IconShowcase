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
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
    private IconsCategory category;
    private ArrayList<IconsCategory> mCategoryList = new ArrayList<>();
    private long startTime, endTime;

    public LoadIconsLists(Context context) {
        mContext = new WeakReference<>(context);
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Resources r = mContext.get().getResources();
        String p = mContext.get().getPackageName();

        String[] previews = r.getStringArray(R.array.preview);
        for (String icon : previews) {
            int iconResId = Utils.getIconResId(r, p, icon);
            if (iconResId > 0) {
                mPreviewIcons.add(new IconItem(icon, iconResId));
            }
        }

        XmlResourceParser xmlParser = null;

        ArrayList<IconItem> icons = new ArrayList<>();

        try {
            xmlParser = r.getXml(R.xml.drawable);

            int event = xmlParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String title = "";
                switch (event) {
                    case XmlPullParser.START_TAG:
                        String name = xmlParser.getName();
                        if (name.equals("category")) {
                            if (category != null && icons.size() > 0) {
                                category.setIconsOfCategory(sortIconsList(r, p, icons));
                                mCategoryList.add(category);
                            }
                            title = xmlParser.getAttributeValue(null, "title");
                            if (title.length() > 1) {
                                category = new IconsCategory(title);
                            }
                            icons.clear();
                        } else if (name.equals("item")) {
                            if (category != null) {
                                String iconName = xmlParser.getAttributeValue(null, "drawable");
                                int iconResId = Utils.getIconResId(r, p, iconName);
                                if (iconResId > 0) {
                                    icons.add(new IconItem(iconName, iconResId));
                                } else {
                                    Timber.d("Icon: " + iconName + " could not be found." +
                                            " Make sure you added it to resources.");
                                }
                            }
                        }
                        break;
                }
                event = xmlParser.next();
            }
        } catch (XmlPullParserException | IOException ex) {
            ex.printStackTrace();
        } finally {
            if (category != null && icons.size() > 0) {
                category.setIconsOfCategory(sortIconsList(r, p, icons));
                mCategoryList.add(category);
            }
            if (xmlParser != null)
                xmlParser.close();
        }
        return (!(mPreviewIcons.isEmpty())) && (!(mCategoryList.isEmpty()));
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        //TODO onPostExecute only executes if task is not cancelled, worked boolean may not be necessary
        if (worked) {
            Timber.d("Load of icons task completed successfully in: %d milliseconds", (endTime - startTime));
            FullListHolder.get().home().createList(mPreviewIcons);
            FullListHolder.get().iconsCategories().createList(mCategoryList);
        }
    }

    private ArrayList<IconItem> sortIconsList(Resources r, String p, ArrayList<IconItem> icons) {
        List<String> list = new ArrayList<>();
        for (IconItem icon : icons) {
            list.add(icon.getName());
        }
        Collections.sort(list);
        Set<String> noDuplicates = new HashSet<>();
        noDuplicates.addAll(list);
        list.clear();
        list.addAll(noDuplicates);
        ArrayList<IconItem> nIcons = new ArrayList<>();
        for (String iconName : list) {
            int iconResId = Utils.getIconResId(r, p, iconName);
            if (iconResId > 0) {
                nIcons.add(new IconItem(iconName, iconResId));
            }
        }
        return nIcons;
    }

}