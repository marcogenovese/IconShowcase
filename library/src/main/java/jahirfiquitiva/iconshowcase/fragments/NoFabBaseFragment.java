package jahirfiquitiva.iconshowcase.fragments;

import android.view.View;

/**
 * Created by Allan Wang on 2016-08-19.
 */
public class NoFabBaseFragment extends BaseFragment {
    @Override
    public void onFabClick(View v) {

    }

    @Override
    int getFabIcon() {
        return 0;
    }

    @Override
    boolean hasFab() {
        return false;
    }
}
