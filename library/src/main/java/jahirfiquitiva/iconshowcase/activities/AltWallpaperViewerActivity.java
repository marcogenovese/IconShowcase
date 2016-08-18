/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

/*
 *
 */

package jahirfiquitiva.iconshowcase.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.models.WallpaperItem;
import jahirfiquitiva.iconshowcase.tasks.ApplyWallpaper;
import jahirfiquitiva.iconshowcase.tasks.WallpaperToCrop;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import jahirfiquitiva.iconshowcase.utilities.color.ToolbarColorizer;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;
import jahirfiquitiva.iconshowcase.views.TouchImageView;


public class AltWallpaperViewerActivity extends AppCompatActivity {

    private boolean mLastTheme, mLastNavBar, opened = false;

    private WallpaperItem item;
    private CoordinatorLayout layout;
    private static Preferences mPrefs;
    private static File downloadsFolder;
    private MaterialDialog dialogApply;
    private static MaterialDialog downloadDialog;
    private Activity context;
    private FloatingActionButton fab, applyFab, saveFab, infoFab;

    @SuppressWarnings("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //TODO: Find the exact way to make the status bar **completely** transparent
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);

        context = this;

        mPrefs = new Preferences(context);

        mPrefs.setActivityVisible(true);

        Intent intent = getIntent();
        String transitionName = intent.getStringExtra("transitionName");

        item = intent.getParcelableExtra("item");

        setContentView(R.layout.alt_wallpaper_viewer_activity);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        applyFab = (FloatingActionButton) findViewById(R.id.applyFab);
        saveFab = (FloatingActionButton) findViewById(R.id.saveFab);
        infoFab = (FloatingActionButton) findViewById(R.id.infoFab);

        hideFab(applyFab);
        hideFab(saveFab);
        hideFab(infoFab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(item.getWallName());
            getSupportActionBar().setSubtitle(item.getWallAuthor());
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_with_shadow);
            changeToolbarTextAppearance(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (Build.VERSION.SDK_INT < 19) {
            ToolbarColorizer.colorizeToolbar(toolbar, ContextCompat.getColor(context, android.R.color.white));
        }

        int tintLightLighter = ContextCompat.getColor(context, R.color.drawable_base_tint);
        int tintDark = ContextCompat.getColor(context, R.color.drawable_tint_dark);

        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) fab.getLayoutParams();
        int fabMargin = context.getResources().getDimensionPixelSize(R.dimen.cards_padding);
        p.setMargins(0, 0, fabMargin, (fabMargin + Utils.getNavigationBarHeight(context)));
        fab.setLayoutParams(p);

        fab.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                if (opened) {
                    closeMenu();
                } else {
                    openMenu();
                }
                opened = !opened;
            }
        });

        applyFab.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                showApplyWallpaperDialog(context, item.getWallURL());
            }
        });

        if (item.isDownloadable()) {
            saveFab.setOnClickListener(new DebouncedClickListener() {
                @Override
                public void onDebouncedClick(View v) {
                    if (!PermissionUtils.canAccessStorage(context)) {
                        PermissionUtils.setViewerActivityAction("save");
                        PermissionUtils.requestStoragePermission(context);
                    } else {
                        showDialogs("save");
                    }
                }
            });
        } else {
            saveFab.setVisibility(View.GONE);
        }

        infoFab.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                ISDialogs.showWallpaperDetailsDialog(context, item.getWallName(), item.getWallAuthor(), item.getWallDimensions(), item.getWallCopyright());
            }
        });

        TouchImageView mPhoto = (TouchImageView) findViewById(R.id.big_wallpaper);
        ViewCompat.setTransitionName(mPhoto, transitionName);

        layout = (CoordinatorLayout) findViewById(R.id.viewerLayout);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            if (filename != null) {
                FileInputStream is = context.openFileInput(filename);
                bmp = BitmapFactory.decodeStream(is);
                is.close();
            } else {
                bmp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int colorFromCachedPic;

        if (bmp != null) {
            colorFromCachedPic = ColorUtils.getProminentSwatch(bmp).getTitleTextColor();
        } else {
            colorFromCachedPic = ThemeUtils.darkTheme ? tintDark : tintLightLighter;
        }

        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progress);
        spinner.getIndeterminateDrawable()
                .setColorFilter(colorFromCachedPic, PorterDuff.Mode.SRC_IN);

        Drawable d;
        if (bmp != null) {
            d = new GlideBitmapDrawable(getResources(), bmp);
        } else {
            d = new ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent));
        }

        if (mPrefs.getAnimationsEnabled()) {
            Glide.with(context)
                    .load(item.getWallURL())
                    .placeholder(d)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            spinner.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mPhoto);
        } else {
            Glide.with(context)
                    .load(item.getWallURL())
                    .placeholder(d)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            spinner.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mPhoto);
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
        if (mLastTheme != ThemeUtils.darkTheme) {
            ThemeUtils.restartActivity(context);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogApply != null) {
            dialogApply.dismiss();
            dialogApply = null;
        }
        if (mPrefs == null) {
            mPrefs = new Preferences(this);
        }
        mPrefs.setActivityVisible(false);
    }

    @Override
    public void onBackPressed() {
        closeViewer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeViewer();
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                if (PermissionUtils.getViewerActivityAction().equals("crop")) {
                    cropWallpaper(item.getWallURL());
                } else {
                    showDialogs(PermissionUtils.getViewerActivityAction());
                }
            } else {
                ISDialogs.showPermissionNotGrantedDialog(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            showFab(fab);
            fab.animate().rotation(0.0f).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void changeToolbarTextAppearance(Toolbar toolbar) {
        TextView title, subtitle;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            title = (TextView) f.get(toolbar);
            setTextAppearance(title, R.style.ToolbarTitleWithShadow);
            try {
                Field f2 = toolbar.getClass().getDeclaredField("mSubtitleTextView");
                f2.setAccessible(true);
                subtitle = (TextView) f2.get(toolbar);
                setTextAppearance(subtitle, R.style.ToolbarSubtitleWithShadow);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                //Do nothing
            }
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            //Do nothing
        }
    }

    @SuppressWarnings("deprecation")
    private void setTextAppearance(TextView text, @StyleRes int style) {
        if (Build.VERSION.SDK_INT < 23) {
            text.setTextAppearance(context, style);
        } else {
            text.setTextAppearance(style);
        }
    }

    private void closeViewer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            finish();
        }
    }

    private void openMenu() {
        fab.animate().rotation(45.0f).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
        showFab(applyFab);
        showFab(saveFab);
        showFab(infoFab);
    }

    private void closeMenu() {
        hideFab(infoFab);
        hideFab(saveFab);
        hideFab(applyFab);
        fab.animate().rotation(0.0f).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
    }

    private void showFab(FloatingActionButton fab) {
        if (fab != null) {
            fab.show();
            fab.setVisibility(View.VISIBLE);
        }
    }

    private void hideFab(FloatingActionButton fab) {
        if (fab != null) {
            fab.hide();
            fab.setVisibility(View.GONE);
        }
    }

    private void saveWallpaperAction(final String name, String url) {

        if (downloadDialog != null) {
            downloadDialog.dismiss();
        }

        if (opened) {
            closeMenu();
            opened = false;
        }
        hideFab(fab);

        final boolean[] enteredDownloadTask = {false};

        downloadDialog = new MaterialDialog.Builder(context)
                .content(R.string.downloading_wallpaper)
                .progress(true, 0)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (downloadDialog != null) {
                            downloadDialog.dismiss();
                        }
                    }
                })
                .show();

        Glide.with(context)
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource != null && downloadDialog.isShowing()) {
                            enteredDownloadTask[0] = true;
                            saveWallpaper(context, name, downloadDialog, resource);
                        }
                    }
                });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUIThread(context, new Runnable() {
                    @Override
                    public void run() {
                        if (!enteredDownloadTask[0]) {
                            String newContent = context.getString(R.string.downloading_wallpaper)
                                    + "\n"
                                    + context.getString(R.string.download_takes_longer);
                            downloadDialog.setContent(newContent);
                            downloadDialog.setActionButton(DialogAction.POSITIVE, android.R.string.cancel);
                        }
                    }
                });
            }
        }, 15000);
    }

    private void saveWallpaper(final Activity context, final String wallName,
                               final MaterialDialog downloadDialog, final Bitmap result) {
        downloadDialog.setContent(context.getString(R.string.saving_wallpaper));
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mPrefs.getDownloadsFolder() != null) {
                    downloadsFolder = new File(mPrefs.getDownloadsFolder());
                } else {
                    downloadsFolder = new File(context.getString(R.string.walls_save_location,
                            Environment.getExternalStorageDirectory().getAbsolutePath()));
                }
                //noinspection ResultOfMethodCallIgnored
                downloadsFolder.mkdirs();
                final File destFile = new File(downloadsFolder, wallName + ".png");
                String snackbarText;
                if (!destFile.exists()) {
                    try {
                        result.compress(Bitmap.CompressFormat.PNG, 100,
                                new FileOutputStream(destFile));
                        snackbarText = context.getString(R.string.wallpaper_downloaded,
                                destFile.getAbsolutePath());
                    } catch (final Exception e) {
                        snackbarText = context.getString(R.string.error);
                    }
                } else {
                    snackbarText = context.getString(R.string.wallpaper_downloaded,
                            destFile.getAbsolutePath());
                }
                final String finalSnackbarText = snackbarText;
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadDialog.dismiss();
                        Snackbar longSnackbar = Snackbar.make(layout, finalSnackbarText,
                                Snackbar.LENGTH_LONG);
                        final int snackbarLight = ContextCompat.getColor(context, R.color.snackbar_light);
                        final int snackbarDark = ContextCompat.getColor(context, R.color.snackbar_dark);
                        ViewGroup snackbarView = (ViewGroup) longSnackbar.getView();
                        snackbarView.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark : snackbarLight);
                        snackbarView.setPadding(snackbarView.getPaddingLeft(),
                                snackbarView.getPaddingTop(), snackbarView.getPaddingRight(),
                                Utils.getNavigationBarHeight(context));
                        longSnackbar.show();
                        longSnackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                showFab(fab);
                                fab.animate().rotation(0.0f).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    private void showApplyWallpaperDialog(final Activity context, final String wallUrl) {
        ISDialogs.showApplyWallpaperDialog(context,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (dialogApply != null) {
                            dialogApply.dismiss();
                        }

                        if (opened) {
                            closeMenu();
                            opened = false;
                        }
                        hideFab(fab);

                        final ApplyWallpaper[] applyTask = new ApplyWallpaper[1];

                        final boolean[] enteredApplyTask = {false};

                        dialogApply = new MaterialDialog.Builder(context)
                                .content(R.string.downloading_wallpaper)
                                .progress(true, 0)
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if (applyTask[0] != null) {
                                            applyTask[0].cancel(true);
                                        }
                                        dialogApply.dismiss();
                                    }
                                })
                                .show();

                        Glide.with(context)
                                .load(wallUrl)
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(
                                            final Bitmap resource,
                                            GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (resource != null && dialogApply.isShowing()) {
                                            enteredApplyTask[0] = true;

                                            if (dialogApply != null) {
                                                dialogApply.dismiss();
                                            }

                                            dialogApply = new MaterialDialog.Builder(context)
                                                    .content(R.string.setting_wall_title)
                                                    .progress(true, 0)
                                                    .cancelable(false)
                                                    .show();

                                            applyTask[0] = new ApplyWallpaper(context, dialogApply,
                                                    resource, false, layout, new ApplyWallpaper.ApplyCallback() {
                                                @Override
                                                public void afterApplied() {
                                                    showFab(fab);
                                                    fab.animate().rotation(0.0f).withLayer().setDuration(300).setInterpolator(new OvershootInterpolator(10.0F)).start();
                                                }
                                            });
                                            applyTask[0].execute();
                                        }
                                    }
                                });

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUIThread(context, new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!enteredApplyTask[0]) {
                                            String newContent = context.getString(R.string.downloading_wallpaper)
                                                    + "\n"
                                                    + context.getString(R.string.download_takes_longer);
                                            dialogApply.setContent(newContent);
                                            dialogApply.setActionButton(DialogAction.POSITIVE, android.R.string.cancel);
                                        }
                                    }
                                });
                            }
                        }, 15000);
                    }
                }, new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        if (!PermissionUtils.canAccessStorage(context)) {
                            PermissionUtils.setViewerActivityAction("crop");
                            PermissionUtils.requestStoragePermission(context);
                        } else {
                            cropWallpaper(wallUrl);
                        }
                    }
                });
    }

    private void showNotConnectedSnackBar() {
        Snackbar notConnectedSnackBar = Snackbar.make(layout, R.string.no_conn_title,
                Snackbar.LENGTH_LONG);

        final int snackbarLight = ContextCompat.getColor(context, R.color.snackbar_light);
        final int snackbarDark = ContextCompat.getColor(context, R.color.snackbar_dark);
        ViewGroup snackbarView = (ViewGroup) notConnectedSnackBar.getView();
        snackbarView.setBackgroundColor(ThemeUtils.darkTheme ? snackbarDark : snackbarLight);
        snackbarView.setPadding(snackbarView.getPaddingLeft(),
                snackbarView.getPaddingTop(), snackbarView.getPaddingRight(),
                Utils.getNavigationBarHeight(context));
        notConnectedSnackBar.show();
    }

    private void showDialogs(String action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            new MaterialDialog.Builder(context)
                    .title(R.string.md_error_label)
                    .content(context.getResources().getString(R.string.md_storage_perm_error,
                            context.getResources().getString(R.string.app_name)))
                    .positiveText(android.R.string.ok)
                    .show();
        } else {
            if (Utils.hasNetwork(context)) {
                switch (action) {
                    case "save":
                        saveWallpaperAction(item.getWallName(), item.getWallURL());
                        break;
                }
            } else {
                showNotConnectedSnackBar();
            }
        }
    }

    private static Handler handler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private static void runOnUIThread(Context context, Runnable r) {
        handler(context).post(r);
    }

    private void cropWallpaper(String wallUrl) {
        if (dialogApply != null) {
            dialogApply.dismiss();
        }

        final WallpaperToCrop[] cropTask = new WallpaperToCrop[1];

        final boolean[] enteredCropTask = {false};

        dialogApply = new MaterialDialog.Builder(context)
                .content(R.string.downloading_wallpaper)
                .progress(true, 0)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (cropTask[0] != null) {
                            cropTask[0].cancel(true);
                        }
                        dialogApply.dismiss();
                    }
                })
                .show();

        Glide.with(context)
                .load(wallUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(final Bitmap resource,
                                                GlideAnimation<? super Bitmap> glideAnimation) {
                        if (resource != null && dialogApply.isShowing()) {
                            enteredCropTask[0] = true;
                            if (dialogApply != null) {
                                dialogApply.dismiss();
                            }
                            dialogApply = new MaterialDialog.Builder(context)
                                    .content(context.getString(R.string.preparing_wallpaper))
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog,
                                                            @NonNull DialogAction which) {
                                            if (cropTask[0] != null) {
                                                cropTask[0].cancel(true);
                                            }
                                            dialogApply.dismiss();
                                        }
                                    })
                                    .show();
                            if (opened) {
                                closeMenu();
                                opened = false;
                            }
                            hideFab(fab);
                            cropTask[0] = new WallpaperToCrop(context, dialogApply, resource,
                                    layout, item.getWallName());
                            cropTask[0].execute();
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUIThread(context, new Runnable() {
                                        @Override
                                        public void run() {
                                            String content = context.getString(R.string.preparing_wallpaper)
                                                    + "\n" + context.getString(R.string.download_takes_longer);

                                            dialogApply.setContent(content);
                                            dialogApply.setActionButton(DialogAction.POSITIVE, android.R.string.cancel);
                                        }
                                    });
                                }
                            }, 7000);
                        }
                    }
                });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUIThread(context, new Runnable() {
                    @Override
                    public void run() {
                        if (!enteredCropTask[0]) {
                            String newContent = context.getString(R.string.downloading_wallpaper)
                                    + "\n"
                                    + context.getString(R.string.download_takes_longer);
                            dialogApply.setContent(newContent);
                            dialogApply.setActionButton(DialogAction.POSITIVE, android.R.string.cancel);
                        }
                    }
                });
            }
        }, 15000);
    }

}