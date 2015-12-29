package jahirfiquitiva.apps.iconshowcase.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.adapters.ChangelogAdapter;
import jahirfiquitiva.apps.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.apps.iconshowcase.dialogs.FolderSelectorDialog;
import jahirfiquitiva.apps.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.apps.iconshowcase.fragments.SettingsFragment;
import jahirfiquitiva.apps.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.apps.iconshowcase.models.WallpapersList;
import jahirfiquitiva.apps.iconshowcase.tasks.LoadAppsToRequest;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class ShowcaseActivity extends AppCompatActivity
        implements FolderSelectorDialog.FolderSelectCallback {

    private static final boolean WITH_LICENSE_CHECKER = false;
    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String thaApp, thaHome, thaPreviews, thaApply, thaWalls, thaRequest, thaFAQs, thaCredits, thaSettings;

    private static AppCompatActivity context;
    private static Context actContext;

    public String version;

    public static int currentItem = -1;

    private boolean firstRun, enable_features, permissionGranted, mLastTheme, mLastNavBar;
    private static Preferences mPrefs;

    public static MaterialDialog settingsDialog;

    public static Drawer drawer;
    public AccountHeader drawerHeader;

    private WallpapersFragment.DownloadJSON downloadJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
            /*
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            */
        }

        super.onCreate(savedInstanceState);
        mPrefs = new Preferences(ShowcaseActivity.this);

        context = this;
        actContext = this;

        setContentView(R.layout.showcase_activity);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Assent.setActivity(this, this);
        permissionGranted = Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE);

        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            Assent.requestPermissions(new AssentCallback() {
                @Override
                public void onPermissionResult(PermissionResultSet result) {
                    permissionGranted = result.isGranted(Assent.WRITE_EXTERNAL_STORAGE);
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
        }

        thaApp = getResources().getString(R.string.app_name);
        thaHome = getResources().getString(R.string.section_one);
        thaPreviews = getResources().getString(R.string.section_two);
        thaApply = getResources().getString(R.string.section_three);
        thaWalls = getResources().getString(R.string.section_four);
        thaRequest = getResources().getString(R.string.section_five);
        thaCredits = getResources().getString(R.string.section_six);
        thaSettings = getResources().getString(R.string.title_settings);
        thaFAQs = getResources().getString(R.string.faqs_section);

        enable_features = mPrefs.isFeaturesEnabled();
        firstRun = mPrefs.isFirstRun();

        setupDrawer(false, toolbar, savedInstanceState);

        runLicenseChecker();

        if (savedInstanceState == null) {
            if (mPrefs.getSettingsModified()) {
                switchFragment(8, thaSettings, "Settings", context);
            } else {
                currentItem = -1;
                drawer.setSelection(1);
            }
        }

    }

    public static void switchFragment(int itemId, String title, String fragment, AppCompatActivity context) {
        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;
        if (context.getSupportActionBar() != null)
            context.getSupportActionBar().setTitle(title);

        if (mPrefs.getAnimationsEnabled()) {
            context.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.main, Fragment.instantiate(context,
                            "jahirfiquitiva.apps.iconshowcase.fragments." + fragment + "Fragment"))
                    .commit();
        } else {
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, Fragment.instantiate(context,
                            "jahirfiquitiva.apps.iconshowcase.fragments." + fragment + "Fragment"))
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLastTheme = ThemeUtils.darkTheme;
        mLastNavBar = ThemeUtils.coloredNavBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLastTheme != ThemeUtils.darkTheme
                || mLastNavBar != ThemeUtils.coloredNavBar) {
            drawer.resetDrawerContent();
            this.recreate();
            /*
            this.startActivity(new Intent(this, this.getClass()));
            this.finish();
            this.overridePendingTransition(0, 0);
            */
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null)
            outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (drawer != null && currentItem != 1) {
            drawer.setSelection(1);
        } else if (drawer != null) {
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestsAdapter adapter = ((RequestsAdapter) RequestsFragment.mRecyclerView.getAdapter());
        if (adapter != null) {
            adapter.stopAppIconFetching();
        }
        if (settingsDialog != null) {
            settingsDialog.dismiss();
            settingsDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody =
                        getResources().getString(R.string.share_one) +
                                getResources().getString(R.string.iconpack_designer) +
                                getResources().getString(R.string.share_two) +
                                MARKET_URL + getPackageName();
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, (getResources().getString(R.string.share_title))));
                break;

            case R.id.sendemail:
                StringBuilder emailBuilder = new StringBuilder();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + getResources().getString(R.string.email_id)));
                intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.email_subject));
                emailBuilder.append("\n \n \nOS Version: ").append(System.getProperty("os.version")).append("(").append(Build.VERSION.INCREMENTAL).append(")");
                emailBuilder.append("\nOS API Level: ").append(Build.VERSION.SDK_INT);
                emailBuilder.append("\nDevice: ").append(Build.DEVICE);
                emailBuilder.append("\nManufacturer: ").append(Build.MANUFACTURER);
                emailBuilder.append("\nModel (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")");
                PackageInfo appInfo = null;
                try {
                    appInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                assert appInfo != null;
                emailBuilder.append("\nApp Version Name: ").append(appInfo.versionName);
                emailBuilder.append("\nApp Version Code: ").append(appInfo.versionCode);
                intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString());
                startActivity(Intent.createChooser(intent, (getResources().getString(R.string.send_title))));
                break;

            case R.id.changelog:
                showChangelog();
                break;

            case R.id.refresh:
                WallpapersFragment.refreshWalls(context);
                loadWallsList();
                break;

        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Assent.setActivity(this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Assent.setActivity(this, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    private void addItemsToDrawer() {
        IDrawerItem walls = new PrimaryDrawerItem().withName(thaWalls).withIcon(GoogleMaterial.Icon.gmd_landscape).withIdentifier(3);
        IDrawerItem request = new PrimaryDrawerItem().withName(thaRequest).withIcon(GoogleMaterial.Icon.gmd_comment_list).withIdentifier(4);
        if (enable_features) {
            drawer.addItemAtPosition(walls, 3);
            drawer.addItemAtPosition(request, 4);
        }
    }

    private void runLicenseChecker() {
        if (firstRun) {
            mPrefs.setSettingsModified(false);
            if (WITH_LICENSE_CHECKER) {
                checkLicense();
            } else {
                mPrefs.setFeaturesEnabled(true);
                addItemsToDrawer();
                showChangelogDialog();
            }
        } else {
            if (WITH_LICENSE_CHECKER) {
                if (!enable_features) {
                    showNotLicensedDialog();
                } else {
                    addItemsToDrawer();
                    showChangelogDialog();
                }
            } else {
                addItemsToDrawer();
                showChangelogDialog();
            }
        }
    }

    private void showChangelog() {
        new MaterialDialog.Builder(this)
                .title(R.string.changelog_dialog_title)
                .adapter(new ChangelogAdapter(this, R.array.fullchangelog), null)
                .positiveText(R.string.great)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mPrefs.setNotFirstRun();
                    }
                })
                .show();
    }

    private void showChangelogDialog() {
        String launchinfo = getSharedPreferences("PrefsFile", MODE_PRIVATE).getString("version", "0");
        if (launchinfo != null && !launchinfo.equals(Util.getAppVersion(this))) {
            showChangelog();
        }
        storeSharedPrefs();
    }

    @SuppressLint("CommitPrefEdits")
    private void storeSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("PrefsFile", MODE_PRIVATE);
        sharedPreferences.edit().putString("version", Util.getAppVersion(this)).commit();
    }

    private void checkLicense() {
        String installer = getPackageManager().getInstallerPackageName(getPackageName());
        try {
            if (installer.equals("com.google.android.feedback") ||
                    installer.equals("com.android.vending")) {
                new MaterialDialog.Builder(this)
                        .title(R.string.license_success_title)
                        .content(R.string.license_success)
                        .positiveText(R.string.close)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                                enable_features = true;
                                mPrefs.setFeaturesEnabled(true);
                                addItemsToDrawer();
                                showChangelogDialog();
                            }
                        })
                        .show();
            } else {
                showNotLicensedDialog();
            }
        } catch (Exception e) {
            showNotLicensedDialog();
        }
    }

    private void showNotLicensedDialog() {
        enable_features = false;
        mPrefs.setFeaturesEnabled(false);
        new MaterialDialog.Builder(this)
                .title(R.string.license_failed_title)
                .content(R.string.license_failed)
                .positiveText(R.string.download)
                .negativeText(R.string.exit)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + getPackageName()));
                        startActivity(browserIntent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        finish();
                    }
                })
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                }).show();
    }

    public void loadWallsList() {
        if (mPrefs.getWallsListLoaded()) {
            WallpapersList.clearList();
            mPrefs.setWallsListLoaded(!mPrefs.getWallsListLoaded());
        }
        downloadJSON = new WallpapersFragment.DownloadJSON(new WallsListInterface() {
            @Override
            public void checkWallsListCreation(boolean result) {
                mPrefs.setWallsListLoaded(result);
                if (WallpapersFragment.mSwipeRefreshLayout != null) {
                    WallpapersFragment.mSwipeRefreshLayout.setEnabled(false);
                    WallpapersFragment.mSwipeRefreshLayout.setRefreshing(false);
                }
                if (WallpapersFragment.mAdapter != null) {
                    WallpapersFragment.mAdapter.notifyDataSetChanged();
                }
            }
        }, this);
        downloadJSON.execute();
    }

    public interface WallsListInterface {
        void checkWallsListCreation(boolean result);
    }

    public void onFolderSelection(File folder) {
        mPrefs.setDownloadsFolder(folder.getAbsolutePath());
        SettingsFragment.changeValues(getApplicationContext());
    }

    public void setupDrawer(boolean withHeader, Toolbar toolbar, Bundle savedInstanceState) {
        if (withHeader) {
            drawerHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header)
                    .withSelectionFirstLine(getResources().getString(R.string.app_long_name))
                    .withSelectionSecondLine("v " + Util.getAppVersion(this))
                    .withProfileImagesClickable(false)
                    .withResetDrawerOnProfileListClick(false)
                    .withSelectionListEnabled(false)
                    .withSelectionListEnabledForSingleProfile(false)
                    .withSavedInstance(savedInstanceState)
                    .build();

            drawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withAccountHeader(drawerHeader)
                    .withFireOnInitialOnClick(true)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(thaHome).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1),
                            new PrimaryDrawerItem().withName(thaPreviews).withIcon(GoogleMaterial.Icon.gmd_palette).withIdentifier(2),
                            new PrimaryDrawerItem().withName(thaApply).withIcon(GoogleMaterial.Icon.gmd_open_in_browser).withIdentifier(5),
                            new PrimaryDrawerItem().withName(thaFAQs).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(6),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName(thaCredits).withIdentifier(7),
                            new SecondaryDrawerItem().withName(thaCredits).withIdentifier(8),
                            new SecondaryDrawerItem().withName(thaSettings).withIdentifier(9)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null) {
                                switch (drawerItem.getIdentifier()) {
                                    case 1:
                                        switchFragment(1, thaApp, "Main", context);
                                        break;
                                    case 2:
                                        switchFragment(2, thaPreviews, "Previews", context);
                                        break;
                                    case 3:
                                        switchFragment(3, thaWalls, "Wallpapers", context);
                                        break;
                                    case 4:
                                        if (!permissionGranted) {
                                            drawer.closeDrawer();
                                            Assent.requestPermissions(new AssentCallback() {
                                                @Override
                                                public void onPermissionResult(PermissionResultSet result) {
                                                    permissionGranted = result.isGranted
                                                            (Assent.WRITE_EXTERNAL_STORAGE);
                                                }
                                            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
                                            if (permissionGranted) {
                                                switchFragment(4, thaRequest, "Requests",
                                                        context);
                                            }
                                        } else {
                                            switchFragment(4, thaRequest, "Requests", context);
                                        }
                                        break;
                                    case 5:
                                        switchFragment(5, thaApply, "Apply", context);
                                        break;
                                    case 6:
                                        switchFragment(6, thaFAQs, "FAQs", context);
                                        break;
                                    case 7:
                                        switchFragment(7, thaCredits, "Credits", context);
                                        break;
                                    case 8:
                                        switchFragment(8, thaCredits, "AltCredits", context);
                                        break;
                                    case 9:
                                        switchFragment(9, thaSettings, "Settings", context);
                                }
                            }
                            return false;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .build();
        } else {
            drawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withFireOnInitialOnClick(true)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(thaHome).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1),
                            new PrimaryDrawerItem().withName(thaPreviews).withIcon(GoogleMaterial.Icon.gmd_palette).withIdentifier(2),
                            new PrimaryDrawerItem().withName(thaApply).withIcon(GoogleMaterial.Icon.gmd_open_in_browser).withIdentifier(5),
                            new PrimaryDrawerItem().withName(thaFAQs).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(6),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName(thaCredits).withIdentifier(7),
                            new SecondaryDrawerItem().withName(thaCredits).withIdentifier(8),
                            new SecondaryDrawerItem().withName(thaSettings).withIdentifier(9)
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem != null) {
                                switch (drawerItem.getIdentifier()) {
                                    case 1:
                                        switchFragment(1, thaApp, "Main", context);
                                        break;
                                    case 2:
                                        switchFragment(2, thaPreviews, "Previews", context);
                                        break;
                                    case 3:
                                        switchFragment(3, thaWalls, "Wallpapers", context);
                                        break;
                                    case 4:
                                        if (!permissionGranted) {
                                            drawer.closeDrawer();
                                            Assent.requestPermissions(new AssentCallback() {
                                                @Override
                                                public void onPermissionResult(PermissionResultSet result) {
                                                    permissionGranted = result.isGranted
                                                            (Assent.WRITE_EXTERNAL_STORAGE);
                                                }
                                            }, 69, Assent.WRITE_EXTERNAL_STORAGE);
                                            if (permissionGranted) {
                                                switchFragment(4, thaRequest, "Requests",
                                                        context);
                                            }
                                        } else {
                                            switchFragment(4, thaRequest, "Requests", context);
                                        }
                                        break;
                                    case 5:
                                        switchFragment(5, thaApply, "Apply", context);
                                        break;
                                    case 6:
                                        switchFragment(6, thaFAQs, "FAQs", context);
                                        break;
                                    case 7:
                                        switchFragment(7, thaCredits, "Credits", context);
                                        break;
                                    case 8:
                                        switchFragment(8, thaCredits, "AltCredits", context);
                                        break;
                                    case 9:
                                        switchFragment(9, thaSettings, "Settings", context);
                                }
                            }
                            return false;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .build();
        }
    }

}