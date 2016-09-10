package jahirfiquitiva.iconshowcase.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pitchedapps.capsule.library.fragments.CapsuleFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.events.OnLoadEvent;

/**
 * Created by Allan Wang on 2016-09-10.
 */
public abstract class EventBaseFragment extends CapsuleFragment {

    protected View loadingView (LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.loading_section, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    protected void eventUnregister() {
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribed(OnLoadEvent event) {
        if (event.type != eventType()) return;
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.main, this).commit();
//        onLoadEvent(event);
    }

    protected abstract OnLoadEvent.Type eventType();

//    protected abstract void onLoadEvent(OnLoadEvent event);

    @Override
    public void onStop() {
        eventUnregister();
        super.onStop();
    }

}
