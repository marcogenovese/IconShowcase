/*
 * Copyright (c) 2015. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.apps.iconshowcase.models.wallpapers;

import java.util.ArrayList;

public class WallpapersList {

    public static ArrayList<WallpaperItem> wallsList = new ArrayList<WallpaperItem>();

    public static void createWallpapersList(ArrayList<String> names, ArrayList<String> authors,
                                            ArrayList<String> urls) {
        try {
            for (int i = 0; i < names.size(); i++) {
                WallpaperItem wallItem = new WallpaperItem(names.get(i), authors.get(i), urls.get(i));
                wallsList.add(wallItem);
            }
        } catch (IndexOutOfBoundsException e) {

        }
    }

    public static ArrayList<WallpaperItem> getWallpapersList() {
        return wallsList;
    }

    public static void clearList() {
        wallsList.clear();
    }

}
