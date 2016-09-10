package jahirfiquitiva.iconshowcase.holders;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.events.OnLoadEvent;
import jahirfiquitiva.iconshowcase.models.IconItem;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public class HomePreviewList {
    private static ArrayList<IconItem> previewList;

    public static void createList(@NonNull ArrayList<IconItem> previewList) {
        HomePreviewList.previewList = previewList;
        EventBus.getDefault().post(new OnLoadEvent(OnLoadEvent.Type.HOMEPREVIEWS));
    }

    public static ArrayList<IconItem> getList() {
        return previewList;
    }

    public static void clearList() {
        previewList = null;
    }

    public static boolean hasList() {
        return previewList != null && !previewList.isEmpty();
    }

}
