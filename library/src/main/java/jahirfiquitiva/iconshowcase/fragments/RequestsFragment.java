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

package jahirfiquitiva.iconshowcase.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pitchedapps.butler.library.icon.request.AppLoadedEvent;
import com.pitchedapps.butler.library.icon.request.AppLoadingEvent;
import com.pitchedapps.butler.library.icon.request.IconRequest;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.iconshowcase.enums.DrawerType;
import jahirfiquitiva.iconshowcase.views.GridSpacingItemDecoration;

public class RequestsFragment extends CapsuleFragment {

    private ViewGroup mViewGroup;
    private RelativeLayout mLoadingView;
    private  TextView mLoadingText;
    private RecyclerView mRecyclerView;
    private RecyclerFastScroller mFastScroller;
    private boolean loaded = false, subscribed = true;
    private int maxApps = 0, minutesLimit = 0;

    @Override
    public void onStart() {
        super.onStart();
        if (subscribed) EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (subscribed) EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onFabClick(View v) {

    }

    @Override
    public int getTitleId() {
        return DrawerType.REQUESTS.getTitleID();
    }

    @Override
    protected int getFabIcon() {
        return R.drawable.ic_email;
    }

    @Override
    protected boolean hasFab() {
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.icon_request_section, container, false);

        int gridSpacing = getResources().getDimensionPixelSize(R.dimen.lists_padding);
        int columnsNumber = getResources().getInteger(R.integer.requests_grid_width);

        minutesLimit = getResources().getInteger(R.integer.limit_request_to_x_minutes);

        setHasOptionsMenu(true);

        mViewGroup = (ViewGroup) layout.findViewById(R.id.viewgroup);
        mLoadingView = (RelativeLayout) layout.findViewById(R.id.loading_view);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.appsToRequestList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnsNumber));
        mRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(columnsNumber,
                        gridSpacing,
                        true));
        mFastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    hideFab();
                } else {
                    showFab();
                }
            }
        });

        if (IconRequest.get().isLoaded()) {
            switchToLoadedView();
            subscribed = false;
        } else {
            mLoadingText = (TextView) layout.findViewById(R.id.text);
        }

        return layout;
    }

    @Subscribe
    public void onAppsLoaded(AppLoadedEvent event) {
        switchToLoadedView();
    }

    @Subscribe
    public void onAppsLoading(AppLoadingEvent event) {
        if (loaded) return;
        mLoadingText.setText(event.getString());
    }

    private void switchToLoadedView() {
        loaded = true;
        mViewGroup.removeView(mLoadingView);
        mLoadingView = null;
        mLoadingText = null;
        mRecyclerView.setVisibility(View.VISIBLE);
        RequestsAdapter mAdapter = new RequestsAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAnimation(null);
        mRecyclerView.setAdapter(mAdapter);
        mFastScroller.attachRecyclerView(mRecyclerView);
    }
}