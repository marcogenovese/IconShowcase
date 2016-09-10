package jahirfiquitiva.iconshowcase.holders;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.events.OnLoadEvent;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public abstract class ListHolderFrame<T> {

    private ArrayList<T> mList;

    public abstract OnLoadEvent.Type getEventType();

    public void createList(@NonNull ArrayList<T> list) {
        mList = list;
        EventBus.getDefault().post(new OnLoadEvent(getEventType()));
    }

    public ArrayList<T> getList() {
        return mList;
    }

    public void clearList() {
        mList = null;
    }

    public boolean hasList() {
        return mList != null && !mList.isEmpty();
    }

    public boolean isEmpty() {
        return !hasList();
    }
}
