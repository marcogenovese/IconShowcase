/*
 * Copyright (c) 2016.  Jahir Fiquitiva
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
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

/*
 *
 */

package jahirfiquitiva.iconshowcase.activities;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.piracychecker.PiracyChecker;
import com.github.javiersantos.piracychecker.enums.InstallerID;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerCallback;
import com.github.javiersantos.piracychecker.enums.PiracyCheckerError;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.sufficientlysecure.donations.google.util.IabHelper;
import org.sufficientlysecure.donations.google.util.IabResult;
import org.sufficientlysecure.donations.google.util.Inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Random;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.RequestsAdapter;
import jahirfiquitiva.iconshowcase.dialogs.ChangelogDialog;
import jahirfiquitiva.iconshowcase.dialogs.FolderSelectorDialog;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.enums.DrawerType;
import jahirfiquitiva.iconshowcase.fragments.ApplyFragment;
import jahirfiquitiva.iconshowcase.fragments.BaseFragment;
import jahirfiquitiva.iconshowcase.fragments.CreditsFragment;
import jahirfiquitiva.iconshowcase.fragments.DonationsFragment;
import jahirfiquitiva.iconshowcase.fragments.FAQsFragment;
import jahirfiquitiva.iconshowcase.fragments.KustomFragment;
import jahirfiquitiva.iconshowcase.fragments.MainFragment;
import jahirfiquitiva.iconshowcase.fragments.PreviewsFragment;
import jahirfiquitiva.iconshowcase.fragments.RequestsFragment;
import jahirfiquitiva.iconshowcase.fragments.SettingsFragment;
import jahirfiquitiva.iconshowcase.fragments.WallpapersFragment;
import jahirfiquitiva.iconshowcase.fragments.ZooperFragment;
import jahirfiquitiva.iconshowcase.models.IconItem;
import jahirfiquitiva.iconshowcase.models.WallpapersList;
import jahirfiquitiva.iconshowcase.services.NotificationsService;
import jahirfiquitiva.iconshowcase.tasks.LoadIconsLists;
import jahirfiquitiva.iconshowcase.tasks.TasksExecutor;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;

public class ShowcaseActivity extends BaseActivity implements FolderSelectorDialog.FolderSelectionCallback {

    private static boolean
            WITH_LICENSE_CHECKER = false,
            WITH_INSTALLED_FROM_AMAZON = false,
            WITH_DONATIONS_SECTION = false,

    //Donations stuff
    DONATIONS_GOOGLE = false,
            DONATIONS_PAYPAL = false,
            DONATIONS_FLATTR = false,
            DONATIONS_BITCOIN = false,

    ENABLE_DEV_OPTIONS = false;

    public static boolean WITH_ZOOPER_SECTION = false, SELECT_ALL_APPS = true;
    private static boolean ENABLE_USER_WALLPAPER_IN_TOOLBAR = true;

    private static String[] mGoogleCatalog = new String[0],
            GOOGLE_CATALOG_VALUES = new String[0],
            GOOGLE_CATALOG_FREE, GOOGLE_CATALOG_PRO;

    private static String GOOGLE_PUBKEY = "", PAYPAL_USER = "", PAYPAL_CURRENCY_CODE = "";

    private IabHelper mHelper;

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";
    private boolean mIsPremium = false, installedFromPlayStore = false;

    private static final String adw_action = "org.adw.launcher.icons.ACTION_PICK_ICON", turbo_action = "com.phonemetra.turbo.launcher.icons.ACTION_PICK_ICON",
            nova_action = "com.novalauncher.THEME";

    public static boolean iconsPicker, wallsPicker, SHUFFLE = true;
    private static boolean iconsPickerEnabled = false, wallsEnabled = false, shuffleIcons = true;

    private static String thaAppName;

    private static AppCompatActivity context;

    public static long currentItem = -1;

    private static int numOfIcons = 4, wallpaper = -1, curVersionCode = 0;

    private boolean mLastTheme;
    private static Preferences mPrefs;

    //TODO remove static variables if possible
    //TODO do not save Dialog instance; use fragment tags
    private MaterialDialog settingsDialog, changelogDialog;
    public static Toolbar toolbar;
    public static AppBarLayout appbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView icon1, icon2, icon3, icon4, icon5, icon6, icon7, icon8;
    public static ImageView toolbarHeader;
    public static Bitmap toolbarHeaderImage;
    public static Drawable wallpaperDrawable;
    private List<DrawerType> mDrawerItems;
    private HashMap<DrawerType, Integer> mDrawerMap = new HashMap<>();

    private Drawer drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        super.onCreate(savedInstanceState);

        context = this;
        mPrefs = new Preferences(this);

        ENABLE_DEV_OPTIONS = getResources().getBoolean(R.bool.dev_options);

        String installer = getIntent().getStringExtra("installer");
        int notifType = getIntent().getIntExtra("launchNotifType", 2);

        curVersionCode = getIntent().getIntExtra("curVersionCode", -1);

        WITH_DONATIONS_SECTION = getIntent().getBooleanExtra("enableDonations", false);
        DONATIONS_GOOGLE = getIntent().getBooleanExtra("enableGoogleDonations", false);
        DONATIONS_PAYPAL = getIntent().getBooleanExtra("enablePayPalDonations", false);
        DONATIONS_FLATTR = getIntent().getBooleanExtra("enableFlattrDonations", false);
        DONATIONS_BITCOIN = getIntent().getBooleanExtra("enableBitcoinDonations", false);

        WITH_LICENSE_CHECKER = getIntent().getBooleanExtra("enableLicenseCheck", false);
        WITH_INSTALLED_FROM_AMAZON = getIntent().getBooleanExtra("enableAmazonInstalls", false);

        GOOGLE_PUBKEY = getIntent().getStringExtra("googlePubKey");

        ENABLE_USER_WALLPAPER_IN_TOOLBAR = getResources().getBoolean(R.bool.enable_user_wallpaper_in_toolbar);

        getAction();

        setContentView(R.layout.showcase_activity);

        TasksExecutor.with(context)
                .loadJust((iconsPicker && iconsPickerEnabled),
                        ((notifType == 1) || (wallsPicker && mPrefs.areFeaturesEnabled() && wallsEnabled)));

        shuffleIcons = getResources().getBoolean(R.bool.shuffle_toolbar_icons);

        mPrefs.setActivityVisible(true);

        if (notifType == 1) {
            NotificationsService.clearNotification(context, 97);
        }

        if (notifType == 2) {
            NotificationsService.clearNotification(context, 19);
        }

        try {
            //TODO move theses strings to final
            if (installer.matches("com.google.android.feedback") || installer.matches("com.android.vending")) {
                installedFromPlayStore = true;
            }
        } catch (Exception e) {
            //Do nothing
        }

        runLicenseChecker(GOOGLE_PUBKEY);

        setupDonations();

        //TODO dynamically add icons rather than defining 8 and hiding those that aren't used
        numOfIcons = context.getResources().getInteger(R.integer.toolbar_icons);

        icon1 = (ImageView) findViewById(R.id.iconOne);
        icon2 = (ImageView) findViewById(R.id.iconTwo);
        icon3 = (ImageView) findViewById(R.id.iconThree);
        icon4 = (ImageView) findViewById(R.id.iconFour);
        icon5 = (ImageView) findViewById(R.id.iconFive);
        icon6 = (ImageView) findViewById(R.id.iconSix);
        icon7 = (ImageView) findViewById(R.id.iconSeven);
        icon8 = (ImageView) findViewById(R.id.iconEight);

        setupFab(R.id.fab);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        toolbarHeader = (ImageView) findViewById(R.id.toolbarHeader);

        setSupportActionBar(toolbar);

        thaAppName = getResources().getString(R.string.app_name);

        collapsingToolbarLayout.setTitle(thaAppName);
        Utils.setupCollapsingToolbarTextColors(context, collapsingToolbarLayout);
        setupDrawer(toolbar, savedInstanceState);

        //Setup donations
        if (DONATIONS_GOOGLE) {
            final IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                    if (inventory != null) {
                        Utils.showLog(context, "IAP inventory exists");
                        for (String aGOOGLE_CATALOG_FREE : GOOGLE_CATALOG_FREE) {
                            Utils.showLog(context, aGOOGLE_CATALOG_FREE + " is " + inventory.hasPurchase(aGOOGLE_CATALOG_FREE));
                            if (inventory.hasPurchase(aGOOGLE_CATALOG_FREE)) { //at least one donation value found, now premium
                                mIsPremium = true;
                            }
                        }
                    }
                    if (isPremium()) {
                        mGoogleCatalog = GOOGLE_CATALOG_PRO;
                    }
                }
            };

            mHelper = new IabHelper(ShowcaseActivity.this, GOOGLE_PUBKEY);
            mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                public void onIabSetupFinished(IabResult result) {
                    if (!result.isSuccess()) {
                        Utils.showLog(context, "In-app Billing setup failed: " + result); //TODO move text to string?
                        new MaterialDialog.Builder(ShowcaseActivity.this).title(R.string.donations_error_title)
                                .content(R.string.donations_error_content)
                                .positiveText(android.R.string.ok)
                                .show();

                    } else {
                        mHelper.queryInventoryAsync(false, mGotInventoryListener);
                    }

                }
            });
        }

        if (savedInstanceState == null) {
            if (notifType == 1) {
                drawerItemSelectAndClick(mDrawerMap.get(DrawerType.WALLPAPERS));
            } else if (iconsPicker && iconsPickerEnabled) {
                drawerItemSelectAndClick(mDrawerMap.get(DrawerType.REQUESTS));
            } else if (wallsPicker && mPrefs.areFeaturesEnabled() && wallsEnabled) {
                drawerItemSelectAndClick(mDrawerMap.get(DrawerType.WALLPAPERS));
            } else {
                if (mPrefs.getSettingsModified()) {
                    //TODO check this
                    long settingsIdentifier = 0;
                    drawerItemSelectAndClick(settingsIdentifier);
                } else {
                    currentItem = -1;
                    drawerItemSelectAndClick(0);
                }
            }
        }
    }

    private void switchFragment(long itemId, DrawerType dt, AppCompatActivity context) {

        if (currentItem == itemId) {
            // Don't allow re-selection of the currently active item
            return;
        }
        currentItem = itemId;

        if (dt == DrawerType.HOME && !iconsPicker && !wallsPicker) {
            Utils.expandToolbar(this);
        } else {
            Utils.collapseToolbar(this);
        }

        if (dt == DrawerType.HOME) {
            icon1.setVisibility(View.INVISIBLE);
            icon2.setVisibility(View.INVISIBLE);
            icon3.setVisibility(View.INVISIBLE);
            icon4.setVisibility(View.INVISIBLE);
        }

        //Fragment Switcher
        FragmentTransaction fragmentTransaction = context.getSupportFragmentManager()
                .beginTransaction();

        Fragment fragment;

        switch (dt) {
            case DONATE:
                fragment = DonationsFragment.newInstance(DONATIONS_GOOGLE,
                        GOOGLE_PUBKEY,
                        mGoogleCatalog,
                        GOOGLE_CATALOG_VALUES,
                        DONATIONS_PAYPAL,
                        PAYPAL_USER,
                        PAYPAL_CURRENCY_CODE,
                        context.getString(R.string.section_donate),
                        DONATIONS_FLATTR,
                        DONATIONS_BITCOIN);
                break;
            case HOME:
                fragment = new MainFragment();
                break;
            case PREVIEWS:
                fragment = new PreviewsFragment();
                break;
            case WALLPAPERS:
                fragment = new WallpapersFragment();
                break;
            case REQUESTS:
                fragment = new RequestsFragment();
                break;
            case APPLY:
                fragment = new ApplyFragment();
                break;
            case FAQS:
                fragment = new FAQsFragment();
                break;
            case ZOOPER:
                fragment = new ZooperFragment();
                break;
            case KUSTOM:
                fragment = new KustomFragment();
                break;
            case CREDITS:
                fragment = new CreditsFragment();
                break;
            case SETTINGS:
                fragment = new SettingsFragment();
                break;
            default:
                //throw error
                fragment = new MainFragment();
                break;
        }
        fragmentTransaction.replace(getFragmentId(), fragment, dt.getName());

        if (mPrefs.getAnimationsEnabled())
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.commit();

        collapsingToolbarLayout.setTitle(getString(dt.getTitleID()));

        if (drawer != null) {
            drawer.setSelection(itemId);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLastTheme = ThemeUtils.darkTheme;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPrefs == null) {
            mPrefs = new Preferences(this);
        }
        if (!iconsPicker && !wallsPicker) {
            setupToolbarHeader(this, toolbarHeader);
        }
        ColorUtils.setupToolbarIconsAndTextsColors(context, appbar, toolbar);
        if (mLastTheme != ThemeUtils.darkTheme) {
            ThemeUtils.restartActivity(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (drawer != null) {
            outState = drawer.saveInstanceState(outState);
        }
        if (collapsingToolbarLayout != null && collapsingToolbarLayout.getTitle() != null) {
            outState.putString("toolbarTitle", collapsingToolbarLayout.getTitle().toString());
        }
        outState.putInt("currentSection", (int) currentItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (collapsingToolbarLayout != null) {
            Utils.setupCollapsingToolbarTextColors(this, collapsingToolbarLayout);
            collapsingToolbarLayout.setTitle(savedInstanceState.getString("toolbarTitle", thaAppName));
        }
        drawerItemClick(savedInstanceState.getInt("currentSection"));
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else if (drawer != null && currentItem != 1 && !iconsPicker) {
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
        if (changelogDialog != null) {
            changelogDialog.dismiss();
            changelogDialog = null;
        }
        if (mPrefs == null) {
            mPrefs = new Preferences(this);
        }
        mPrefs.setActivityVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            if (permissionGranted(grantResult) && PermissionUtils.permissionReceived() != null) {
                PermissionUtils.permissionReceived().onStoragePermissionGranted();
            }
        }
    }

    private boolean permissionGranted(int[] results) {
        return results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int i = item.getItemId();
        if (i == R.id.changelog) {
            ChangelogDialog.show(this);
        } else if (i == R.id.refresh) {
            WallpapersFragment.refreshWalls(context);
            loadWallsList(this);
        } else if (i == R.id.columns) {
            ISDialogs.showColumnsSelectorDialog(context);
        } else if (i == R.id.select_all) {
            RequestsAdapter requestsAdapter = RequestsFragment.requestsAdapter;
            if (requestsAdapter != null && RequestsFragment.requestsAdapter.appsList.size() > 0) {
                RequestsFragment.requestsAdapter.selectOrDeselectAll(SELECT_ALL_APPS, mPrefs);
                SELECT_ALL_APPS = !SELECT_ALL_APPS;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 2) {
            RequestsAdapter adapter = ((RequestsAdapter) RequestsFragment.mRecyclerView.getAdapter());
            if (adapter != null) {
                adapter.deselectAllApps();
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void runLicenseChecker(String licenseKey) {
        mPrefs.setSettingsModified(false);
        if (WITH_LICENSE_CHECKER) {
            checkLicense(context, mPrefs, licenseKey);
        } else {
            mPrefs.setFeaturesEnabled(true);
            showChangelogDialog();
        }
    }

    private void showChangelogDialog() {

        int prevVersionCode = mPrefs.getVersionCode();

        if ((curVersionCode > prevVersionCode) && (curVersionCode > -1)) {
            mPrefs.setVersionCode(curVersionCode);
            ChangelogDialog.show(this);
        }

    }

    private void checkLicense(final Context context, final Preferences mPrefs, String licenseKey) {
        PiracyChecker checker = new PiracyChecker(context);

        if ((licenseKey != null) && (!(licenseKey.isEmpty())) && (licenseKey.length() > 25)) {
            checker.enableGooglePlayLicensing(licenseKey);
        }

        checker.enableInstallerId(InstallerID.GOOGLE_PLAY);

        if (WITH_INSTALLED_FROM_AMAZON) {
            checker.enableInstallerId(InstallerID.AMAZON_APP_STORE);
        }

        checker.callback(new PiracyCheckerCallback() {
            @Override
            public void allow() {
                ISDialogs.showLicenseSuccessDialog(context, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        mPrefs.setFeaturesEnabled(true);
                        showChangelogDialog();
                    }
                }, new MaterialDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mPrefs.setFeaturesEnabled(true);
                        showChangelogDialog();
                    }
                }, new MaterialDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mPrefs.setFeaturesEnabled(true);
                        showChangelogDialog();
                    }
                });
            }

            @Override
            public void dontAllow(PiracyCheckerError piracyCheckerError) {
                showNotLicensedDialog((Activity) context, mPrefs);
            }
        });

        checker.start();
    }

    private void showNotLicensedDialog(final Activity act, Preferences mPrefs) {
        mPrefs.setFeaturesEnabled(false);
        ISDialogs.showLicenseFailDialog(act, new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ShowcaseActivity.MARKET_URL + act.getPackageName()));
                act.startActivity(browserIntent);
            }
        }, new MaterialDialog.SingleButtonCallback() {

            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                act.finish();
            }
        }, new MaterialDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                act.finish();
            }
        }, new MaterialDialog.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                act.finish();
            }
        });
    }

    private void loadWallsList(Context context) {
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
        }, context).execute();
    }

    @Override
    public void onFolderSelection(@NonNull File folder) {
        mPrefs.setDownloadsFolder(folder.getAbsolutePath());
        SettingsFragment.changeWallsFolderValue(this, mPrefs);
    }

    @Override
    protected int getFragmentId() {
        return R.id.main;
    }

    public interface WallsListInterface {

        void checkWallsListCreation(boolean result);
    }

    private DrawerType drawerKeyToType(String s) {
        switch (s.toLowerCase()) {
            case "previews":
                return DrawerType.PREVIEWS;
            case "wallpapers":
                wallsEnabled = true;
                return DrawerType.WALLPAPERS;
            case "requests":
                iconsPickerEnabled = true;
                return DrawerType.REQUESTS;
            case "apply":
                return DrawerType.APPLY;
            case "faqs":
                return DrawerType.FAQS;
            case "zooper":
                return DrawerType.ZOOPER;
            case "kustom":
                return DrawerType.KUSTOM;
            default:
                //TODO add better catch;
                throw new RuntimeException("Invalid drawer key " + s + ".\nPlease check your primary_drawer_items array");
        }
    }

    @SuppressWarnings("ResourceAsColor")
    private void setupDrawer(final Toolbar toolbar, Bundle savedInstanceState) {
        mDrawerItems = new ArrayList<>();
        mDrawerItems.add(DrawerType.HOME);

        //Convert keys to enums
        String[] configurePrimaryDrawerItems = getResources().getStringArray(R.array.primary_drawer_items);

        for (String s : configurePrimaryDrawerItems) {
            mDrawerItems.add(drawerKeyToType(s));
        }
        mDrawerItems.add(DrawerType.CREDITS);
        mDrawerItems.add(DrawerType.SETTINGS);
        if (WITH_DONATIONS_SECTION) mDrawerItems.add(DrawerType.DONATE);

        DrawerBuilder drawerBuilder = new DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withFireOnInitialOnClick(true)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            drawerItemClick(drawerItem.getIdentifier());
                        }
                        return false;
                    }
                });

        for (int i = 0; i < mDrawerItems.size(); i++) {
            DrawerType dt = mDrawerItems.get(i);
            //TODO change; credits will always be the first secondary item
            if (dt == DrawerType.CREDITS) {
                drawerBuilder.addDrawerItems(new DividerDrawerItem());
            }
            mDrawerMap.put(dt, i);
            drawerBuilder.addDrawerItems(DrawerType.getDrawerItem(context, dt, i));
        }

        drawerBuilder.withSavedInstance(savedInstanceState);

        String headerAppName = "", headerAppVersion = "";

        boolean withDrawerTexts;

        if (ENABLE_DEV_OPTIONS) {
            withDrawerTexts = mPrefs.getDevDrawerTexts();
        } else {
            withDrawerTexts = getResources().getBoolean(R.bool.with_drawer_texts);
        }

        if (withDrawerTexts) {
            headerAppName = getResources().getString(R.string.app_long_name);
            headerAppVersion = "v " + Utils.getAppVersion(this);
        }

        AccountHeader drawerHeader = new AccountHeaderBuilder().withActivity(this)
                .withHeaderBackground(ThemeUtils.darkTheme ? ThemeUtils.transparent ? R.drawable
                        .drawer_header_clear : R.drawable.drawer_header_dark : R.drawable.drawer_header_light)
                .withSelectionFirstLine(headerAppName)
                .withSelectionSecondLine(headerAppVersion)
                .withProfileImagesClickable(false)
                .withResetDrawerOnProfileListClick(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withSavedInstance(savedInstanceState)
                .build();

        drawerBuilder.withAccountHeader(drawerHeader);

        drawer = drawerBuilder.build();

    }

    private void drawerItemSelectAndClick(long id) {
        drawer.setSelection(id);
        drawerItemClick(id);
    }

    private void drawerItemClick(long id) {
        switchFragment(id, mDrawerItems.get((int) id), context);
    }

    private void getAction() {
        String action;
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
                    iconsPicker = true;
                    wallsPicker = false;
                    break;
                case Intent.ACTION_SET_WALLPAPER:
                    iconsPicker = false;
                    wallsPicker = true;
                    break;
                default:
                    iconsPicker = false;
                    wallsPicker = false;
                    break;
            }
        } catch (ActivityNotFoundException | NullPointerException e) {
            iconsPicker = false;
            wallsPicker = false;
        }
    }

    public void setupIcons() {

        ArrayList<IconItem> icons = null;

        if (LoadIconsLists.getIconsLists() != null) {
            //noinspection ConstantConditions
            icons = LoadIconsLists.getIconsLists().get(0).getIconsArray();
        }

        ArrayList<IconItem> finalIconsList = new ArrayList<>();

        if (icons != null && SHUFFLE && shuffleIcons) {
            Collections.shuffle(icons);
        }

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

            if (numOfIcons == 6) {
                icon5.setImageResource(finalIconsList.get(4).getResId());
                icon6.setImageResource(finalIconsList.get(5).getResId());
            } else if (numOfIcons == 8) {
                icon5.setImageResource(finalIconsList.get(4).getResId());
                icon6.setImageResource(finalIconsList.get(5).getResId());
                icon7.setImageResource(finalIconsList.get(6).getResId());
                icon8.setImageResource(finalIconsList.get(7).getResId());
            }
        }

        SHUFFLE = false;

    }

    public void animateIcons(int delay) {

        if (!iconsPicker && !wallsPicker) {
            switch (numOfIcons) {
                case 4:
                    if (icon1 != null) {
                        icon1.setVisibility(View.VISIBLE);
                    }
                    if (icon2 != null) {
                        icon2.setVisibility(View.VISIBLE);
                    }
                    if (icon3 != null) {
                        icon3.setVisibility(View.VISIBLE);
                    }
                    if (icon4 != null) {
                        icon4.setVisibility(View.VISIBLE);
                    }
                    break;
                case 6:
                    if (icon1 != null) {
                        icon1.setVisibility(View.VISIBLE);
                    }
                    if (icon2 != null) {
                        icon2.setVisibility(View.VISIBLE);
                    }
                    if (icon3 != null) {
                        icon3.setVisibility(View.VISIBLE);
                    }
                    if (icon4 != null) {
                        icon4.setVisibility(View.VISIBLE);
                    }
                    if (icon5 != null) {
                        icon5.setVisibility(View.VISIBLE);
                    }
                    if (icon6 != null) {
                        icon6.setVisibility(View.VISIBLE);
                    }
                    break;
                case 8:
                    if (icon1 != null) {
                        icon1.setVisibility(View.VISIBLE);
                    }
                    if (icon2 != null) {
                        icon2.setVisibility(View.VISIBLE);
                    }
                    if (icon3 != null) {
                        icon3.setVisibility(View.VISIBLE);
                    }
                    if (icon4 != null) {
                        icon4.setVisibility(View.VISIBLE);
                    }
                    if (icon5 != null) {
                        icon5.setVisibility(View.VISIBLE);
                    }
                    if (icon6 != null) {
                        icon6.setVisibility(View.VISIBLE);
                    }
                    if (icon7 != null) {
                        icon7.setVisibility(View.VISIBLE);
                    }
                    if (icon8 != null) {
                        icon8.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        if (mPrefs.getAnimationsEnabled()) {
            final Animation anim = AnimationUtils.loadAnimation(context, R.anim.bounce);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playIconsAnimations(anim);
                }
            }, delay);
        }

    }

    private void playIconsAnimations(Animation anim) {

        icon1.startAnimation(anim);
        icon2.startAnimation(anim);
        icon3.startAnimation(anim);
        icon4.startAnimation(anim);

        switch (numOfIcons) {
            case 6:
                icon5.startAnimation(anim);
                icon6.startAnimation(anim);
                break;
            case 8:
                icon5.startAnimation(anim);
                icon6.startAnimation(anim);
                icon7.startAnimation(anim);
                icon8.startAnimation(anim);
                break;
        }
    }

    public static void setupToolbarHeader(Context context, ImageView toolbarHeader) {

        if (ENABLE_USER_WALLPAPER_IN_TOOLBAR && mPrefs.getWallpaperAsToolbarHeaderEnabled()) {
            WallpaperManager wm = WallpaperManager.getInstance(context);

            if (wm != null) {
                Drawable currentWallpaper = wm.getFastDrawable();
                if (currentWallpaper != null) {
                    toolbarHeader.setAlpha(0.9f);
                    toolbarHeader.setImageDrawable(currentWallpaper);
                    wallpaperDrawable = currentWallpaper;
                    toolbarHeaderImage = Utils.drawableToBitmap(currentWallpaper);
                }
            }
        } else {
            String[] wallpapers = context.getResources().getStringArray(R.array.wallpapers);

            if (wallpapers.length > 0) {
                int res;
                ArrayList<Integer> wallpapersArray = new ArrayList<>();

                for (String wallpaper : wallpapers) {
                    res = context.getResources().getIdentifier(wallpaper, "drawable", context.getPackageName());
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

                wallpaperDrawable = ContextCompat.getDrawable(context, wallpapersArray.get(wallpaper));
                toolbarHeader.setImageDrawable(wallpaperDrawable);
                toolbarHeaderImage = Utils.drawableToBitmap(ContextCompat.getDrawable(context, wallpapersArray.get(wallpaper)));
            }
        }

        toolbarHeader.setVisibility(View.VISIBLE);
    }

    private boolean isPremium() {
        return mIsPremium;
    }

    public void setSettingsDialog(MaterialDialog settingsDialog) {
        this.settingsDialog = settingsDialog;
    }

    public MaterialDialog getChangelogDialog() {
        return this.changelogDialog;
    }

    public void setChangelogDialog(MaterialDialog changelogDialog) {
        this.changelogDialog = changelogDialog;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public AppBarLayout getAppbar() {
        return appbar;
    }

    public ImageView getToolbarHeader() {
        return toolbarHeader;
    }

    public Bitmap getToolbarHeaderImage() {
        return toolbarHeaderImage;
    }

    public Drawer getDrawer() {
        return drawer;
    }

    private void setupDonations() {
        //donations stuff

        if (installedFromPlayStore) {
            // Disable donation methods not allowed by Google
            DONATIONS_PAYPAL = false;
            DONATIONS_FLATTR = false;
            DONATIONS_BITCOIN = false;
        }

        //google
        if (DONATIONS_GOOGLE) {
            GOOGLE_CATALOG_FREE = getResources().getStringArray(R.array.nonconsumable_google_donation_items);
            GOOGLE_CATALOG_PRO = getResources().getStringArray(R.array.consumable_google_donation_items);
            mGoogleCatalog = GOOGLE_CATALOG_FREE;
            GOOGLE_CATALOG_VALUES = getResources().getStringArray(R.array.google_donations_catalog);

            try {
                if (!(GOOGLE_PUBKEY.length() > 50) || !(GOOGLE_CATALOG_VALUES.length > 0) || !(GOOGLE_CATALOG_FREE.length == GOOGLE_CATALOG_PRO.length) || !
                        (GOOGLE_CATALOG_FREE.length == GOOGLE_CATALOG_VALUES.length)) {
                    DONATIONS_GOOGLE = false; //google donations setup is incorrect
                }
            } catch (Exception e) {
                DONATIONS_GOOGLE = false;
            }

        }

        //paypal
        if (DONATIONS_PAYPAL) {
            PAYPAL_USER = getResources().getString(R.string.paypal_user);
            PAYPAL_CURRENCY_CODE = getResources().getString(R.string.paypal_currency_code);
            if (!(PAYPAL_USER.length() > 5) || !(PAYPAL_CURRENCY_CODE.length() > 1)) {
                DONATIONS_PAYPAL = false; //paypal content incorrect
            }
        }

        if (WITH_DONATIONS_SECTION) {
            WITH_DONATIONS_SECTION = DONATIONS_GOOGLE || DONATIONS_PAYPAL || DONATIONS_FLATTR || DONATIONS_BITCOIN; //if one of the donations are enabled,
            // then the section is enabled
        }
    }

}