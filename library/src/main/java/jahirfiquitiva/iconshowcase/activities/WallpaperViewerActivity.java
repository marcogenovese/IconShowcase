/*
 * Copyright (c) 2016 Jahir Fiquitiva
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
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.iconshowcase.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.FileInputStream;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.base.BaseWallpaperViewerActivity;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import jahirfiquitiva.iconshowcase.utilities.color.ToolbarColorizer;
import jahirfiquitiva.iconshowcase.utilities.utils.IconUtils;
import jahirfiquitiva.iconshowcase.utilities.utils.PermissionsUtils;
import jahirfiquitiva.iconshowcase.utilities.utils.ThemeUtils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;
import jahirfiquitiva.iconshowcase.views.TouchImageView;

public class WallpaperViewerActivity extends BaseWallpaperViewerActivity {

    private LinearLayout toHide1, toHide2;
    private Activity context;

    @SuppressWarnings("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setFullScreen(false);
        super.onCreate(savedInstanceState);

        setCallback(new WallpaperDialogsCallback() {
            @Override
            public void onDialogShown() {
                if (toHide1 != null && toHide2 != null) {
                    toHide1.setVisibility(View.GONE);
                    toHide2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onDialogDismissed() {
                if (toHide1 != null && toHide2 != null) {
                    toHide1.setVisibility(View.VISIBLE);
                    toHide2.setVisibility(View.VISIBLE);
                }
            }
        });

        setContentView(R.layout.wallpaper_viewer_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_with_shadow);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ToolbarColorizer.colorizeToolbar(toolbar, ContextCompat.getColor(context, android.R.color
                .white));

        toHide1 = (LinearLayout) findViewById(R.id.iconsA);
        toHide2 = (LinearLayout) findViewById(R.id.iconsB);

        setViewsToHide(toHide1, toHide2);

        int tintColor = ThemeUtils.darkOrLight(context, R.color.drawable_tint_dark, R.color
                .drawable_base_tint);

        Drawable save = IconUtils.getTintedIcon(context, R.drawable.ic_save, tintColor);

        Drawable apply = IconUtils.getTintedIcon(context, R.drawable.ic_apply_wallpaper,
                tintColor);

        Drawable info = IconUtils.getTintedIcon(context, R.drawable.ic_info, tintColor);

        ImageView saveIV = (ImageView) findViewById(R.id.download);
        if (getItem().isDownloadable()) {
            saveIV.setImageDrawable(save);
            saveIV.setOnClickListener(new DebouncedClickListener() {
                @Override
                public void onDebouncedClick(View v) {
                    PermissionsUtils.checkPermission(context, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE, new PermissionsUtils
                            .PermissionRequestListener() {
                        @Override
                        public void onPermissionRequest() {
                            PermissionsUtils.setViewerActivityAction("save");
                            PermissionsUtils.requestStoragePermission(context);
                        }

                        @Override
                        public void onPermissionDenied() {
                            ISDialogs.showPermissionNotGrantedDialog(context);
                        }

                        @Override
                        public void onPermissionCompletelyDenied() {
                            ISDialogs.showPermissionNotGrantedDialog(context);
                        }

                        @Override
                        public void onPermissionGranted() {
                            runWallpaperSave(context);
                        }
                    });
                }
            });
        } else {
            saveIV.setVisibility(View.GONE);
        }

        ImageView applyIV = (ImageView) findViewById(R.id.apply);
        applyIV.setImageDrawable(apply);
        applyIV.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                showApplyWallpaperDialog(context);
            }
        });

        ImageView infoIV = (ImageView) findViewById(R.id.info);
        infoIV.setImageDrawable(info);
        infoIV.setOnClickListener(new DebouncedClickListener() {
            @Override
            public void onDebouncedClick(View v) {
                ISDialogs.showWallpaperDetailsDialog(context, getItem().getWallName(),
                        getItem().getWallAuthor(), getItem().getWallDimensions(), getItem()
                                .getWallCopyright(),
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //Do nothing
                            }
                        });
            }
        });

        TouchImageView mPhoto = (TouchImageView) findViewById(R.id.big_wallpaper);
        ViewCompat.setTransitionName(mPhoto, getTransitionName());

        setLayout((RelativeLayout) findViewById(R.id.viewerLayout));

        TextView wallNameText = (TextView) findViewById(R.id.wallName);
        wallNameText.setText(getItem().getWallName());

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
            colorFromCachedPic = ColorUtils.getPaletteSwatch(bmp).getTitleTextColor();
        } else {
            colorFromCachedPic = tintColor;
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

        if (getPrefs().getAnimationsEnabled()) {
            Glide.with(context)
                    .load(getItem().getWallURL())
                    .placeholder(d)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target, boolean
                                                           isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target, boolean
                                                               isFromMemoryCache, boolean
                                                               isFirstResource) {
                            spinner.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mPhoto);
        } else {
            Glide.with(context)
                    .load(getItem().getWallURL())
                    .placeholder(d)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model,
                                                   Target<GlideDrawable> target, boolean
                                                           isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target, boolean
                                                               isFromMemoryCache, boolean
                                                               isFirstResource) {
                            spinner.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(mPhoto);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            //Crop request
            if (toHide1 != null && toHide2 != null) {
                toHide1.setVisibility(View.VISIBLE);
                toHide2.setVisibility(View.VISIBLE);
            }
        }
    }

}