package jahirfiquitiva.apps.iconshowcase.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.apps.iconshowcase.tasks.ZipFilesToRequest;
import jahirfiquitiva.apps.iconshowcase.utilities.ApplicationBase;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;
import jahirfiquitiva.apps.iconshowcase.views.GridSpacingItemDecoration;

public class RequestsFragment extends Fragment {

    public static RecyclerFastScroller fastScroller;
    public static ProgressBar progressBar;
    public static RecyclerView mRecyclerView;
    public static FloatingActionButton fab;

    static int columnsNumber, gridSpacing;
    static boolean withBorders;
    public static ViewGroup layout;

    private Preferences mPrefs;

    private static Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        gridSpacing = getResources().getDimensionPixelSize(R.dimen.lists_padding);
        columnsNumber = getResources().getInteger(R.integer.requests_grid_width);
        withBorders = true;

        layout = (ViewGroup) inflater.inflate(R.layout.icon_request_section, container, false);

        context = getActivity();

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
        } catch (InflateException e) {
            e.printStackTrace();
        }

        if (ShowcaseActivity.toolbar != null) {
            if (ShowcaseActivity.toolbar.getTitle() != null &&
                    !ShowcaseActivity.toolbar.getTitle().equals(
                            Util.getStringFromResources(getActivity(), R.string.section_five))) {
                ShowcaseActivity.toolbar.setTitle(R.string.section_five);
            }
        }

        mPrefs = new Preferences(getActivity());

        showRequestsAdviceDialog(getActivity());

        fab = (FloatingActionButton) layout.findViewById(R.id.requests_btn);
        progressBar = (ProgressBar) layout.findViewById(R.id.requestProgress);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.appsToRequestList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnsNumber));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(columnsNumber, gridSpacing, withBorders));
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        hideStuff();

        fastScroller.setHideDelay(1000);
        fab.setColorNormal(getResources().getColor(R.color.accent));
        fab.setColorPressed(getResources().getColor(R.color.accent));
        fab.setColorRipple(getResources().getColor(R.color.semitransparent_white));
        fab.hide(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    Assent.requestPermissions(new AssentCallback() {
                        @Override
                        public void onPermissionResult(PermissionResultSet result) {
                            if (result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                                        .content(R.string.building_request_dialog)
                                        .progress(true, 0)
                                        .cancelable(false)
                                        .show();

                                new ZipFilesToRequest(getActivity(), dialog,
                                        ((RequestsAdapter) mRecyclerView.getAdapter()).appsList).execute();
                            }
                        }
                    }, 69, Assent.WRITE_EXTERNAL_STORAGE);
                } else {
                    final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                            .content(R.string.building_request_dialog)
                            .progress(true, 0)
                            .cancelable(false)
                            .show();

                    new ZipFilesToRequest(getActivity(), dialog,
                            ((RequestsAdapter) mRecyclerView.getAdapter()).appsList).execute();
                }
            }
        });

        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupContent();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

    }

    public static void setupContent() {
        if (layout != null) {
            if (ApplicationBase.allAppsToRequest != null && ApplicationBase.allAppsToRequest.size() > 0) {
                RequestsAdapter requestsAdapter = new RequestsAdapter(context, ApplicationBase.allAppsToRequest);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(requestsAdapter);
                requestsAdapter.startIconFetching(mRecyclerView);
                fab.attachToRecyclerView(mRecyclerView);
                showStuff();
            }
        }
    }

    private static void showStuff() {
        fab.show(true);
        if (progressBar.getVisibility() != View.GONE) {
            progressBar.setVisibility(View.GONE);
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        fastScroller.setVisibility(View.VISIBLE);
        fastScroller.setRecyclerView(mRecyclerView);
    }

    private void hideStuff() {
        fab.hide(true);
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestsAdapter adapter = ((RequestsAdapter) mRecyclerView.getAdapter());
        if (adapter != null) {
            adapter.stopAppIconFetching();
        }
    }

    private void showRequestsAdviceDialog(Context dialogContext) {
        if (!mPrefs.getRequestsDialogDismissed()) {
            new MaterialDialog.Builder(dialogContext)
                    .title(R.string.advice)
                    .content(R.string.request_advice)
                    .positiveText(R.string.close)
                    .neutralText(R.string.dontshow)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            mPrefs.setRequestsDialogDismissed(false);
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            mPrefs.setRequestsDialogDismissed(true);
                        }
                    })
                    .show();
        }
    }

}