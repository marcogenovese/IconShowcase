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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pitchedapps.capsule.library.fragments.CapsuleFragment;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.AltWallpaperViewerActivity;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.activities.WallpaperViewerActivity;
import jahirfiquitiva.iconshowcase.adapters.WallpapersAdapter;
import jahirfiquitiva.iconshowcase.dialogs.WallpaperDialog;
import jahirfiquitiva.iconshowcase.enums.DrawerItem;
import jahirfiquitiva.iconshowcase.events.BlankEvent;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.models.WallpapersList;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import jahirfiquitiva.iconshowcase.views.GridSpacingItemDecoration;
import timber.log.Timber;

public class WallpapersFragment extends CapsuleFragment {

    private ViewGroup layout;
    private ProgressBar mProgress;
    private RecyclerView mRecyclerView;
    private RecyclerFastScroller fastScroller;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public WallpapersAdapter mAdapter;
    private ImageView noConnection;
    private Activity context;
    private GridSpacingItemDecoration gridSpacing;
    private int light, dark;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

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

    private static final String itemKey = "wallpaper_items";

    public static PreviewsFragment newInstance(@Nullable ArrayList<WallpaperItem> items) {
        PreviewsFragment fragment = new PreviewsFragment();
        if (items == null || items.isEmpty()) return fragment;
        Bundle args = new Bundle();
        args.putParcelableArrayList(itemKey, items);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        context = getActivity();

        layout = (ViewGroup) inflater.inflate(R.layout.wallpapers_section, container, false);

        light = ContextCompat.getColor(context, R.color.drawable_tint_dark);
        dark = ContextCompat.getColor(context, R.color.drawable_tint_light);

        noConnection = (ImageView) layout.findViewById(R.id.no_connected_icon);
        mProgress = (ProgressBar) layout.findViewById(R.id.progress);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.wallsGrid);
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);

        if (!ShowcaseActivity.wallsPicker) {
            showWallsAdviceDialog(getActivity());
        }

        noConnection.setImageDrawable(ColorUtils.getTintedIcon(
                context, R.drawable.ic_no_connection,
                ThemeUtils.darkTheme ? light : dark));
        noConnection.setVisibility(View.GONE);

        showProgressBar();

        setupRecyclerView(false, 0);

        mRecyclerView.setVisibility(View.GONE);

        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeUtils.darkTheme ? dark : light);

        mSwipeRefreshLayout.setColorSchemeResources(
                ThemeUtils.darkTheme ? R.color.dark_theme_accent : R.color.light_theme_accent, //TODO check if having three of the same colors makes a difference
                ThemeUtils.darkTheme ? R.color.dark_theme_accent : R.color.light_theme_accent,
                ThemeUtils.darkTheme ? R.color.dark_theme_accent : R.color.light_theme_accent);

        mSwipeRefreshLayout.setEnabled(false);

        //TODO: MAKE WALLPAPERS APPEAR AT FIRST. FOR SOME REASON ONLY APPEAR AFTER PRESSING "UPDATE" ICON IN TOOLBAR
        setupLayout();

        return layout;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.wallpapers, menu);
    }

    public void setupLayout() {

        //TODO: MAKE WALLPAPERS APPEAR AT FIRST. FOR SOME REASON ONLY APPEAR AFTER PRESSING "UPDATE" ICON IN TOOLBAR
        if (WallpapersList.getWallpapersList() != null && WallpapersList.getWallpapersList().size() > 0) {
            mAdapter = new WallpapersAdapter(context,
                    new WallpapersAdapter.ClickListener() {
                        @Override
                        public void onClick(WallpapersAdapter.WallsHolder view,
                                            int position, boolean longClick) {
                            if ((longClick && !ShowcaseActivity.wallsPicker) || ShowcaseActivity.wallsPicker) {
                                showApplyWallpaperDialog(context,
                                        WallpapersList.getWallpapersList().get(position));
                            } else {
                                final Intent intent = new Intent(context,
                                        context.getResources().getBoolean(R.bool.alternative_viewer) ? AltWallpaperViewerActivity.class :
                                                WallpaperViewerActivity.class);

                                intent.putExtra("item", WallpapersList.getWallpapersList().get(position));
                                intent.putExtra("transitionName", ViewCompat.getTransitionName(view.wall));

                                Bitmap bitmap;

                                if (view.wall.getDrawable() != null) {
                                    bitmap = Utils.drawableToBitmap(view.wall.getDrawable());

                                    try {
                                        String filename = "temp.png";
                                        FileOutputStream stream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        stream.close();
                                        intent.putExtra("image", filename);
                                    } catch (Exception e) {
                                        Timber.d("Error getting drawable", e.getLocalizedMessage());
                                    }

                                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, view.wall, ViewCompat.getTransitionName(view.wall));
                                    context.startActivity(intent, options.toBundle());
                                } else {
                                    context.startActivity(intent);
                                }
                            }
                        }
                    });

            mAdapter.setData(WallpapersList.getWallpapersList());

            if (layout != null) {

                mRecyclerView.setAdapter(mAdapter);

                fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);

                fastScroller.attachRecyclerView(mRecyclerView);

                noConnection = (ImageView) layout.findViewById(R.id.no_connected_icon);

                if (Utils.hasNetwork(context)) {
                    hideProgressBar();
                    noConnection.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    fastScroller.setVisibility(View.VISIBLE);
                    mSwipeRefreshLayout.setEnabled(false);
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                runOnUIThread(context, new Runnable() {
                    @Override
                    public void run() {
                        if (layout != null) {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUIThread(context, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (WallpapersList.getWallpapersList().size() <= 0) {
                                                noConnection.setImageDrawable(ColorUtils.getTintedIcon(
                                                        context, R.drawable.ic_no_connection,
                                                        ThemeUtils.darkTheme ? light : dark));
                                                hideStuff(noConnection);
                                            } else {
                                                hideProgressBar();
                                            }
                                        }
                                    });
                                }
                            }, 7500);
                        }
                    }
                });
            }
        }
    }

    private static Handler handler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private static void runOnUIThread(Context context, Runnable r) {
        handler(context).post(r);
    }

    private void hideStuff(ImageView noConnection) {
        if (mRecyclerView.getAdapter() != null) {
            fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
            fastScroller.attachRecyclerView(mRecyclerView);
        }
        hideProgressBar();
        if (noConnection != null) {
            noConnection.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void showProgressBar() {
        if (mProgress != null) {
            if (mProgress.getVisibility() != View.VISIBLE) {
                mProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideProgressBar() {
        if (mProgress != null) {
            if (mProgress.getVisibility() != View.GONE) {
                mProgress.setVisibility(View.GONE);
            }
        }
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

    @Subscribe
    public void asdf(BlankEvent event) {
        if (event.getCode() > 5) return;
        updateRecyclerView(event.getCode());
    }

    public void updateRecyclerView(int newColumns) {
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        showProgressBar();
        setupRecyclerView(true, newColumns);
        hideProgressBar();
    }

    public void refreshWalls(Context context) {
        hideProgressBar();
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
        if (Utils.hasNetwork(context)) {
            Utils.showSimpleSnackbar(context, layout,
                    context.getResources().getString(R.string.refreshing_walls));
        } else {
            Utils.showSimpleSnackbar(context, layout,
                    context.getResources().getString(R.string.no_conn_title));
        }
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    private void showWallsAdviceDialog(Context context) {
        final Preferences mPrefs = new Preferences(context);
        if (!mPrefs.getWallsDialogDismissed()) {
            new MaterialDialog.Builder(context)
                    .title(R.string.advice)
                    .content(R.string.walls_advice)
                    .positiveText(R.string.close)
                    .neutralText(R.string.dontshow)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mPrefs.setWallsDialogDismissed(false); //TODO what's the point of this if it already needs to be false just to get here
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            mPrefs.setWallsDialogDismissed(true);
                        }
                    })
                    .show();
        }
    }

    private void showApplyWallpaperDialog(final Context context, final WallpaperItem item) {

        WallpaperDialog.show(getActivity(), item.getWallURL());
//        new MaterialDialog.Builder(context)
//                .title(R.string.apply)
//                .content(R.string.confirm_apply)
//                .positiveText(R.string.apply)
//                .negativeText(android.R.string.cancel)
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick (@NonNull MaterialDialog materialDialog, @NonNull final DialogAction dialogAction) {
//                        if (dialogApply != null) {
//                            dialogApply.dismiss();
//                        }
//
//                        final ApplyWallpaper[] applyTask = new ApplyWallpaper[1];
//
//                        final boolean[] enteredApplyTask = {false};
//
//                        dialogApply = new MaterialDialog.Builder(context)
//                                .content(R.string.downloading_wallpaper)
//                                .progress(true, 0)
//                                .cancelable(false)
//                                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                                    @Override
//                                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                        if (applyTask[0] != null) {
//                                            applyTask[0].cancel(true);
//                                        }
//                                        dialogApply.dismiss();
//                                    }
//                                })
//                                .show();
//
//                        Glide.with(context)
//                                .load(item.getWallURL())
//                                .asBitmap()
//                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                                .into(new SimpleTarget<Bitmap>() {
//                                    @Override
//                                    public void onResourceReady (final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                        if (resource != null && dialogApply.isShowing()) {
//                                            enteredApplyTask[0] = true;
//
//                                            if (dialogApply != null) {
//                                                dialogApply.dismiss();
//                                            }
//                                            dialogApply = new MaterialDialog.Builder(context)
//                                                    .content(R.string.setting_wall_title)
//                                                    .progress(true, 0)
//                                                    .cancelable(false)
//                                                    .show();
//
//                                            applyTask[0] = new ApplyWallpaper(context, dialogApply, resource, false, layout, null);
//                                            applyTask[0].execute();
//                                        }
//                                    }
//                                });
//
//                        Timer timer = new Timer();
//                        timer.schedule(new TimerTask() {
//                            @Override
//                            public void run () {
//                                runOnUIThread(context, new Runnable() {
//                                    @Override
//                                    public void run () {
//                                        if (!enteredApplyTask[0]) {
//                                            String newContent = context.getString(R.string.downloading_wallpaper)
//                                                    + "\n"
//                                                    + context.getString(R.string.download_takes_longer);
//                                            dialogApply.setContent(newContent);
//                                            dialogApply.setActionButton(DialogAction.POSITIVE, android.R.string.cancel);
//                                        }
//                                    }
//                                });
//                            }
//                        }, 15000);
//                    }
//                })
//                .show();
    }

}