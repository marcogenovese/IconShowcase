package jahirfiquitiva.apps.iconshowcase.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.apps.iconshowcase.models.RequestItem;
import jahirfiquitiva.apps.iconshowcase.tasks.ZipFilesToRequest;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.views.GridSpacingItemDecoration;

public class RequestsFragment extends Fragment {

    // List & Adapter

    public static RequestsAdapter mAdapter;

    public static ProgressBar progressBar;
    public static RecyclerView mRecyclerView;
    public static FloatingActionButton fab;
    public static RecyclerFastScroller fastScroller;

    static int columnsNumber, gridSpacing;
    static boolean withBorders;
    public static ViewGroup layout;

    private Preferences mPrefs;

    private static Activity context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ActionBar toolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (toolbar != null)
            toolbar.setTitle(R.string.section_five);

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

        }

        mPrefs = new Preferences(getActivity());

        TextView advice = (TextView) layout.findViewById(R.id.requestAdvice);
        advice.setVisibility(mPrefs.getHiddenAdvices() ? View.GONE : View.VISIBLE);

        gridSpacing = getResources().getDimensionPixelSize(R.dimen.lists_padding);
        columnsNumber = getResources().getInteger(R.integer.requests_grid_width);
        withBorders = true;

        fab = (FloatingActionButton) layout.findViewById(R.id.requests_btn);
        fab.setColorNormal(getResources().getColor(R.color.accent));
        fab.setColorPressed(getResources().getColor(R.color.accent));
        fab.setColorRipple(getResources().getColor(R.color.semitransparent_white));
        fab.hide(true);

        progressBar = (ProgressBar) layout.findViewById(R.id.requestProgress);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.appsToRequestList);
        mRecyclerView.setVisibility(View.GONE);
        fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        fastScroller.setHideDelay(1000);
        fastScroller.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .content(R.string.building_request_dialog)
                        .progress(true, 0)
                        .cancelable(false)
                        .show();
                new ZipFilesToRequest(getActivity(), dialog).execute();

            }
        });

        hideStuff();
        setupLayout();

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
    }

    public static void setupRequestAdapter(ArrayList<RequestItem> list) {
        mAdapter = new RequestsAdapter(context, list, new RequestsAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                RequestItem requestsItem = RequestsAdapter.appsList.get(position);
                requestsItem.setSelected(!requestsItem.isSelected());
                RequestsAdapter.appsList.set(position, requestsItem);
                mAdapter.notifyItemChanged(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        setupLayout();
    }

    public static void setupLayout() {
        if (layout != null) {
            if (mAdapter != null) {
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, columnsNumber));
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(columnsNumber, gridSpacing, withBorders));
                mRecyclerView.setHasFixedSize(true);
                mAdapter.startIconFetching(mRecyclerView);
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

    private static void hideStuff() {
        fab.hide(true);
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setVisibility(View.GONE);
        fastScroller.setVisibility(View.GONE);
    }

}