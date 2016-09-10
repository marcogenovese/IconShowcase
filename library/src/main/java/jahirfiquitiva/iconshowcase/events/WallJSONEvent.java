package jahirfiquitiva.iconshowcase.events;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.models.WallpaperItem;

/**
 * Created by Allan Wang on 2016-09-06.
 */
public class WallJSONEvent {

    public final ArrayList<WallpaperItem> walls;

    public WallJSONEvent(ArrayList<WallpaperItem> list) {
        walls = list;
    }
}
