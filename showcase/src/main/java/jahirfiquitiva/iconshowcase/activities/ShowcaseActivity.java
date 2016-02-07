package jahirfiquitiva.iconshowcase.activities;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.sufficientlysecure.donations.DonationsFragment;
import org.sufficientlysecure.donations.google.util.IabHelper;
import org.sufficientlysecure.donations.google.util.IabResult;
import org.sufficientlysecure.donations.google.util.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import jahirfiquitiva.iconshowcase.BuildConfig;
import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.dialogs.FolderChooserDialog;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.iconshowcase.fragments.SettingsFragment;
import jahirfiquitiva.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.iconshowcase.models.DrawerHeaderStyle;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.WallpapersList;
import jahirfiquitiva.iconshowcase.tasks.LoadIconsLists;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.views.CustomCoordinatorLayout;

public class ShowcaseActivity extends AppCompatActivity implements FolderChooserDialog.FolderSelectionCallback {

    public static boolean WITH_LICENSE_CHECKER = false,
            WITH_INSTALLED_FROM_AMAZON = false,
            WITH_ZOOPER_SECTION = false,
            WITH_DONATIONS_SECTION = false,
            WITH_ICONS_BASED_CHANGELOG = true,
            WITH_USER_WALLPAPER_AS_TOOLBAR_HEADER = true,
            WITH_ALTERNATIVE_ABOUT_SECTION = true,
            WITH_SECONDARY_DRAWER_ITEMS_ICONS = false,

    //Donations stuff
    DONATIONS_GOOGLE = false,
            DONATIONS_PAYPAL = false,
            DONATIONS_FLATTR = false,
            DONATIONS_BITCOIN = false;

    private static String[] mGoogleCatalog = new String[0],
            GOOGLE_CATALOG_VALUES = new String[0];

    ///test array values
    private static String[] primaryDrawerItems, secondaryDrawerItems;

    private static String GOOGLE_PUBKEY = new String(),
            PAYPAL_USER = new String(),
            PAYPAL_CURRENCY_CODE = new String();

    public static DrawerHeaderStyle drawerHeaderStyle = DrawerHeaderStyle.NORMAL_HEADER;

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";
    public boolean mIsPremium = false;
    private static String TAG;

    private String action = "action";
    private static final String
            adw_action = "org.adw.launcher.icons.ACTION_PICK_ICON",
            turbo_action = "com.phonemetra.turbo.launcher.icons.ACTION_PICK_ICON",
            nova_action = "com.novalauncher.THEME";

    public static boolean iconPicker, wallsPicker, requestEnabled = false, wallsEnabled = false, applyEnabled = false;

    private static String thaHome, thaPreviews, thaApply, thaWalls, thaRequest, thaDonate, thaFAQs,
            thaZooper, thaCredits, thaSettings;

    private static AppCompatActivity context;

    public String version;

    public static int currentItem = -1, wallpaper = -1, seven = 7, wallsIdentifier = 0, requestIdentifier = 0, applyIdentifier = 0, settingsIdentifier = 0, secondaryStart = 0;

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

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        super.onCreate(savedInstanceState);

        context = this;
        mPrefs = new Preferences(ShowcaseActivity.this);

        String[] configurePrimaryDrawerItems = getResources().getStringArray(R.array.primary_drawer_items);
        primaryDrawerItems = new String[configurePrimaryDrawerItems.length + 1];
        primaryDrawerItems[0] = "Main";
        for (int i = 0; i < configurePrimaryDrawerItems.length; i++) {
            primaryDrawerItems[i + 1] = configurePrimaryDrawerItems[i];
        }

        primaryDrawerItems = validateDrawerItemNames(primaryDrawerItems);

        //SecondaryDrawerItems is now fixed; no need for this
//        String[] configureSecondaryDrawerItems = getResources().getStringArray(R.array.secondary_drawer_items);
//        secondaryDrawerItems = new String[configureSecondaryDrawerItems.length + 1];
//        secondaryDrawerItems[0] = "Credits";
//        for (int i = 0; i < configureSecondaryDrawerItems.length; i++) {
//            secondaryDrawerItems[i + 1] = configureSecondaryDrawerItems[i];
//        }

        getAction();

        Intent intent = getIntent();
        WITH_LICENSE_CHECKER = intent.getBooleanExtra("license_check", false);
        WITH_INSTALLED_FROM_AMAZON = intent.getBooleanExtra("allow_installs_from_amazon", false);
        WITH_DONATIONS_SECTION = intent.getBooleanExtra("with_donations_section", false);

        if (WITH_DONATIONS_SECTION) {
            DONATIONS_GOOGLE = intent.getBooleanExtra("google_method", false);
            if (DONATIONS_GOOGLE) {
                GOOGLE_PUBKEY = intent.getStringExtra("pubkey");
            }
            DONATIONS_PAYPAL = intent.getBooleanExtra("paypal_method", false);
            DONATIONS_FLATTR = intent.getBooleanExtra("flattr_method", false);
            DONATIONS_BITCOIN = intent.getBooleanExtra("bitcoin_method", false);
        }

        WITH_ZOOPER_SECTION = getResources().getBoolean(R.bool.zooper_included);
        WITH_ICONS_BASED_CHANGELOG = getResources().getBoolean(R.bool.icons_changelog);
        WITH_USER_WALLPAPER_AS_TOOLBAR_HEADER = getResources().getBoolean(R.bool.user_wallpaper_in_home);
        WITH_ALTERNATIVE_ABOUT_SECTION = getResources().getBoolean(R.bool.cards_credits);
        WITH_SECONDARY_DRAWER_ITEMS_ICONS = getResources().getBoolean(R.bool.secondary_drawer_items_icons);

        //donations stuff
        //google
        final String[] GOOGLE_CATALOG_FREE = getResources().getStringArray(R.array.nonconsumable_google_donation_items);
        final String[] GOOGLE_CATALOG_PRO = getResources().getStringArray(R.array.consumable_google_donation_items);
        mGoogleCatalog = GOOGLE_CATALOG_FREE;
        GOOGLE_CATALOG_VALUES = getResources().getStringArray(R.array.google_donations_catalog);
        //TODO check if 50 is a good reference value
        if (!(GOOGLE_PUBKEY.length() > 50) || !(GOOGLE_CATALOG_VALUES.length > 0) || !(GOOGLE_CATALOG_FREE.length == GOOGLE_CATALOG_PRO.length) || !(GOOGLE_CATALOG_FREE.length == GOOGLE_CATALOG_VALUES.length)) {
            DONATIONS_GOOGLE = false; //google donations setup is incorrent
        }

        //paypal
        PAYPAL_USER = getResources().getString(R.string.paypal_user);
        PAYPAL_CURRENCY_CODE = getResources().getString(R.string.paypal_currency_code);
        if (!(PAYPAL_USER.length() > 5) || !(PAYPAL_CURRENCY_CODE.length() > 1)) {
            DONATIONS_PAYPAL = false; //paypal content incorrect
        }

        WITH_DONATIONS_SECTION = DONATIONS_GOOGLE || DONATIONS_PAYPAL || DONATIONS_FLATTR || DONATIONS_BITCOIN; //if one of the donations are enabled, then the section is enabled

        //Initialize SecondaryDrawerItems
        if (WITH_DONATIONS_SECTION) {
            secondaryDrawerItems = new String[] {"Credits", "Settings", "Donations"};
        } else {
            secondaryDrawerItems = new String[] {"Credits", "Settings"};
        }

        if (WITH_ALTERNATIVE_ABOUT_SECTION) { //use alternative credits layout if selected
            secondaryDrawerItems[0] = "CreditsAlt";
        }

        switch (getResources().getInteger(R.integer.nav_drawer_header_style)) {
            case 1:
                drawerHeaderStyle = DrawerHeaderStyle.NORMAL_HEADER;
                break;
            case 2:
                drawerHeaderStyle = DrawerHeaderStyle.MINI_HEADER;
                break;
            case 3:
                drawerHeaderStyle = DrawerHeaderStyle.NO_HEADER;
                break;
        }

        setContentView(R.layout.showcase_activity);

        runLicenseChecker();

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

        thaHome = getResources().getString(R.string.section_home);
        thaPreviews = getResources().getString(R.string.section_icons);
        thaApply = getResources().getString(R.string.section_apply);
        thaWalls = getResources().getString(R.string.section_wallpapers);
        thaRequest = getResources().getString(R.string.section_icon_request);
        thaDonate = getResources().getString(R.string.section_donate);
        thaCredits = getResources().getString(R.string.section_about);
        thaSettings = getResources().getString(R.string.title_settings);
        thaFAQs = getResources().getString(R.string.faqs_section);
        thaZooper = getResources().getString(R.string.zooper_section_title);

        //Setup donations
        final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

                //TODO test this
                if (inventory != null) {
                    Log.d(TAG, "IAP inventory exists");
                    for (int i = 0; i < GOOGLE_CATALOG_FREE.length; i++) {
                        Log.d(TAG, GOOGLE_CATALOG_FREE[i] + " is " + inventory.hasPurchase(GOOGLE_CATALOG_FREE[i]));
                        if (inventory.hasPurchase(GOOGLE_CATALOG_FREE[i])) { //at least one donation value found, now premium
                            mIsPremium = true;
                        }
                    }
                }
                if (isPremium()) {
                    mGoogleCatalog = GOOGLE_CATALOG_PRO;
                }
            }
        };

        CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        layoutParams.height = layoutParams.height + UIUtils.getStatusBarHeight(this);
        toolbar.setLayoutParams(layoutParams);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupDrawer(toolbar, savedInstanceState);

        if (savedInstanceState == null) {
            if (iconPicker && requestEnabled) {
                drawerItemClick(requestIdentifier);
                drawer.setSelection(requestIdentifier);
            } else if (wallsPicker && mPrefs.areFeaturesEnabled() && wallsEnabled) {
                drawerItemClick(wallsIdentifier);
                drawer.setSelection(wallsIdentifier);
            } else {
                if (mPrefs.getSettingsModified()) {
                    drawerItemClick(settingsIdentifier);
                    drawer.setSelection(settingsIdentifier);
                } else {
                    currentItem = -1;
                    drawerItemClick(1);
                    drawer.setSelection(1);
                }
            }
        }
    }

    public static String fragment2title (String fragment) {
        switch (fragment) {
            case "Main":
                return "  ";
            case "Previews":
                return thaPreviews;
            case "Apply":
                return thaApply;
            case "Wallpapers":
                return thaWalls;
            case "Requests":
                return thaRequest;
            case "Zooper":
                return thaZooper;
            case "Donations":
                return thaDonate;
            case "Faqs":
                return thaFAQs;
            case "Credits":
                return thaCredits;
            case "Settings":
                return thaSettings;
        }
        return ":(";
    }

    public static String[] validateDrawerItemNames (String[] list) {
        String[] validatedList = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            if (list[i].toLowerCase().indexOf("apply") != -1) {
                list[i] = "Apply";
            } else if (list[i].toLowerCase().indexOf("credit") != -1) {
                list[i] = "Credits";
            } else if (list[i].toLowerCase().indexOf("faq") != -1) {
                list[i] = "Faqs";
            } else if (list[i].toLowerCase().indexOf("preview") != -1) {
                list[i] = "Previews";
            } else if (list[i].toLowerCase().indexOf("request") != -1) {
                list[i] = "Requests";
            } else if (list[i].toLowerCase().indexOf("setting") != -1) {
                list[i] = "Settings";
            } else if (list[i].toLowerCase().indexOf("wallpaper") != -1) {
                list[i] = "Wallpapers";
            } else if (list[i].toLowerCase().indexOf("zooper") != -1) {
                list[i] = "Zooper";
            } else {
                Log.e("DrawerItemError", list[i] + " is not a valid drawer item");
            }
        }
        return validatedList;
    }

    public static void switchFragment(int itemId, String fragment,
                                      AppCompatActivity context) {

        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;

        setupFAB(fragment);

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

        //Fragment Switcher
        if (mPrefs.getAnimationsEnabled()) {
            if (fragment2title(fragment).equals(thaDonate)) {
                DonationsFragment donationsFragment;
                donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG,
                        DONATIONS_GOOGLE, GOOGLE_PUBKEY, mGoogleCatalog, GOOGLE_CATALOG_VALUES,
                        DONATIONS_PAYPAL, PAYPAL_USER, PAYPAL_CURRENCY_CODE, context.getString(R.string.section_donate),
                        DONATIONS_FLATTR, null, null,
                        DONATIONS_BITCOIN, null);
                context.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main, donationsFragment, "donationsFragment")
                        .commit();
            } else {
                context.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.main, Fragment.instantiate(context,
                                "jahirfiquitiva.iconshowcase.fragments." + fragment + "Fragment"))
                        .commit();
            }
        } else {
            if (fragment2title(fragment).equals(thaDonate)) {
                DonationsFragment donationsFragment;
                donationsFragment = DonationsFragment.newInstance(BuildConfig.DEBUG,
                        DONATIONS_GOOGLE, GOOGLE_PUBKEY, mGoogleCatalog, GOOGLE_CATALOG_VALUES,
                        DONATIONS_PAYPAL, PAYPAL_USER, PAYPAL_CURRENCY_CODE, context.getString(R.string.section_donate),
                        DONATIONS_FLATTR, null, null,
                        DONATIONS_BITCOIN, null);
                context.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, donationsFragment, "donationsFragment")
                        .commit();
            } else {
                context.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main, Fragment.instantiate(context,
                                "jahirfiquitiva.iconshowcase.fragments." + fragment + "Fragment"))
                        .commit();
            }
        }

        titleView.setText(fragment2title(fragment));

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
        } else if (i == R.id.select_all) { //TODO fix this
            RequestsFragment.requestsAdapter.selectOrUnselectAll();
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
        ISDialogs.showLicenseFailDialog(this,
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
                }, new MaterialDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                }, new MaterialDialog.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
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

        //Initialize PrimaryDrawerItem
        PrimaryDrawerItem home, previews, walls, requests, apply, faqs, zooper;

        //initialize SecondaryDrawerItem
        SecondaryDrawerItem creditsItem, settingsItem, donationsItem;

        secondaryStart = primaryDrawerItems.length + 1; //marks the first identifier value that should be used


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

        for(int i = 0; i < primaryDrawerItems.length; i++) {
            switch (primaryDrawerItems[i]) {
                case "Main":
                    home = new PrimaryDrawerItem().withName(thaHome).withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(i + 1);
                    drawerBuilder.addDrawerItems(home);
                    break;
                case "Previews":
                    previews = new PrimaryDrawerItem().withName(thaPreviews).withIcon(GoogleMaterial.Icon.gmd_palette).withIdentifier(i + 1);
                    drawerBuilder.addDrawerItems(previews);
                    break;
                case "Walls":
                    wallsEnabled = true;
                    wallsIdentifier = i + 1;
                    walls = new PrimaryDrawerItem().withName(thaWalls).withIcon(GoogleMaterial.Icon.gmd_landscape).withIdentifier(wallsIdentifier);
                    drawerBuilder.addDrawerItems(walls);
                    break;
                case "Requests":
                    requestEnabled = true;
                    requestIdentifier = i + 1;
                    requests = new PrimaryDrawerItem().withName(thaRequest).withIcon(GoogleMaterial.Icon.gmd_comment_list).withIdentifier(requestIdentifier);
                    drawerBuilder.addDrawerItems(requests);
                    break;
                case "Apply":
                    applyEnabled = true;
                    applyIdentifier = i + 1;
                    apply = new PrimaryDrawerItem().withName(thaApply).withIcon(GoogleMaterial.Icon.gmd_open_in_browser).withIdentifier(applyIdentifier);
                    drawerBuilder.addDrawerItems(apply);
                    break;
                case "Faqs":
                    faqs = new PrimaryDrawerItem().withName(thaFAQs).withIcon(GoogleMaterial.Icon.gmd_help).withIdentifier(i + 1);
                    drawerBuilder.addDrawerItems(faqs);
                    break;
                case "Zooper":
                    zooper = new PrimaryDrawerItem().withName(thaZooper).withIcon(GoogleMaterial.Icon.gmd_widgets).withIdentifier(i + 1);
                    drawerBuilder.addDrawerItems(zooper);
                    break;
            }
        }

        drawerBuilder.addDrawerItems(new DividerDrawerItem()); //divider between primary and secondary

        if (WITH_SECONDARY_DRAWER_ITEMS_ICONS) {
            for(int i = 0; i < secondaryDrawerItems.length; i++) {
                switch (secondaryDrawerItems[i]) {
                    case "Credits":
                        creditsItem = new SecondaryDrawerItem().withName(thaCredits).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(i + secondaryStart);
                        drawerBuilder.addDrawerItems(creditsItem);
                        break;
                    case "Settings":
                        settingsIdentifier = i + secondaryStart;
                        settingsItem = new SecondaryDrawerItem().withName(thaSettings).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(i + secondaryStart);
                        drawerBuilder.addDrawerItems(settingsItem);
                        break;
                    case "Donations":
                        donationsItem = new SecondaryDrawerItem().withName(thaDonate).withIcon(GoogleMaterial.Icon.gmd_money_box).withIdentifier(i + secondaryStart);
                        drawerBuilder.addDrawerItems(donationsItem);
                        break;
                }
            }
        } else {
            for (int i = 0; i < secondaryDrawerItems.length; i++) {
                switch (secondaryDrawerItems[i]) {
                    case "Credits":
                        creditsItem = new SecondaryDrawerItem().withName(thaCredits).withIdentifier(i + secondaryStart);
                        drawerBuilder.addDrawerItems(creditsItem);
                        break;
                    case "Settings":
                        settingsItem = new SecondaryDrawerItem().withName(thaSettings).withIdentifier(i + secondaryStart);
                        drawerBuilder.addDrawerItems(settingsItem);
                        break;
                    case "Donations":
                        donationsItem = new SecondaryDrawerItem().withName(thaDonate).withIdentifier(i + secondaryStart);
                        drawerBuilder.addDrawerItems(donationsItem);
                        break;
                }
            }
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
            if (context.getResources().getBoolean(R.bool.mini_header_solid_color)) {
                miniHeader.setBackgroundColor(ThemeUtils.darkTheme ?
                        ContextCompat.getColor(context, R.color.dark_theme_primary) :
                        ContextCompat.getColor(context, R.color.light_theme_primary));
            } else {
                miniHeader.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.drawer_header));
            }
            TextView appVersion = (TextView) drawer.getHeader().findViewById(R.id.text_app_version);
            appVersion.setText(getString(R.string.app_version, Utils.getAppVersion(this)));
        }

    }

    public static void drawerItemClick(int id) {
        if (id <= primaryDrawerItems.length) {
            switchFragment(id, primaryDrawerItems[id - 1], context);
        } else {
            switchFragment(id, secondaryDrawerItems[id - secondaryStart], context);
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

        ArrayList<IconItem> icons = null;

        if (LoadIconsLists.getIconsLists() != null) {
            icons = LoadIconsLists.getIconsLists().get(1).getIconsArray();
        }
        ArrayList<IconItem> finalIconsList = new ArrayList<>();

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
            icon1.setImageResource(finalIconsList.get(0).getResId());
            icon2.setImageResource(finalIconsList.get(1).getResId());
            icon3.setImageResource(finalIconsList.get(2).getResId());
            icon4.setImageResource(finalIconsList.get(3).getResId());
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
        if (fragment.equals("Main")) {
            fab.setVisibility(View.VISIBLE);
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (applyEnabled) {
                        drawerItemClick(applyIdentifier);
                        drawer.setSelection(applyIdentifier);
                    } else {
                        Toast.makeText(context, "Contact your dev :/. He or she forgot to add an apply fragment",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            fab.setVisibility(View.GONE);
            fab.hide();
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
            String[] wallpapers = context.getResources().getStringArray(R.array.wallpapers);

            for (String wallpaper : wallpapers) {
                int res = context.getResources().getIdentifier(wallpaper, "drawable", context.getPackageName());
                if (res != 0) {
                    final int thumbRes = context.getResources().getIdentifier(wallpaper, "drawable", context.getPackageName());
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

    public boolean isPremium() {
        return mIsPremium;
    }

}