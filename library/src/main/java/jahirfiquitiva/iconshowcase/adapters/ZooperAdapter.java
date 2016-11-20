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

package jahirfiquitiva.iconshowcase.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;
import jahirfiquitiva.iconshowcase.fragments.ZooperFragment;
import jahirfiquitiva.iconshowcase.models.ZooperWidget;
import jahirfiquitiva.iconshowcase.tasks.CopyFilesToStorage;
import jahirfiquitiva.iconshowcase.tasks.LoadZooperWidgets;
import jahirfiquitiva.iconshowcase.utilities.PermissionUtils;
import jahirfiquitiva.iconshowcase.utilities.Preferences;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;
import jahirfiquitiva.iconshowcase.utilities.color.ColorUtils;
import jahirfiquitiva.iconshowcase.views.DebouncedClickListener;

public class ZooperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Drawable[] icons = new Drawable[2];
    private final Context context;
    private final Drawable wallpaper;
    private final View layout;
    private final boolean everythingInstalled;
    private ArrayList<ZooperWidget> widgets;
    private ZooperFragment mFragment;
    private Preferences mPrefs;
    private int extraCards = 0;

    public ZooperAdapter(Context context, View layout,
                         Drawable wallpaper, boolean appsInstalled, ZooperFragment mFragment) {
        this.context = context;
        this.mPrefs = new Preferences(context);
        this.layout = layout;
        this.widgets = LoadZooperWidgets.widgets;
        this.wallpaper = wallpaper;
        this.everythingInstalled = (appsInstalled && areAssetsInstalled());
        this.extraCards = this.everythingInstalled ? 0 : 2;
        final int light = ContextCompat.getColor(context, R.color.drawable_tint_dark);
        final int dark = ContextCompat.getColor(context, R.color.drawable_tint_light);
        this.icons[0] = ColorUtils.getTintedIcon(
                context, R.drawable.ic_store_download,
                ThemeUtils.darkTheme ? light : dark);
        this.icons[1] = ColorUtils.getTintedIcon(
                context, R.drawable.ic_assets,
                ThemeUtils.darkTheme ? light : dark);
        this.mFragment = mFragment;
    }

    public void setWidgets(ArrayList<ZooperWidget> widgets) {
        if (widgets != null) {
            this.widgets.addAll(widgets);
            this.notifyItemRangeInserted(0, widgets.size() - 1);
        } else {
            this.widgets = new ArrayList<>();
            this.notifyItemRangeInserted(0, 0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (!everythingInstalled) {
            switch (i) {
                case 0:
                case 1:
                    return new ZooperButtonHolder(
                            inflater.inflate(R.layout.item_zooper_button, parent, false), i);
                default:
                    return new ZooperHolder(
                            inflater.inflate(R.layout.item_widget_preview, parent, false));
            }
        } else {
            return new ZooperHolder(
                    inflater.inflate(R.layout.item_widget_preview, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String[] texts = new String[]{
                Utils.getStringFromResources(context, R.string.install_apps),
                Utils.getStringFromResources(context, R.string.install_assets)
        };

        if (!everythingInstalled) {
            switch (position) {
                case 0:
                case 1:
                    ZooperButtonHolder zooperButtonHolder = (ZooperButtonHolder) holder;
                    zooperButtonHolder.icon.setImageDrawable(icons[position]);
                    zooperButtonHolder.text.setText(texts[position]);
                    break;
                default:
                    ZooperWidget widget = widgets.get(position - 2);
                    ZooperHolder zooperHolder = (ZooperHolder) holder;
                    zooperHolder.background.setImageDrawable(wallpaper);
                    if (mPrefs != null && mPrefs.getAnimationsEnabled()) {
                        Glide.with(context)
                                .load(new File(widget.getPreviewPath()))
                                .priority(Priority.IMMEDIATE)
                                .into(zooperHolder.widget);
                    } else {
                        Glide.with(context)
                                .load(new File(widget.getPreviewPath()))
                                .priority(Priority.IMMEDIATE)
                                .dontAnimate()
                                .into(zooperHolder.widget);
                    }
                    break;
            }
        } else {
            ZooperWidget widget = widgets.get(position);
            ZooperHolder zooperHolder = (ZooperHolder) holder;
            zooperHolder.background.setImageDrawable(wallpaper);
            if (mPrefs != null && mPrefs.getAnimationsEnabled()) {
                Glide.with(context)
                        .load(new File(widget.getPreviewPath()))
                        .priority(Priority.IMMEDIATE)
                        .into(zooperHolder.widget);
            } else {
                Glide.with(context)
                        .load(new File(widget.getPreviewPath()))
                        .priority(Priority.IMMEDIATE)
                        .dontAnimate()
                        .into(zooperHolder.widget);
            }

        }

    }

    @Override
    public int getItemCount() {
        return widgets != null ? widgets.size() + extraCards : extraCards;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private boolean areAssetsInstalled() {
        boolean assetsInstalled = false;

        String fileToIgnore1 = "material-design-iconic-font-v2.2.0.ttf",
                fileToIgnore2 = "materialdrawerfont.ttf",
                fileToIgnore3 = "materialdrawerfont-font-v5.0.0.ttf",
                fileToIgnore4 = "google-material-font-v2.2.0.1.original.ttf";

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        String[] folders = new String[]{"fonts", "iconsets", "bitmaps"};

        for (String folder : folders) {
            try {
                files = assetManager.list(folder);
            } catch (IOException e) {
                //Do nothing
            }

            if (files != null && files.length > 0) {
                for (String filename : files) {
                    if (filename.contains(".")) {
                        if (!filename.equals(fileToIgnore1) && !filename.equals(fileToIgnore2)
                                && !filename.equals(fileToIgnore3) && !filename.equals(fileToIgnore4)) {
                            File file = new File(Environment.getExternalStorageDirectory()
                                    + "/ZooperWidget/" + getFolderName(folder) + "/" + filename);
                            assetsInstalled = file.exists();
                        }
                    }
                }
            }
        }

        return assetsInstalled;
    }

    private String getFolderName(String folder) {
        switch (folder) {
            case "fonts":
                return "Fonts";
            case "iconsets":
                return "IconSets";
            case "bitmaps":
                return "Bitmaps";
            default:
                return folder;
        }
    }

    public void installAssets() {
        if (!PermissionUtils.canAccessStorage(context)) {
            PermissionUtils.requestStoragePermission((ShowcaseActivity) context);
        } else {
            String[] folders = new String[]{"fonts", "iconsets", "bitmaps"};
            for (String folderName : folders) {
                String dialogContent =
                        context.getResources().getString(
                                R.string.copying_assets, getFolderName(folderName));
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .content(dialogContent)
                        .progress(true, 0)
                        .cancelable(false)
                        .show();
                new CopyFilesToStorage(context, layout, dialog, folderName).execute();
            }
        }
    }

    class ZooperHolder extends RecyclerView.ViewHolder {

        final ImageView background;
        final ImageView widget;

        public ZooperHolder(View itemView) {
            super(itemView);
            background = (ImageView) itemView.findViewById(R.id.wall);
            widget = (ImageView) itemView.findViewById(R.id.preview);
        }

    }

    class ZooperButtonHolder extends RecyclerView.ViewHolder {

        final CardView card;
        final ImageView icon;
        final TextView text;

        public ZooperButtonHolder(View itemView, final int position) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.zooper_btn_card);
            icon = (ImageView) itemView.findViewById(R.id.zooper_btn_icon);
            text = (TextView) itemView.findViewById(R.id.zooper_btn_title);
            card.setOnClickListener(new DebouncedClickListener() {
                @Override
                public void onDebouncedClick(View v) {
                    switch (position) {
                        case 0:
                            //Open dialog
                            ArrayList<String> apps = new ArrayList<>();
                            if (!Utils.isAppInstalled(context, "org.zooper.zwpro")) {
                                apps.add(Utils.getStringFromResources(context, R.string.zooper_app));
                            }
                            if (context.getResources().getBoolean(R.bool.mu_needed) &&
                                    !Utils.isAppInstalled(context, "com.batescorp.notificationmediacontrols.alpha")) {
                                apps.add(Utils.getStringFromResources(context, R.string.mu_app));
                            }
                            if (context.getResources().getBoolean(R.bool.kolorette_needed) &&
                                    !Utils.isAppInstalled(context, "com.arun.themeutil.kolorette")) {
                                apps.add(Utils.getStringFromResources(context, R.string.kolorette_app));
                            }
                            if (apps.size() > 0) {
                                ISDialogs.showZooperAppsDialog(context, apps);
                            } else {
                                if (mFragment != null) mFragment.showInstalledAppsSnackbar();
                            }
                            break;
                        case 1:
                            //Install assets
                            if (!areAssetsInstalled()) {
                                if (!PermissionUtils.canAccessStorage(context)) {
                                    PermissionUtils.requestStoragePermission((ShowcaseActivity) context);
                                } else {
                                    installAssets();
                                }
                            } else {
                                if (mFragment != null) mFragment.showInstalledAssetsSnackbar();
                            }
                            break;
                    }
                }
            });
        }

    }

}
