package jahirfiquitiva.apps.iconshowcase.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.apps.iconshowcase.tasks.ApplyWallpaper;
import jahirfiquitiva.apps.iconshowcase.tasks.WallpaperToCrop;
import jahirfiquitiva.apps.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Preferences;
import jahirfiquitiva.apps.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.apps.iconshowcase.utilities.Utils;
import jahirfiquitiva.apps.iconshowcase.views.TouchImageView;

public class ViewerActivity extends AppCompatActivity {

    private boolean mLastTheme, mLastNavBar;

    public static final String EXTRA_CURRENT_ITEM_POSITION = "extra_current_item_position";

    private String transitionName, wallUrl, wallName, wallAuthor, wallDimensions, wallCopyright;
    private TouchImageView mPhoto;

    private RelativeLayout layout;
    private static Preferences mPrefs;
    private static File downloadsFolder;
    public static MaterialDialog dialogApply;

    public static LinearLayout toHide1, toHide2;

    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ThemeUtils.onActivityCreateSetTheme(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ThemeUtils.onActivityCreateSetNavBar(this);
        }

        super.onCreate(savedInstanceState);

        context = this;

        mPrefs = new Preferences(context);

        Intent intent = getIntent();
        transitionName = intent.getStringExtra("transitionName");
        wallUrl = intent.getStringExtra("wallUrl");
        wallName = intent.getStringExtra("wallName");
        wallAuthor = intent.getStringExtra("authorName");
        wallDimensions = intent.getStringExtra("wallDimensions");
        wallCopyright = intent.getStringExtra("wallCopyright");

        setContentView(R.layout.wall_viewer_activity);

        toHide1 = (LinearLayout) findViewById(R.id.iconsA);
        toHide1 = (LinearLayout) findViewById(R.id.iconsB);

        final int darkgrey = ContextCompat.getColor(context, R.color.card_dark_background);

        Drawable save = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_save)
                .color(darkgrey)
                .sizeDp(24);

        Drawable apply = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_format_paint)
                .color(darkgrey)
                .sizeDp(24);

        Drawable info = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_info_outline)
                .color(darkgrey)
                .sizeDp(24);

        ImageView saveIV = (ImageView) findViewById(R.id.download);
        saveIV.setImageDrawable(save);
        saveIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.canAccessStorage(context)) {
                    PermissionUtils.setViewerActivityAction("save");
                    PermissionUtils.requestStoragePermission(context);
                } else {
                    showDialogs("save");
                }
            }
        });

        ImageView applyIV = (ImageView) findViewById(R.id.apply);
        applyIV.setImageDrawable(apply);
        applyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtils.canAccessStorage(context)) {
                    PermissionUtils.setViewerActivityAction("apply");
                    PermissionUtils.requestStoragePermission(context);
                } else {
                    showDialogs("apply");
                }
            }
        });

        ImageView infoIV = (ImageView) findViewById(R.id.info);
        infoIV.setImageDrawable(info);
        infoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ISDialogs.showWallpaperDetailsDialog(context, wallName, wallAuthor,
                        wallDimensions, wallCopyright);
            }
        });

        mPhoto = (TouchImageView) findViewById(R.id.big_wallpaper);
        ViewCompat.setTransitionName(mPhoto, transitionName);

        layout = (RelativeLayout) findViewById(R.id.viewerLayout);

        TextView wallNameText = (TextView) findViewById(R.id.wallName);
        wallNameText.setText(wallName);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra("image");
        try {
            FileInputStream is = context.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ProgressBar spinner = (ProgressBar) findViewById(R.id.progress);
        spinner.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        Drawable d = new GlideBitmapDrawable(getResources(), bmp);

        int light = ContextCompat.getColor(context, android.R.color.white);
        int grey = ContextCompat.getColor(context, R.color.grey);
        Drawable errorIcon = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_alert_triangle)
                .color(ThemeUtils.darkTheme ? light : grey)
                .sizeDp(192);

        if (mPrefs.getAnimationsEnabled()) {
            Glide.with(context)
                    .load(wallUrl)
                    .placeholder(d)
                    .error(errorIcon)
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
                    .load(wallUrl)
                    .placeholder(d)
                    .error(errorIcon)
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
        mLastNavBar = ThemeUtils.coloredNavBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLastTheme != ThemeUtils.darkTheme
                || mLastNavBar != ThemeUtils.coloredNavBar) {
            ThemeUtils.restartActivity(context);
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialogApply != null) {
            dialogApply.dismiss();
            dialogApply = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == PermissionUtils.PERMISSION_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                showDialogs(PermissionUtils.getViewerActivityAction());
            } else {
                showErrorDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //Crop request
            if (toHide1 != null & toHide2 != null) {
                toHide1.setVisibility(View.VISIBLE);
                toHide2.setVisibility(View.VISIBLE);
            }
        }
    }

    private void saveWallpaperAction(final String name, String url) {
        final MaterialDialog downloadDialog = ISDialogs.showDownloadDialog(context);
        downloadDialog.show();
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

                        if (toHide1 != null & toHide2 != null) {
                            toHide1.setVisibility(View.GONE);
                            toHide2.setVisibility(View.GONE);
                        }

                        Snackbar longSnackbar = Snackbar.make(layout, finalSnackbarText,
                                Snackbar.LENGTH_LONG);
                        longSnackbar.show();
                        longSnackbar.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                if (toHide1 != null & toHide2 != null) {
                                    toHide1.setVisibility(View.VISIBLE);
                                    toHide2.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void showApplyWallpaperDialog(final Activity context, final String wallUrl) {
        ISDialogs.showApplyWallpaperDialog(context,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        dialogApply = new MaterialDialog.Builder(context)
                                .content(R.string.downloading_wallpaper)
                                .progress(true, 0)
                                .cancelable(false)
                                .show();
                        Glide.with(context)
                                .load(wallUrl)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (resource != null) {
                                            dialogApply.setContent(context.getString(R.string.setting_wall_title));
                                            new ApplyWallpaper(context, dialogApply, resource,
                                                    false, layout, null, toHide1, toHide2).execute();
                                        }
                                    }
                                });
                    }
                }, new MaterialDialog.SingleButtonCallback() {
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
                                .load(wallUrl)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        if (resource != null) {
                                            new WallpaperToCrop(context, dialogApply, resource,
                                                    layout, null, wallName, toHide1, toHide2).execute();
                                        }
                                    }
                                });
                    }
                });
    }

    private void showNotConnectedSnackBar(final FloatingActionMenu fab) {
        if (fab != null) {
            if (fab.isOpened()) {
                fab.close(mPrefs.getAnimationsEnabled());
            }
            fab.hideMenuButton(mPrefs.getAnimationsEnabled());
            fab.hideMenu(mPrefs.getAnimationsEnabled());
        }

        Snackbar notConnectedSnackBar = Snackbar.make(layout, R.string.no_conn_title,
                Snackbar.LENGTH_LONG);
        notConnectedSnackBar.show();
        notConnectedSnackBar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (fab != null) {
                    fab.showMenuButton(mPrefs.getAnimationsEnabled());
                }
            }
        });
        if (!notConnectedSnackBar.isShown()) {
            if (fab != null) {
                fab.showMenuButton(mPrefs.getAnimationsEnabled());
            }
        }
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
                        saveWallpaperAction(wallName, wallUrl);
                        break;

                    case "apply":
                        showApplyWallpaperDialog(context, wallUrl);
                        break;
                }
            } else {
                showNotConnectedSnackBar(null);
            }
        }

    }

    private void showErrorDialog() {
        //TODO: Add a error dialog
    }

}