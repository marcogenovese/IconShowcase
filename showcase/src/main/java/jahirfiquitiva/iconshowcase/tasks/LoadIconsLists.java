package jahirfiquitiva.iconshowcase.tasks;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;
import jahirfiquitiva.iconshowcase.models.IconsLists;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class LoadIconsLists extends AsyncTask<Void, String, Boolean> {

    private Context context;
    public static ArrayList<IconsLists> iconsLists;
    public static ArrayList<IconsCategory> categories;
    long startTime, endTime;

    public LoadIconsLists(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Resources r = context.getResources();
        String p = context.getPackageName();

        iconsLists = new ArrayList<>();

        String[] newIcons = r.getStringArray(R.array.changelog_icons);
        List<String> newIconsL = sortList(newIcons);

        ArrayList<IconItem> changelogIconsArray = new ArrayList<>();
        for (String icon : newIconsL) {
            int iconResId = getIconResId(r, p, icon);
            if (iconResId != 0) {
                changelogIconsArray.add(new IconItem(icon, iconResId));
            }
        }
        iconsLists.add(new IconsLists("Changelog", changelogIconsArray));

        String[] prev = r.getStringArray(R.array.preview);
        List<String> previewIconsL = sortList(prev);
        ArrayList<IconItem> previewIconsArray = new ArrayList<>();
        for (String icon : previewIconsL) {
            int iconResId = getIconResId(r, p, icon);
            if (iconResId != 0) {
                previewIconsArray.add(new IconItem(icon, iconResId));
            }
        }
        iconsLists.add(new IconsLists("Previews", previewIconsArray));

        String[] tabsNames = r.getStringArray(R.array.tabs);
        categories = new ArrayList<>();

        ArrayList<IconItem> allIcons = new ArrayList<>();

        for (String tabName : tabsNames) {
            int arrayId = r.getIdentifier(tabName, "array", p);
            String[] icons = r.getStringArray(arrayId);
            List<String> iconsList = sortList(icons);

            ArrayList<IconItem> iconsArray = new ArrayList<>();

            for (String iconName : iconsList) {
                int iconResId = getIconResId(r, p, iconName);
                if (iconResId != 0) {
                    iconsArray.add(new IconItem(iconName, iconResId));
                    if (context.getResources().getBoolean(R.bool.auto_generate_all_icons)) {
                        allIcons.add(new IconItem(iconName, iconResId));
                    }
                }
            }

            categories.add(new IconsCategory(Utils.makeTextReadable(tabName), iconsArray));

        }

        if (context.getResources().getBoolean(R.bool.auto_generate_all_icons)) {
            categories.add(new IconsCategory("All", getAllIconsList(r, p, allIcons)));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean worked) {
        endTime = System.currentTimeMillis();
        Utils.showLog(context, "Load of icons task completed succesfully in: " + String.valueOf((endTime - startTime)) + " millisecs.");
    }

    private List<String> sortList(String[] array) {
        List<String> list = new ArrayList<>(Arrays.asList(array));
        Collections.sort(list);
        return list;
    }

    private List<String> sortAndOrganizeList(String[] array) {
        List<String> list = new ArrayList<>(Arrays.asList(array));
        Collections.sort(list);

        Set<String> noDuplicatesList = new HashSet<>();
        noDuplicatesList.addAll(list);

        List<String> newList = new ArrayList<>();
        newList.addAll(noDuplicatesList);
        Collections.sort(newList);

        return newList;
    }

    private ArrayList<IconItem> getAllIconsList(Resources r, String p,
                                                ArrayList<IconItem> initialList) {

        ArrayList<String> allIconsNames = new ArrayList<>();

        for (int i = 0; i < initialList.size(); i++) {
            allIconsNames.add(initialList.get(i).getName());
        }

        Collections.sort(allIconsNames);

        Set<String> noDuplicates = new HashSet<>();
        noDuplicates.addAll(allIconsNames);
        allIconsNames.clear();
        allIconsNames.addAll(noDuplicates);

        ArrayList<IconItem> sortedListArray = new ArrayList<>();

        for (int j = 0; j < allIconsNames.size(); j++) {
            int resId = getIconResId(r, p, allIconsNames.get(j));
            if (resId != 0) {
                sortedListArray.add(new IconItem(allIconsNames.get(j), resId));
            }
        }

        return sortedListArray;

    }

    private int getIconResId(Resources r, String p, String name) {
        int res = r.getIdentifier(name, "drawable", p);
        if (res != 0) {
            return res;
        } else {
            Utils.showLog(context, "missing icon: " + name);
            return 0;
        }
    }

    public static ArrayList<IconsLists> getIconsLists() {
        return iconsLists.size() > 0 ? iconsLists : null;
    }

    public static ArrayList<IconsCategory> getIconsCategories() {
        return categories.size() > 0 ? categories : null;
    }

}