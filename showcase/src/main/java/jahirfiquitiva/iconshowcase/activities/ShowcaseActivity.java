package jahirfiquitiva.iconshowcase.activities;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.mikepenz.materialize.util.UIUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.dialogs.FolderChooserDialog;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.fragments.SettingsFragment;
import jahirfiquitiva.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.iconshowcase.models.DrawerHeaderStyle;
import jahirfiquitiva.iconshowcase.models.IconsLists;
import jahirfiquitiva.iconshowcase.models.WallpapersList;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.views.CustomCoordinatorLayout;

public class ShowcaseActivity extends AppCompatActivity implements FolderChooserDialog.FolderSelectionCallback {

    public static boolean WITH_LICENSE_CHECKER = false,
            WITH_INSTALLED_FROM_AMAZON = false,
            WITH_ZOOPER_SECTION = false,
            WITH_ICONS_BASED_CHANGELOG = false,
            WITH_USER_WALLPAPER_AS_TOOLBAR_HEADER = true,
            WITH_ALTERNATIVE_ABOUT_SECTION = true,
            WITH_SECONDARY_DRAWER_ITEMS_ICONS = false;

    /*
    Change between drawer header options:
    NORMAL_HEADER
    MINI_HEADER
    NO_HEADER
     */
    public static DrawerHeaderStyle drawerHeaderStyle = DrawerHeaderStyle.MINI_HEADER;

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    private String action = "action";
    private static final String
            adw_action = "org.adw.launcher.icons.ACTION_PICK_ICON",
            turbo_action = "com.phonemetra.turbo.launcher.icons.ACTION_PICK_ICON",
            nova_action = "com.novalauncher.THEME";

    public static boolean iconPicker, wallsPicker;

    private static String thaHome, thaPreviews, thaApply, thaWalls, thaRequest, thaFAQs,
            thaZooper, thaCredits, thaSettings;

    private static AppCompatActivity context;

    public String version;

    public static int currentItem = -1, wallpaper = -1, seven = 7;

    private boolean mLastTheme, mLastNavBar;
    private static Preferences mPrefs;

    public static MaterialDialog settingsDialog;
    public static Toolbar toolbar;
    public static AppBarLayout appbar;
    public static CollapsingToolbarLayout collapsingToolbarLayout;
    public static CustomCoordinatorLayout coordinatorLayout;
    public static FloatingActionButton fab;
    public static ImageView icon1, icon2, icon3, icon4;
    public static TextView titleView;

    public static Drawer drawer;
    public AccountHeader drawerHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        super.onCreate(savedInstanceState);

        context = this;
        mPrefs = new Preferences(ShowcaseActivity.this);

        getAction();

        setContentView(R.layout.showcase_activity);

        coordinatorLayout = (CustomCoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleView = (TextView) findViewById(R.id.title);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        icon1 = (ImageView) findViewById(R.id.iconOne);
        icon2 = (ImageView) findViewById(R.id.iconTwo);
        icon3 = (ImageView) findViewById(R.id.iconThree);
        icon4 = (ImageView) findViewById(R.id.iconFour);

        GridLayout iconsRow = (GridLayout) findViewById(R.id.iconsRow);

        setupIcons(icon1, icon2, icon3, icon4, this);

        iconsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentItem == 1) {
                    setupIcons(icon1, icon2, icon3, icon4, context);
                    animateIcons(icon1, icon2, icon3, icon4);
                }
            }
        });

        thaHome = getResources().getString(R.string.section_one);
        thaPreviews = getResources().getString(R.string.section_two);
        thaApply = getResources().getString(R.string.section_three);
        thaWalls = getResources().getString(R.string.section_four);
        thaRequest = getResources().getString(R.string.section_five);
        thaCredits = getResources().getString(R.string.section_six);
        thaSettings = getResources().getString(R.string.title_settings);
        thaFAQs = getResources().getString(R.string.faqs_section);
        thaZooper = getResources().getString(R.string.zooper_section_title);

        CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = layoutParams.height + UIUtils.getStatusBarHeight(this);
        toolbar.setLayoutParams(layoutParams);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupDrawer(toolbar, savedInstanceState);

        runLicenseChecker();

        if (savedInstanceState == null) {
            if (iconPicker) {
                drawerItemClick(2);
                drawer.setSelection(2);
            } else if (wallsPicker && mPrefs.areFeaturesEnabled()) {
                drawerItemClick(3);
                drawer.setSelection(3);
            } else {
                if (mPrefs.getSettingsModified()) {
                    if (WITH_ZOOPER_SECTION) {
                        drawerItemClick(seven + 2);
                        drawer.setSelection(seven + 2);
                    } else {
                        drawerItemClick(seven + 1);
                        drawer.setSelection(seven + 1);
                    }
                } else {
                    currentItem = -1;
                    drawerItemClick(1);
                    drawer.setSelection(1);
                }
            }
        }
    }

    public static void switchFragment(int itemId, String title, String fragment,
                                      AppCompatActivity context) {

        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;

        if (fragment.equals("Main")) {
            icon1.setVisibility(View.INVISIBLE);
            icon2.setVisibility(View.INVISIBLE);
            icon3.setVisibility(View.INVISIBLE);
            icon4.setVisibility(View.INVISIBLE);
            appbar.setExpanded(true, mPrefs.getAnimationsEnabled());
            appbar.setActivated(true);
            coordinatorLayout.setScrollAllowed(true);
        } else if (!fragment.equals("Previews")) {
            appbar.setExpanded(false, mPrefs.getAnimationsEnabled());
            appbar.setActivated(false);
            coordinatorLayout.setScrollAllowed(false);
        }

        setupFAB(fragment);

        if (mPrefs.getAnimationsEnabled()) {
            context.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                    .replace(R.id.main, Fragment.instantiate(context,
                            "jahirfiquitiva.iconshowcase.fragments." + fragment + "Fragment"))
                    .commit();
        } else {
            context.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, Fragment.instantiate(context,
                            "jahirfiquitiva.iconshowcase.fragments." + fragment + "Fragment"))
                    .commit();
        }

        titleView.setText(title);

        if (drawer != null) {
            drawer.setSelection(itemId);
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
            ThemeUtils.restartActivity(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null)
            outState = drawer.saveInstanceState(outState);
        if (getSupportActionBar() != null) {
            outState.putString("toolbarTitle", String.valueOf(titleView.getText()));
        }
        outState.putInt("currentSection", currentItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (getSupportActionBar() != null)
            titleView.setText(savedInstanceState.getString("toolbarTitle", "   "));
        drawerItemClick(savedInstanceState.getInt("currentSection"));
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.permissionReceived() != null)
                    PermissionUtils.permissionReceived().onStoragePermissionGranted();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int i = item.getItemId();
        if (i == R.id.changelog) {
            if (WITH_ICONS_BASED_CHANGELOG) {
                ISDialogs.showIconsChangelogDialog(this);
            } else {
                ISDialogs.showChangelogDialog(this);
            }

        } else if (i == R.id.refresh) {
            WallpapersFragment.refreshWalls(context);
            loadWallsList();

        } else if (i == R.id.columns) {
            ISDialogs.showColumnsSelectorDialog(context);

        }
        return true;
    }

    private void runLicenseChecker() {
        mPrefs.setSettingsModified(false);
        mPrefs.setFirstRun(getSharedPreferences("PrefsFile", MODE_PRIVATE).getBoolean("first_run", true));
        if (mPrefs.isFirstRun()) {
            if (WITH_LICENSE_CHECKER) {
                checkLicense();
            } else {
                mPrefs.setFeaturesEnabled(true);
                showChangelogDialog();
            }
            mPrefs.setFirstRun(false);
            getSharedPreferences("PrefsFile", MODE_PRIVATE).edit()
                    .putBoolean("first_run", false).commit();
        } else {
            if (WITH_LICENSE_CHECKER) {
                if (!mPrefs.areFeaturesEnabled()) {
                    showNotLicensedDialog();
                } else {
                    showChangelogDialog();
                }
            } else {
                showChangelogDialog();
            }
        }
    }

    private void showChangelogDialog() {
        String launchinfo = getSharedPreferences("PrefsFile", MODE_PRIVATE).getString("version", "0");
        storeSharedPrefs();
        if (launchinfo != null && !launchinfo.equals(Utils.getAppVersion(this))) {
            if (WITH_ICONS_BASED_CHANGELOG) {
                ISDialogs.showIconsChangelogDialog(this);
            } else {
                ISDialogs.showChangelogDialog(this);
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private void storeSharedPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences("PrefsFile", MODE_PRIVATE);
        sharedPreferences.edit().putString("version", Utils.getAppVersion(this)).commit();
    }

    private void checkLicense() {
        String installer = getPackageManager().getInstallerPackageName(getPackageName());
        try {
            if (installer.equals("com.google.android.feedback") ||
                    installer.equals("com.android.vending")) {
                ISDialogs.showLicenseSuccessDialog(this, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mPrefs.setFeaturesEnabled(true);
                        showChangelogDialog();
                    }
                });
            } else if (installer.equals("com.amazon.venezia") && WITH_INSTALLED_FROM_AMAZON) {
                ISDialogs.showLicenseSuccessDialog(this, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mPrefs.setFeaturesEnabled(true);
                        showChangelogDialog();
                    }
                });
            } else {
                showNotLicensedDialog();
            }
        } catch (Exception e) {
            showNotLicensedDialog();
        }
    }

    private void showNotLicensedDialog() {
        mPrefs.setFeaturesEnabled(false);
        ISDialogs.showLicenseFailedDialog(this,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + getPackageName()));
                        startActivity(browserIntent);
                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        finish();
                    }
                });
    }

    public void loadWallsList() {
        if (mPrefs.getWallsListLoaded()) {
            WallpapersList.clearList();
            mPrefs.setWallsListLoaded(!mPrefs.getWallsListLoaded());
        }
        new WallpapersFragment.DownloadJSON(new WallsListInterface() {
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
        }, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public interface WallsListInterface {
        void checkWallsListCreation(boolean result);
    }

    public void onFolderSelection(@NonNull File folder) {
        mPrefs.setDownloadsFolder(folder.getAbsolutePath());
        SettingsFragment.changeValues(getApplicationContext());
    }

    public void setupDrawer(Toolbar toolbar, Bundle savedInstanceState) {

        PrimaryDrawerItem home = new PrimaryDrawerItem().withName(thaHome).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1);
        PrimaryDrawerItem previews = new PrimaryDrawerItem().withName(thaPreviews).withIcon(GoogleMaterial.Icon.gmd_palette).withIdentifier(2);
        PrimaryDrawerItem walls = new PrimaryDrawerItem().withName(thaWalls).withIcon(GoogleMaterial.Icon.gmd_landscape).withIdentifier(3);
        PrimaryDrawerItem requests = new PrimaryDrawerItem().withName(thaRequest).withIcon(GoogleMaterial.Icon.gmd_comment_list).withIdentifier(4);
        PrimaryDrawerItem apply = new PrimaryDrawerItem().withName(thaApply).withIcon(GoogleMaterial.Icon.gmd_open_in_browser).withIdentifier(5);
        PrimaryDrawerItem faqs = new PrimaryDrawerItem().withName(thaFAQs).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(6);

        SecondaryDrawerItem creditsItem, settingsItem;

        DrawerBuilder drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .withFireOnInitialOnClick(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            drawerItemClick(drawerItem.getIdentifier());
                        }
                        return false;
                    }
                })
                .withDisplayBelowStatusBar(false);

        if (WITH_ZOOPER_SECTION) {
            PrimaryDrawerItem zooper = new PrimaryDrawerItem().withName(thaZooper).withIcon(GoogleMaterial.Icon.gmd_widgets).withIdentifier(seven);
            if (WITH_SECONDARY_DRAWER_ITEMS_ICONS) {
                creditsItem = new SecondaryDrawerItem().withName(thaCredits).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(seven + 1);
                settingsItem = new SecondaryDrawerItem().withName(thaSettings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(seven + 2);
            } else {
                creditsItem = new SecondaryDrawerItem().withName(thaCredits).withIdentifier(seven + 1);
                settingsItem = new SecondaryDrawerItem().withName(thaSettings).withIdentifier(seven + 2);
            }
            drawerBuilder.addDrawerItems(home, previews, walls, requests, apply, faqs, zooper, new DividerDrawerItem(), creditsItem, settingsItem);
        } else {
            if (WITH_SECONDARY_DRAWER_ITEMS_ICONS) {
                creditsItem = new SecondaryDrawerItem().withName(thaCredits).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(seven);
                settingsItem = new SecondaryDrawerItem().withName(thaSettings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(seven + 1);
            } else {
                creditsItem = new SecondaryDrawerItem().withName(thaCredits).withIdentifier(seven);
                settingsItem = new SecondaryDrawerItem().withName(thaSettings).withIdentifier(seven + 1);
            }
            drawerBuilder.addDrawerItems(home, previews, walls, requests, apply, faqs, new DividerDrawerItem(), creditsItem, settingsItem);
        }

        drawerBuilder.withSavedInstance(savedInstanceState);

        switch (drawerHeaderStyle) {
            case NORMAL_HEADER:
                drawerHeader = new AccountHeaderBuilder()
                        .withActivity(this)
                        .withHeaderBackground(R.drawable.drawer_header)
                        .withSelectionFirstLine(getResources().getString(R.string.app_long_name))
                        .withSelectionSecondLine("v " + Utils.getAppVersion(this))
                        .withProfileImagesClickable(false)
                        .withResetDrawerOnProfileListClick(false)
                        .withSelectionListEnabled(false)
                        .withSelectionListEnabledForSingleProfile(false)
                        .withSavedInstance(savedInstanceState)
                        .build();

                drawerBuilder.withAccountHeader(drawerHeader);
                break;
            case MINI_HEADER:
                drawerBuilder.withHeader(R.layout.mini_drawer_header);
                break;
            case NO_HEADER:
                break;
        }

        drawer = drawerBuilder.build();

        if (drawerHeaderStyle.equals(DrawerHeaderStyle.MINI_HEADER)) {
            ImageView miniHeader = (ImageView) drawer.getHeader().findViewById(R.id.mini_drawer_header);
            miniHeader.getLayoutParams().height = UIUtils.getActionBarHeight(this) + UIUtils.getStatusBarHeight(this);
            TextView appVersion = (TextView) drawer.getHeader().findViewById(R.id.text_app_version);
            appVersion.setText(getString(R.string.app_version, Utils.getAppVersion(this)));
        }

    }

    public static void drawerItemClick(int id) {
        switch (id) {
            case 1:
                switchFragment(1, "   ", "Main", context);
                break;
            case 2:
                switchFragment(2, thaPreviews, "Previews", context);
                break;
            case 3:
                switchFragment(3, thaWalls, "Wallpapers", context);
                break;
            case 4:
                switchFragment(4, thaRequest, "Requests", context);
                break;
            case 5:
                switchFragment(5, thaApply, "Apply", context);
                break;
            case 6:
                switchFragment(6, thaFAQs, "FAQs", context);
                break;
            case 7:
                if (WITH_ZOOPER_SECTION) {
                    switchFragment(7, thaZooper, "Zooper", context);
                } else {
                    if (WITH_ALTERNATIVE_ABOUT_SECTION) {
                        switchFragment(7, thaCredits, "CreditsAlt", context);
                    } else {
                        switchFragment(7, thaCredits, "Credits", context);
                    }
                }
                break;
            case 8:
                if (WITH_ZOOPER_SECTION) {
                    if (WITH_ALTERNATIVE_ABOUT_SECTION) {
                        switchFragment(8, thaCredits, "CreditsAlt", context);
                    } else {
                        switchFragment(8, thaCredits, "Credits", context);
                    }
                } else {
                    switchFragment(8, thaSettings, "Settings", context);
                }
                break;
            case 9:
                if (WITH_ZOOPER_SECTION) {
                    switchFragment(9, thaSettings, "Settings", context);
                }
                break;
        }
    }

    public void getAction() {
        try {
            action = getIntent().getAction();
        } catch (Exception e) {
            action = "action";
        }

        try {
            switch (action) {
                case adw_action:
                case turbo_action:
                case nova_action:
                case Intent.ACTION_PICK:
                case Intent.ACTION_GET_CONTENT:
                    iconPicker = true;
                    wallsPicker = false;
                    break;
                case Intent.ACTION_SET_WALLPAPER:
                    iconPicker = false;
                    wallsPicker = true;
                    break;
                default:
                    iconPicker = false;
                    wallsPicker = false;
                    break;
            }
        } catch (ActivityNotFoundException | NullPointerException e) {
            iconPicker = false;
            wallsPicker = false;
        }

    }

    public static void setupIcons(final ImageView icon1, final ImageView icon2,
                                  final ImageView icon3, final ImageView icon4, Context context) {

        ArrayList<Integer> icons = IconsLists.getPreviewAL();
        ArrayList<Integer> finalIconsList = new ArrayList<>();

        if (icons != null) {
            Collections.shuffle(icons);
        }

        int numOfIcons = context.getResources().getInteger(R.integer.icons_grid_width);
        int i = 0;

        if (icons != null) {
            while (i < numOfIcons) {
                finalIconsList.add(icons.get(i));
                i++;
            }

            icon1.setImageResource(finalIconsList.get(0));
            icon2.setImageResource(finalIconsList.get(1));
            icon3.setImageResource(finalIconsList.get(2));
            icon4.setImageResource(finalIconsList.get(3));

        }
    }

    public static void animateIcons(ImageView icon1, ImageView icon2,
                                    ImageView icon3, ImageView icon4) {

        icon1.setVisibility(View.VISIBLE);
        icon2.setVisibility(View.VISIBLE);
        icon3.setVisibility(View.VISIBLE);
        icon4.setVisibility(View.VISIBLE);

        if (mPrefs.getAnimationsEnabled()) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.bounce);
            playIconsAnimations(icon1, icon2, icon3, icon4, anim);
        }

    }

    private static void playIconsAnimations(ImageView icon1, ImageView icon2,
                                            ImageView icon3, ImageView icon4, Animation anim) {
        icon1.startAnimation(anim);
        icon2.startAnimation(anim);
        icon3.startAnimation(anim);
        icon4.startAnimation(anim);

    }

    private static void setupFAB(String fragment) {
        switch (fragment) {
            case "Main":
                fab.setVisibility(View.VISIBLE);
                fab.show();
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerItemClick(5);
                        drawer.setSelection(5);
                    }
                });
                break;
            default:
                fab.setVisibility(View.GONE);
                break;
        }
    }

    public static void setupToolbarHeader(Context context) {

        ImageView toolbarHeader = (ImageView) ((AppCompatActivity) context).findViewById(R.id.toolbarHeader);
        ProgressBar progress = (ProgressBar) ((AppCompatActivity) context).findViewById(R.id.headerProgressBar);

        if (WITH_USER_WALLPAPER_AS_TOOLBAR_HEADER && mPrefs.getWallpaperAsToolbarHeaderEnabled()) {
            WallpaperManager wm = WallpaperManager.getInstance(context);
            if (wm != null) {
                Drawable currentWallpaper = wm.getFastDrawable();
                if (currentWallpaper != null) {
                    float alpha = 0.8f;
                    toolbarHeader.setAlpha(alpha);
                    toolbarHeader.setImageDrawable(currentWallpaper);
                }
            }
        } else {

            ArrayList<Integer> wallpapersArray = new ArrayList<>();
            String[] newIcons = context.getResources().getStringArray(R.array.wallpapers);

            for (String extra : newIcons) {
                int res = context.getResources().getIdentifier(extra, "drawable", context.getPackageName());
                if (res != 0) {
                    final int thumbRes = context.getResources().getIdentifier(extra, "drawable", context.getPackageName());
                    if (thumbRes != 0) {
                        wallpapersArray.add(thumbRes);
                    }
                }
            }

            Random random = new Random();

            if (wallpaper == -1) {
                wallpaper = random.nextInt(wallpapersArray.size());
            }

            toolbarHeader.setImageResource(wallpapersArray.get(wallpaper));
        }

        progress.setVisibility(View.INVISIBLE);
        toolbarHeader.setVisibility(View.VISIBLE);
    }

    public void openFileChooser() {
        //TODO ADD FOLDER CHOOSER
    }

}