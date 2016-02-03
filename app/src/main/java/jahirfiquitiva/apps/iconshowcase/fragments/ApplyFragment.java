package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import jahirfiquitiva.apps.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.apps.iconshowcase.sort.InstalledLauncherComparator;
import jahirfiquitiva.apps.iconshowcase.utilities.LauncherIntents;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.Utils;
import jahirfiquitiva.apps.iconshowcase.views.GridSpacingItemDecoration;

public class ApplyFragment extends Fragment {

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String intentString;
    private final List<Launcher> launchers = new ArrayList<>();

    private RelativeLayout applyLayout;
    private RecyclerView recyclerView;

    private Preferences mPrefs;

    private ViewGroup layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

        applyLayout = (RelativeLayout) layout.findViewById(R.id.applyLayout);
        recyclerView = (RecyclerView) layout.findViewById(R.id.launchersList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                getResources().getInteger(R.integer.launchers_grid_width)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new GridSpacingItemDecoration(getResources().getInteger(R.integer.launchers_grid_width),
                        getResources().getDimensionPixelSize(R.dimen.lists_padding),
                        true));

        RecyclerFastScroller fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);
        fastScroller.attachRecyclerView(recyclerView);

        // Splits all launcher  arrays by the | delimiter {name}|{package}
        final String[] launcherArray = getResources().getStringArray(R.array.launchers);
        for (String launcher : launcherArray)
            launchers.add(new Launcher(launcher.split("\\|")));
        Collections.sort(launchers, new InstalledLauncherComparator(getActivity()));

        LaunchersAdapter adapter = new LaunchersAdapter(getActivity(), launchers,
                new LaunchersAdapter.ClickListener() {
                    @Override
                    public void onClick(int position) {
                        if (launchers.get(position).name.equals("Google Now")) {
                            gnlDialog();
                        } else if (launchers.get(position).name.equals("CM Theme Engine")) {
                            if (Utils.isAppInstalled(getActivity(), "com.cyngn.theme.chooser")) {
                                openLauncher("CM Theme Engine");
                            } else if (Utils.isAppInstalled(getActivity(), launchers.get(position).packageName)) {
                                openLauncher(launchers.get(position).name);
                            }
                        } else if (Utils.isAppInstalled(getActivity(), launchers.get(position).packageName)) {
                            openLauncher(launchers.get(position).name);
                        } else {
                            openInPlayStore(launchers.get(position));
                        }
                    }
                });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return layout;
    }

    private void openLauncher(String name) {
        final String launcherName = Character.toUpperCase(name.charAt(0))
                + name.substring(1).toLowerCase().replace(" ", "").replace("launcher", "");
        new LauncherIntents(getActivity(), launcherName, applyLayout);
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
        ISDialogs.showOpenInPlayStoreDialog(getContext(), launcher.name, dialogContent, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(intentString));
                startActivity(intent);
            }
        });
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
                    if (Utils.isAppInstalled(context, "org.cyanogenmod.theme.chooser")
                            || Utils.isAppInstalled(context, "com.cyngn.theme.chooser")) {
                        isInstalled = 1;
                    }
                } else {
                    isInstalled = Utils.isAppInstalled(context, packageName) ? 1 : 0;
                }
            }

            // Caches this value, checking if a launcher is installed is intensive on processing
            return isInstalled == 1;
        }

    }

    private void gnlDialog() {
        final String appLink = MARKET_URL + getResources().getString(R.string.extraapp);
        ISDialogs.showGoogleNowLauncherDialog(getContext(), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(appLink));
                startActivity(intent);
            }
        });
    }

    private void showApplyAdviceDialog(Context dialogContext) {
        if (!mPrefs.getApplyDialogDismissed()) {
            MaterialDialog.SingleButtonCallback singleButtonCallback = new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog dialog, DialogAction which) {
                    if (which.equals(DialogAction.POSITIVE)) {
                        mPrefs.setApplyDialogDismissed(false);
                    } else if (which.equals(DialogAction.NEUTRAL)) {
                        mPrefs.setApplyDialogDismissed(true);
                    }
                }
            };
            ISDialogs.showApplyAdviceDialog(dialogContext, singleButtonCallback);
        }
    }

}