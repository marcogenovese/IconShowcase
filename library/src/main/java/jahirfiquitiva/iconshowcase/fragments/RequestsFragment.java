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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pitchedapps.butler.library.icon.request.AppLoadedEvent;
import com.pitchedapps.butler.library.icon.request.IconRequest;
import com.pitchedapps.capsule.library.event.CFabEvent;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.activities.base.DrawerActivity;
import jahirfiquitiva.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.views.GridSpacingItemDecoration;
import timber.log.Timber;

public class RequestsFragment extends CapsuleFragment {

    public static RequestsAdapter mAdapter;
    private ViewGroup mViewGroup;
    private RelativeLayout mLoadingView;
    //private TextView mLoadingText;
    private RecyclerView mRecyclerView;
    private boolean subscribed = true;
    private int maxApps = 0, minutesLimit = 0; //TODO move to taskactivity

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View layout = inflater.inflate(R.layout.icon_request_section, container, false);

        int gridSpacing = getResources().getDimensionPixelSize(R.dimen.lists_padding);
        int columnsNumber = getResources().getInteger(R.integer.requests_grid_width);

        minutesLimit = getResources().getInteger(R.integer.limit_request_to_x_minutes);

        setHasOptionsMenu(true);

        hideFab();

        mViewGroup = (ViewGroup) layout.findViewById(R.id.viewgroup);
        mLoadingView = (RelativeLayout) layout.findViewById(R.id.loading_view);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.appsToRequestList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnsNumber));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(columnsNumber,
                        gridSpacing,
                        true));
        RecyclerFastScroller mFastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        mFastScroller.attachRecyclerView(mRecyclerView);

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
        if (!IconRequest.get().isLoading()) {
            switchToLoadedView();
            subscribed = false;
            Timber.d("Requests already loaded");
        } else {
            //mLoadingText = (TextView) layout.findViewById(R.id.loading_text);
            Timber.d("Requests still loading; subscribing to events");
            //            AppLoadingEvent stickyEvent = EventBus.getDefault().removeStickyEvent(AppLoadingEvent.class);
            //            if (stickyEvent != null) onAppsLoading(stickyEvent);
        }
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (subscribed) EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public int getTitleId() {
        return DrawerActivity.DrawerItem.REQUESTS.getTitleID();
    }

    @Nullable
    @Override
    protected CFabEvent updateFab() {
        return new CFabEvent(R.drawable.ic_email, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRequestProcess();
            }
        });
    }

    //    public static RequestsFragment newInstance(boolean isLoaded) {
    //        RequestsFragment fragment = new RequestsFragment();
    //        Bundle args = new Bundle();
    //        args.putBoolean("is_loaded", isLoaded);
    //        fragment.setArguments(args);
    //        return fragment;
    //    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.requests, menu);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppsLoaded(AppLoadedEvent event) { //TODO make use of exceptions provided in event
        switchToLoadedView();
        //        EventBus.getDefault().unregister(AppLoadingEvent.class);
    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    public void onAppsLoading(AppLoadingEvent event) {
    //        EventBus.getDefault().removeStickyEvent(AppLoadingEvent.class);
    //        if (loaded) return;
    //        mLoadingText.setText(event.getString());
    //    }

    private void switchToLoadedView() {
        mViewGroup.removeView(mLoadingView);
        mLoadingView = null;
        //mLoadingText = null;
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new RequestsAdapter();
        //        mRecyclerView.setItemAnimator(null);
        //        mRecyclerView.setAnimation(null);
        mRecyclerView.setAdapter(mAdapter);
        showFab();
    }

    public void startRequestProcess() {
        Preferences mPrefs = new Preferences(getActivity());
        if (getResources().getInteger(R.integer.max_apps_to_request) > -1) {
            if (mPrefs.getRequestsLeft() <= 0) {
                if (mAdapter.getSelectedApps() != null) {
                    if (mAdapter.getSelectedApps().size() < mPrefs.getRequestsLeft()) {
                        showRequestsFilesCreationDialog(getActivity(), mPrefs);
                    } else if ((Utils.canRequestXApps(getActivity(), minutesLimit, mPrefs) != -2)
                            || (minutesLimit <= 0)) {
                        showRequestsFilesCreationDialog(getActivity(), mPrefs);
                    } else {
                        ISDialogs.showRequestTimeLimitDialog(getActivity(), minutesLimit);
                    }
                }
            } else {
                showRequestsFilesCreationDialog(getActivity(), mPrefs);
            }
        } else {
            showRequestsFilesCreationDialog(getActivity(), mPrefs);
        }
    }

    private void showRequestsFilesCreationDialog(Context context, Preferences mPrefs) {
        if (mAdapter.getSelectedApps() != null && mAdapter.getSelectedApps().size() > 0) {
            if (!PermissionUtils.canAccessStorage(context)) {
                PermissionUtils.requestStoragePermission((ShowcaseActivity) context);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    ISDialogs.showPermissionNotGrantedDialog(context);
                } else {
                    final MaterialDialog dialog = ISDialogs.showBuildingRequestDialog(context);
                    if (getResources().getInteger(R.integer.max_apps_to_request) > -1) {
                        if (maxApps < 0) {
                            maxApps = 0;
                        }
                        if (mAdapter.getSelectedApps().size() <= mPrefs.getRequestsLeft()) {
                            dialog.show();
                            Calendar c = Calendar.getInstance();
                            Utils.saveCurrentTimeOfRequest(mPrefs, c);
                            IconRequest.get().send(new IconRequest.RequestReadyCallback() {
                                @Override
                                public void onRequestReady() {
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            ISDialogs.showRequestLimitDialog(context, maxApps);
                        }
                    } else {
                        dialog.show();
                        Calendar c = Calendar.getInstance();
                        Utils.saveCurrentTimeOfRequest(mPrefs, c);
                        IconRequest.get().send(new IconRequest.RequestReadyCallback() {
                            @Override
                            public void onRequestReady() {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        } else {
            ISDialogs.showNoSelectedAppsDialog(context);
        }
    }

}