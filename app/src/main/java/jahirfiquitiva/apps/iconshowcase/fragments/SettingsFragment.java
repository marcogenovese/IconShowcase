/*
 * Copyright (c) 2015. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.apps.iconshowcase.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.apps.iconshowcase.basefragments.PreferenceFragment;
import jahirfiquitiva.apps.iconshowcase.dialogs.FolderChooserDialog;
import jahirfiquitiva.apps.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;

public class SettingsFragment extends PreferenceFragment implements PermissionUtils.OnPermissionResultListener {

    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    private static Preferences mPrefs;
    private static PackageManager p;
    private static ComponentName componentName;
    private static Preference WSL, data;
    private static String location, cacheSize;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity();

        String settingsTitle = getResources().getString(R.string.title_settings);

        mPrefs = new Preferences(getActivity());

        mPrefs.setSettingsModified(false);

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sp.edit();

        if (mPrefs.getDownloadsFolder() != null) {
            location = mPrefs.getDownloadsFolder();
        } else {
            location = getString(R.string.walls_save_location,
                    Environment.getExternalStorageDirectory().getAbsolutePath());
        }

        cacheSize = fullCacheDataSize(getActivity().getApplicationContext());

        p = getActivity().getPackageManager();
        componentName = new ComponentName(getActivity(), ShowcaseActivity.class);

        addPreferencesFromResource(R.xml.preferences);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        if (fab != null) {
            fab.setVisibility(View.GONE);
        }

        WSL = findPreference("wallsSaveLocation");
        WSL.setSummary(getResources().getString(R.string.pref_summary_wsl, location));

        // Set the preference for current selected theme
        findPreference("themes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final int[] selectedTheme = {sp.getInt("theme", 0)};
                final int[] newSelectedTheme = new int[1];
                ShowcaseActivity.settingsDialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.pref_title_themes)
                        .items(R.array.themes)
                        .itemsCallbackSingleChoice(selectedTheme[0], new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int position, CharSequence text) {
                                mPrefs.setSettingsModified(true);
                                newSelectedTheme[0] = position;
                                switch (position) {
                                    case 0:
                                        ThemeUtils.changeToTheme(getActivity(), ThemeUtils.LIGHT);
                                        break;
                                    case 1:
                                        ThemeUtils.changeToTheme(getActivity(), ThemeUtils.DARK);
                                        break;
                                    case 2:
                                        ThemeUtils.changeToTheme(getActivity(), ThemeUtils.AUTO);
                                        break;
                                }
                                editor.putInt("theme", position).apply();
                                return true;
                            }
                        })
                        .positiveText(android.R.string.ok)
                        .positiveColorRes(R.color.accent)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                if (newSelectedTheme != selectedTheme) {
                                    ThemeUtils.restartActivity(getActivity());
                                }
                            }
                        })
                        .show();
                return true;
            }
        });

        // Set the preference for colored nav bar on Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final CheckBoxPreference coloredNavBar = (CheckBoxPreference) getPreferenceManager().findPreference("coloredNavBar");
            coloredNavBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    mPrefs.setSettingsModified(true);
                    if (newValue.toString().equals("true")) {
                        ThemeUtils.changeNavBar(getActivity(), ThemeUtils.NAVBAR_DEFAULT);
                    } else {
                        ThemeUtils.changeNavBar(getActivity(), ThemeUtils.NAVBAR_BLACK);
                    }
                    return true;
                }
            });
        }

        CheckBoxPreference animations = (CheckBoxPreference) getPreferenceManager().findPreference("animations");
        animations.setChecked(mPrefs.getAnimationsEnabled());
        animations.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                mPrefs.setAnimationsEnabled(newValue.toString().equals("true"));
                return true;
            }
        });

        data = findPreference("clearData");
        data.setSummary(getResources().getString(R.string.pref_summary_cache, cacheSize));
        data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.clearcache_dialog_title)
                        .content(R.string.clearcache_dialog_content)
                        .positiveText(android.R.string.yes)
                        .negativeText(android.R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                clearApplicationDataAndCache(getActivity());
                                changeValues(getActivity());
                            }
                        })
                        .show();

                return true;
            }
        });

        findPreference("wallsSaveLocation").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(!PermissionUtils.canAccessStorage(getContext())) {
                    PermissionUtils.requestStoragePermission(getActivity(), SettingsFragment.this);
                } else {
                    showFolderChooserDialog();
                }
                return true;
            }
        });

        final CheckBoxPreference hideIcon = (CheckBoxPreference) getPreferenceManager().findPreference("launcherIcon");
        if (mPrefs.getLauncherIconShown()) {
            hideIcon.setChecked(false);
        }
        hideIcon.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue.toString().equals("true")) {
                    ShowcaseActivity.settingsDialog = new MaterialDialog.Builder(getActivity())
                            .title(R.string.hideicon_dialog_title)
                            .content(R.string.hideicon_dialog_content)
                            .positiveText(android.R.string.yes)
                            .negativeText(android.R.string.no)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                    if (mPrefs.getLauncherIconShown()) {
                                        mPrefs.setIconShown(false);
                                        p.setComponentEnabledSetting(componentName,
                                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                PackageManager.DONT_KILL_APP);
                                    }

                                    hideIcon.setChecked(true);
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                    hideIcon.setChecked(false);
                                }
                            })
                            .show();

                    ShowcaseActivity.settingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (mPrefs.getLauncherIconShown()) {
                                hideIcon.setChecked(false);
                            }
                        }
                    });

                } else {
                    if (!mPrefs.getLauncherIconShown()) {

                        mPrefs.setIconShown(true);
                        p.setComponentEnabledSetting(componentName,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);

                    }
                }
                return true;
            }
        });

    }

    public static void changeValues(Context context) {
        if (mPrefs.getDownloadsFolder() != null) {
            location = mPrefs.getDownloadsFolder();
        } else {
            location = context.getString(R.string.walls_save_location,
                    Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        WSL.setSummary(context.getResources().getString(R.string.pref_summary_wsl, location));
        cacheSize = fullCacheDataSize(context);
        data.setSummary(context.getResources().getString(R.string.pref_summary_cache, cacheSize));

    }

    private static String fullCacheDataSize(Context context) {
        String finalSize;

        long cache = 0;
        long extCache = 0;
        double finalResult, mbFinalResult;

        File[] fileList = context.getCacheDir().listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                cache += dirSize(fileList[i]);
            } else {
                cache += fileList[i].length();
            }
        }
        try {
            File[] fileExtList = context.getExternalCacheDir().listFiles();
            for (int j = 0; j < fileExtList.length; j++) {
                if (fileExtList[j].isDirectory()) {
                    extCache += dirSize(fileExtList[j]);
                } else {
                    extCache += fileExtList[j].length();
                }
            }
        } catch (NullPointerException npe) {
            Log.d("CACHE", Log.getStackTraceString(npe));
        }

        finalResult = (cache + extCache) / 1000;

        if (finalResult > 1001) {
            mbFinalResult = finalResult / 1000;
            finalSize = String.format("%.2f", mbFinalResult) + " MB";
        } else {
            finalSize = String.format("%.2f", finalResult) + " KB";
        }

        return finalSize;
    }

    private static long dirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    result += fileList[i].length();
                }
            }
            return result;
        }
        return 0;
    }

    public static void clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    public static void clearApplicationDataAndCache(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
        clearCache(context);
        editor.clear().commit();
        mPrefs.setEasterEggEnabled(false);
        mPrefs.setIconShown(true);
        mPrefs.setDownloadsFolder(null);
        mPrefs.setRequestsDialogDismissed(false);
        mPrefs.setApplyDialogDismissed(false);
        mPrefs.setWallsDialogDismissed(false);
        ThemeUtils.restartActivity((Activity) context);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    private void showFolderChooserDialog() {
        new FolderChooserDialog().show((AppCompatActivity) getActivity());
    }

    @Override
    public void onStoragePermissionGranted() {
        ((ShowcaseActivity) getActivity()).openFileChooser();
    }
}