package jahirfiquitiva.iconshowcase.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.tasks.ZipFilesToRequest;
import jahirfiquitiva.iconshowcase.utilities.ApplicationBase;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.views.GridSpacingItemDecoration;

public class RequestsFragment extends Fragment implements PermissionUtils.OnPermissionResultListener {

    public static ProgressBar progressBar;
    public static RecyclerView mRecyclerView;
    public static RecyclerFastScroller fastScroller;
    private static FloatingActionButton fab;

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

        context = getActivity();

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }

        try {
            layout = (ViewGroup) inflater.inflate(R.layout.icon_request_section, container, false);
        } catch (InflateException e) {
            // Do nothing
        }

        mPrefs = new Preferences(getActivity());

        showRequestsAdviceDialog(getActivity());

        fab = (FloatingActionButton) layout.findViewById(R.id.requests_fab);

        if (ApplicationBase.allAppsToRequest == null || ApplicationBase.allAppsToRequest.size() <= 0) {
            fab.hide();
        } else {
            fab.show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.canAccessStorage(getContext())) {
                    PermissionUtils.requestStoragePermission(getActivity(), RequestsFragment.this);
                } else {
                    showRequestsFilesCreationDialog(context);
                }
            }
        });

        progressBar = (ProgressBar) layout.findViewById(R.id.requestProgress);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.appsToRequestList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnsNumber));
        mRecyclerView.addItemDecoration(
                new GridSpacingItemDecoration(columnsNumber,
                        gridSpacing,
                        true));
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        fastScroller.attachRecyclerView(mRecyclerView);
        hideStuff();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
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
                showStuff();
            }
        }
    }

    private static void showStuff() {
        if (progressBar.getVisibility() != View.GONE) {
            progressBar.setVisibility(View.GONE);
        }
        mRecyclerView.setVisibility(View.VISIBLE);
        fastScroller.setVisibility(View.VISIBLE);
        fab.show();
    }

    private void hideStuff() {
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
            MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    if (which.equals(DialogAction.POSITIVE))
                        mPrefs.setRequestsDialogDismissed(false);
                    else if (which.equals(DialogAction.NEUTRAL))
                        mPrefs.setRequestsDialogDismissed(true);
                }
            };
            ISDialogs.showRequestAdviceDialog(dialogContext, singleButtonCallback);
        }
    }

    public static void showRequestsFilesCreationDialog(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {

            ISDialogs.showPermissionNotGrantedDialog(context);

        } else {
            final MaterialDialog dialog = ISDialogs.showBuildingRequestDialog(context);
            dialog.show();

            new ZipFilesToRequest((Activity) context, dialog,
                    ((RequestsAdapter) mRecyclerView.getAdapter()).appsList).execute();
        }
    }

    @Override
    public void onStoragePermissionGranted() {
        showRequestsFilesCreationDialog(context);
    }

}