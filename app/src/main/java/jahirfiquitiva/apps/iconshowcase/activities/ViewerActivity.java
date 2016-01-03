package jahirfiquitiva.apps.iconshowcase.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.melnykov.fab.FloatingActionButton;
import com.mikepenz.materialize.MaterializeBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.tasks.ApplyWallpaper;
import jahirfiquitiva.apps.iconshowcase.tasks.WallpaperToCrop;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;
import jahirfiquitiva.apps.iconshowcase.views.TouchImageView;

public class ViewerActivity extends AppCompatActivity {

    private boolean mLastTheme, mLastNavBar;

    public static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";
    private int mIndex;
    private String mIndexText, transitionName, wallUrl, wallName, wallAuthor;
    private TouchImageView mPhoto;

    private FloatingActionButton fab;
    private RelativeLayout layout;
    private Snackbar snackbar;
    private static Preferences mPrefs;
    private static File downloadsFolder;
    public static MaterialDialog dialogApply;

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        ThemeUtils.onActivityCreateSetTheme(this);

        super.onCreate(savedInstanceState);

        mPrefs = new Preferences(ViewerActivity.this);

        Assent.setActivity(this, this);

        Intent intent = getIntent();
        mIndex = intent.getIntExtra(EXTRA_CURRENT_ITEM_POSITION, 1);
        mIndexText = intent.getStringExtra("indexText");
        transitionName = intent.getStringExtra("transitionName");
        wallUrl = intent.getStringExtra("wallUrl");
        wallName = intent.getStringExtra("wallName");
        wallAuthor = intent.getStringExtra("authorName");

        setContentView(R.layout.wall_viewer_activity);

        new MaterializeBuilder()
                .withActivity(this)
                .build();

        mPhoto = (TouchImageView) findViewById(R.id.big_wallpaper);
        ViewCompat.setTransitionName(mPhoto, transitionName);

        layout = (RelativeLayout) findViewById(R.id.viewerLayout);

        context = this;

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(wallName);
            getSupportActionBar().setSubtitle(getResources().getString(R.string.wallpaper_by,
                    wallAuthor));
        }

        fab = (FloatingActionButton) findViewById(R.id.walls_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    Assent.requestPermissions(new AssentCallback() {
                        @Override
                        public void onPermissionResult(PermissionResultSet result) {
                            if (result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                                if (Util.hasNetwork(context)) {
                                    showOptions();
                                } else {
                                    showNotConnectedSnackBar(fab, context);
                                }
                            }
                        }
                    }, 69, Assent.WRITE_EXTERNAL_STORAGE);
                } else {
                    if (Util.hasNetwork(context)) {
                        showOptions();
                    } else {
                        showNotConnectedSnackBar(fab, context);
                    }
                }
            }
        });

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Drawable d = new GlideBitmapDrawable(getResources(), bmp);

        if (mPrefs.getAnimationsEnabled()) {
            Glide.with(this)
                    .load(wallUrl)
                    .placeholder(d)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(mPhoto);
        } else {
            Glide.with(this)
                    .load(wallUrl)
                    .placeholder(d)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .into(mPhoto);
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
            this.recreate();/*
            this.startActivity(new Intent(this, this.getClass()));
            this.finish();
            this.overridePendingTransition(0, 0);
            */
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                supportFinishAfterTransition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogApply != null) {
            dialogApply.dismiss();
            dialogApply = null;
        }
    }

    private void showOptions() {
        new MaterialDialog.Builder(this)
                .title(R.string.wallpaper)
                .items(R.array.wallpaper_options)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, final int i, CharSequence charSequence) {
                        performOptionIon(context, i, wallUrl, wallName, mPhoto);
                    }
                }).show();
    }

    public void performOptionIon(final Activity context, int selection, final String url, final String name, final TouchImageView view) {
        if (selection == 0) {
            showApplyWallpaperDialog(context, url);
        } else {
            final MaterialDialog downloadDialog = new MaterialDialog.Builder(context)
                    .content(R.string.downloading_wallpaper)
                    .progress(true, 0)
                    .cancelable(false)
                    .show();
            Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (resource != null) {
                                saveWallpaper(context, name, downloadDialog, resource);
                            }
                        }
                    });
        }
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
                        fab.hide(true);
                        Snackbar longSnackbar = Snackbar.make(layout, finalSnackbarText,
                                Snackbar.LENGTH_LONG);
                        longSnackbar.show();
                        longSnackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                fab.show(true);
                            }
                        });
                        if (!longSnackbar.isShown()) {
                            fab.show(true);
                        }
                    }
                });
            }
        }).start();
    }

    public void showApplyWallpaperDialog(final Activity context, final String wallurl) {
        new MaterialDialog.Builder(this)
                .title(R.string.apply)
                .content(R.string.confirm_apply)
                .positiveText(R.string.apply)
                .neutralText(R.string.crop)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        dialogApply = new MaterialDialog.Builder(context)
                                .content(R.string.downloading_wallpaper)
                                .progress(true, 0)
                                .cancelable(false)
                                .show();
                        Glide.with(context)
                                .load(wallurl)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (resource != null) {
                                            fab.hide(true);
                                            dialogApply.setContent(context.getString(R.string.setting_wall_title));
                                            new ApplyWallpaper(context, dialogApply, resource,
                                                    false, layout, fab).execute();
                                        }
                                    }
                                });
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (dialogApply != null) {
                            dialogApply.dismiss();
                        }
                        dialogApply = new MaterialDialog.Builder(context)
                                .content(R.string.downloading_wallpaper)
                                .progress(true, 0)
                                .cancelable(false)
                                .show();
                        Glide.with(context)
                                .load(wallurl)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (resource != null) {
                                            new WallpaperToCrop(context, dialogApply, resource,
                                                    layout, fab, wallName).execute();
                                        }
                                    }
                                });
                    }
                }).show();
    }

    private void showNotConnectedSnackBar(final FloatingActionButton fab, final Context context) {
        fab.hide(true);
        String retry = getResources().getString(R.string.retry);
        snackbar = Snackbar
                .make(layout, R.string.no_conn_title, Snackbar.LENGTH_INDEFINITE)
                .setAction(retry.toUpperCase(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Util.hasNetwork(context)) {
                            snackbar.dismiss();
                            showOptions();
                            fab.show(true);
                        } else {
                            showNotConnectedSnackBar(fab, context);
                        }
                    }
                });
        snackbar.setActionTextColor(getResources().getColor(R.color.accent));
        snackbar.show();
    }

}