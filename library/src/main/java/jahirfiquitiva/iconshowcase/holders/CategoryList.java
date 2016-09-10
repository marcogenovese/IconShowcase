package jahirfiquitiva.iconshowcase.holders;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.events.OnLoadEvent;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.IconsCategory;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class CategoryList {
    private static ArrayList<IconsCategory> categoryList;

    public static void createList(@NonNull ArrayList<IconsCategory> categoryList) {
        CategoryList.categoryList = categoryList;
        EventBus.getDefault().post(new OnLoadEvent(OnLoadEvent.Type.PREVIEWS));
    }

    public static ArrayList<IconsCategory> getList() {
        return categoryList;
    }

    public static void clearList() {
        categoryList = null;
    }

    public static boolean hasList() {
        return categoryList != null && !categoryList.isEmpty();
    }

}
