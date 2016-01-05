package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.LaunchersAdapter;
import jahirfiquitiva.apps.iconshowcase.sort.InstalledLauncherComparator;
import jahirfiquitiva.apps.iconshowcase.utilities.LauncherIntents;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;
import jahirfiquitiva.apps.iconshowcase.views.GridSpacingItemDecoration;

public class ApplyFragment extends Fragment {

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String intentString;
    private final List<Launcher> launchers = new ArrayList<>();

    private RelativeLayout applyLayout;
    private RecyclerView recyclerView;
    private RecyclerFastScroller recyclerFastScroller;

    private Preferences mPrefs;

    private ViewGroup layout;

    int columnsNumber, gridSpacing;
    boolean withBorders;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.apply_section, container, false);
        } catch (InflateException e) {

        }

        mPrefs = new Preferences(getActivity());

        showApplyAdviceDialog(getActivity());

        gridSpacing = getResources().getDimensionPixelSize(R.dimen.launchers_grid_padding);
        columnsNumber = getResources().getInteger(R.integer.launchers_grid_width);
        withBorders = true;

        applyLayout = (RelativeLayout) layout.findViewById(R.id.applyLayout);
        recyclerView = (RecyclerView) layout.findViewById(R.id.launchersList);
        recyclerFastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columnsNumber));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(columnsNumber, gridSpacing, withBorders));

        // Splits all launcher  arrays by the | delimiter {name}|{package}
        final String[] launcherArray = getResources().getStringArray(R.array.launchers);
        for (String launcher : launcherArray)
            launchers.add(new Launcher(launcher.split("\\|")));
        Collections.sort(launchers, new InstalledLauncherComparator(getActivity()));

        LaunchersAdapter adapter = new LaunchersAdapter(getActivity(), launchers,
                new LaunchersAdapter.ClickListener() {
                    @Override
                    public void onClick(int position) {
                        if (launchers.get(position).name.equals("Google Now Launcher")) {
                            gnlDialog();
                        } else if (launchers.get(position).name.equals("CM Theme Engine")) {
                            if (Util.isAppInstalled(getActivity(), "com.cyngn.theme.chooser")) {
                                openLauncher("CM Theme Engine");
                            } else if (Util.isAppInstalled(getActivity(), launchers.get(position).packageName)) {
                                openLauncher(launchers.get(position).name);
                            }
                        } else if (Util.isAppInstalled(getActivity(), launchers.get(position).packageName)) {
                            openLauncher(launchers.get(position).name);
                        } else {
                            openInPlayStore(launchers.get(position));
                        }
                    }
                });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerFastScroller.setHideDelay(1000);
        recyclerFastScroller.attachRecyclerView(recyclerView);

        return layout;
    }

    private void openLauncher(String name) {
        final String launcherName = Character.toUpperCase(name.charAt(0))
                + name.substring(1).toLowerCase().replace(" ", "").replace("launcher", "")
                + "Launcher";
        LauncherIntents launcherIntent = new LauncherIntents(getActivity(), launcherName, applyLayout);

    }

    private void openInPlayStore(final Launcher launcher) {
        intentString = MARKET_URL + launcher.packageName;
        final String LauncherName = launcher.name;
        final String cmName = "CM Theme Engine";
        String dialogContent;
        if (LauncherName.equals(cmName)) {
            dialogContent = getResources().getString(R.string.cm_dialog_content, launcher.name);
            intentString = "http://download.cyanogenmod.org/";
        } else {
            dialogContent = getResources().getString(R.string.lni_content, launcher.name);
            intentString = MARKET_URL + launcher.packageName;
        }
        new MaterialDialog.Builder(getActivity())
                .title(launcher.name)
                .content(dialogContent)
                .positiveText(R.string.yes)
                .negativeText(R.string.lni_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(intentString));
                        startActivity(intent);
                    }
                })
                .show();
    }

    public class Launcher {

        public final String name;
        public final String packageName;
        public final int launcherColor;
        private int isInstalled = -1;

        public Launcher(String[] values) {
            name = values[0];
            packageName = values[1];
            launcherColor = Color.parseColor(values[2]);
        }

        public boolean isInstalled(Context context) {
            if (isInstalled == -1) {
                if (packageName.equals("org.cyanogenmod.theme.chooser")) {
                    if (Util.isAppInstalled(context, "org.cyanogenmod.theme.chooser")
                            || Util.isAppInstalled(context, "com.cyngn.theme.chooser")) {
                        isInstalled = 1;
                    }
                } else {
                    isInstalled = Util.isAppInstalled(context, packageName) ? 1 : 0;
                }
            }

            // Caches this value, checking if a launcher is installed is intensive on processing
            return isInstalled == 1;
        }

    }

    private void gnlDialog() {
        final String appLink = MARKET_URL + getResources().getString(R.string.extraapp);
        new MaterialDialog.Builder(getActivity())
                .title(R.string.gnl_title)
                .content(R.string.gnl_content)
                .positiveText(R.string.yes)
                .negativeText(R.string.lni_no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(appLink));
                        startActivity(intent);
                    }
                })
                .show();
    }

    private void showApplyAdviceDialog(Context dialogContext) {
        if (!mPrefs.getApplyDialogDismissed()) {
            new MaterialDialog.Builder(dialogContext)
                    .title(R.string.advice)
                    .content(R.string.apply_advice)
                    .positiveText(R.string.close)
                    .neutralText(R.string.dontshow)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            mPrefs.setApplyDialogDismissed(false);
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            mPrefs.setApplyDialogDismissed(true);
                        }
                    })
                    .show();
        }
    }

}
