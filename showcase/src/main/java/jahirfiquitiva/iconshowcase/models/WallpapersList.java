/*
 *
 */

package jahirfiquitiva.iconshowcase.models;

import java.util.ArrayList;

public class WallpapersList {

    public static ArrayList<WallpaperItem> wallsList = new ArrayList<>();

    public static void createWallpapersList(ArrayList<String> names, ArrayList<String> authors,
                                            ArrayList<String> urls, ArrayList<String> dimensions,
                                            ArrayList<String> copyrights) {
        try {
            for (int i = 0; i < names.size(); i++) {
                WallpaperItem wallItem =
                        new WallpaperItem(names.get(i), authors.get(i), urls.get(i),
                                dimensions.get(i), copyrights.get(i));
                wallsList.add(wallItem);
            }
        } catch (IndexOutOfBoundsException e) {
            //Do nothing
        }
    }

    public static ArrayList<WallpaperItem> getWallpapersList() {
        return wallsList;
    }

    public static void clearList() {
        wallsList.clear();
    }

}
