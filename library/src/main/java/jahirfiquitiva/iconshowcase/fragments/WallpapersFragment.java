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

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.adapters.WallpapersAdapter;
import jahirfiquitiva.iconshowcase.dialogs.AdviceDialog;
import jahirfiquitiva.iconshowcase.enums.DrawerItem;
import jahirfiquitiva.iconshowcase.events.OnLoadEvent;
import jahirfiquitiva.iconshowcase.holders.FullListHolder;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import jahirfiquitiva.iconshowcase.views.GridSpacingItemDecoration;

public class WallpapersFragment extends EventBaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerFastScroller fastScroller;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public WallpapersAdapter mAdapter;
    private ImageView noConnection;
    private Activity context;
    private GridSpacingItemDecoration gridSpacing;
    private int tintColor;

    @Override
    public void onFabClick(View v) {

    }

    @Override
    public int getTitleId() {
        return DrawerItem.WALLPAPERS.getTitleID();
    }

    @Override
    protected int getFabIcon() {
        return 0;
    }

    /**
     * Will hide the fab if false; the fab is still in the viewgroup and is used for various other tasks such as the snackbar
     *
     * @return
     */
    @Override
    protected boolean hasFab() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (FullListHolder.get().walls().isNull()) return loadingView(inflater, container);

        setHasOptionsMenu(true);
        context = getActivity();

        View layout = inflater.inflate(R.layout.wallpapers_section, container, false);

        tintColor = ThemeUtils.darkOrLight(context, R.color.drawable_tint_dark, R.color.drawable_tint_light);

        noConnection = (ImageView) layout.findViewById(R.id.no_connected_icon);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.wallsGrid);
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);

        if (!((ShowcaseActivity) getActivity()).isWallsPicker()) {
            AdviceDialog.show(getActivity(), AdviceDialog.Type.WALLPAPER);
        }

        noConnection.setImageDrawable(ColorUtils.getTintedIcon(
                context, R.drawable.ic_no_connection, tintColor));

        // showProgressBar();

        setupRecyclerView(false, 0);

//        mRecyclerView.setVisibility(View.GONE);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(tintColor);

        mSwipeRefreshLayout.setColorSchemeResources(
                ThemeUtils.darkOrLight(R.color.dark_theme_accent, R.color.light_theme_accent),
                ThemeUtils.darkOrLight(R.color.dark_theme_accent, R.color.light_theme_accent),
                ThemeUtils.darkOrLight(R.color.dark_theme_accent, R.color.light_theme_accent)); //TODO check if having three of the same colors makes a difference

        mSwipeRefreshLayout.setEnabled(false);

        //TODO: MAKE WALLPAPERS APPEAR AT FIRST. FOR SOME REASON ONLY APPEAR AFTER PRESSING "UPDATE" ICON IN TOOLBAR
        mAdapter = new WallpapersAdapter(getActivity(),
                FullListHolder.get().walls().getList());

        mRecyclerView.setAdapter(mAdapter);
        fastScroller.attachRecyclerView(mRecyclerView);

        if (Utils.hasNetwork(context)) {
            noConnection.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            fastScroller.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (FullListHolder.get().walls().getList().isEmpty()) {
            noConnection.setImageDrawable(ColorUtils.getTintedIcon(
                    context, R.drawable.ic_no_connection,
                    tintColor));
            hideStuff();
        }

        return layout;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.wallpapers, menu);
    }

    private void hideStuff() {
        noConnection.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(boolean updating, int newColumns) {

        Preferences mPrefs = new Preferences(context);
        if (updating && gridSpacing != null) {
            mPrefs.setWallsColumnsNumber(newColumns);
            mRecyclerView.removeItemDecoration(gridSpacing);
        }

        int columnsNumber = mPrefs.getWallsColumnsNumber();
        if (context.getResources().getConfiguration().orientation == 2) {
            columnsNumber += 2;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(context,
                columnsNumber));
        gridSpacing = new GridSpacingItemDecoration(columnsNumber,
                context.getResources().getDimensionPixelSize(R.dimen.lists_padding),
                true);
        mRecyclerView.addItemDecoration(gridSpacing);
        mRecyclerView.setHasFixedSize(true);

        if (mRecyclerView.getVisibility() != View.VISIBLE) {
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        if (mRecyclerView.getAdapter() != null) {
            fastScroller.attachRecyclerView(mRecyclerView);
            if (fastScroller.getVisibility() != View.VISIBLE) {
                fastScroller.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateRecyclerView(int newColumns) {
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        setupRecyclerView(true, newColumns);
    }

    public void refreshWalls(Context context) {
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        int stringId;
        if (Utils.hasNetwork(context)) {
            stringId = R.string.refreshing_walls;
        } else {
            stringId = R.string.no_conn_title;
        }
        Snackbar snackbar = snackbarCustom(getString(stringId), Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(ThemeUtils.darkOrLight(context, R.color.snackbar_dark, R.color.snackbar_light));
        snackbar.show();
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    protected OnLoadEvent.Type eventType() {
        return OnLoadEvent.Type.WALLPAPERS;
    }
}