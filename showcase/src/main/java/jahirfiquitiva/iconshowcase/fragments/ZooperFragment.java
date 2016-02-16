/*
 *
 */

package jahirfiquitiva.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.models.ZooperWidget;
import jahirfiquitiva.iconshowcase.tasks.CopyFilesToStorage;
import jahirfiquitiva.iconshowcase.tasks.LoadZooperWidgets;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.ToolbarColorizer;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class ZooperFragment extends Fragment {

    private MaterialDialog dialog;
    private ViewGroup layout;
    private Context context;
    private int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();

        boolean WITH_MEDIA_UTILITIES_WIDGETS = context.getResources().getBoolean(R.bool.mu_needed);

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.zooper_section, container, false);
        } catch (InflateException e) {
            //Do nothing
        }

        final int light = ContextCompat.getColor(context, R.color.drawable_tint_dark);
        final int dark = ContextCompat.getColor(context, R.color.drawable_tint_light);

        Drawable alert = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_alert_triangle)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable fonts = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_text_format)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable iconsets = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_toys)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable fire = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_fire)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        ImageView fireIV = (ImageView) layout.findViewById(R.id.icon_preview);
        fireIV.setImageDrawable(fire);

        final ImageView preview = (ImageView) layout.findViewById(R.id.preview_picture);

        final ArrayList<ZooperWidget> widgets = LoadZooperWidgets.widgets;

        preview.setImageBitmap(widgets.get(i).getPreview());
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i += 1;
                if (widgets.size() > 0) {
                    preview.setImageBitmap(widgets.get(i).getPreview());
                    if (i == widgets.size() - 1) {
                        i = -1;
                    }
                }
            }
        });

        ImageView zooperIV = (ImageView) layout.findViewById(R.id.icon_zooper);
        zooperIV.setImageDrawable(alert);

        ImageView muIV = (ImageView) layout.findViewById(R.id.icon_mu);
        muIV.setImageDrawable(alert);

        ImageView fontsIV = (ImageView) layout.findViewById(R.id.icon_fonts);
        fontsIV.setImageDrawable(fonts);

        ImageView iconsetsIV = (ImageView) layout.findViewById(R.id.icon_iconsets);
        iconsetsIV.setImageDrawable(iconsets);

        CardView cardZooper = (CardView) layout.findViewById(R.id.zooper_card);

        if (Utils.isAppInstalled(context, "org.zooper.zwpro")) {
            cardZooper.setVisibility(View.GONE);
        } else {
            cardZooper.setVisibility(View.VISIBLE);
        }

        AppCompatButton downloadZooper = (AppCompatButton) layout.findViewById(R.id.download_button);
        downloadZooper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new MaterialDialog.Builder(context)
                        .title(R.string.zooper_download_dialog_title)
                        .items(R.array.zooper_download_dialog_options)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int selection, CharSequence text) {
                                switch (selection) {
                                    case 0:
                                        Utils.openLinkInChromeCustomTab(context,
                                                "https://play.google.com/store/apps/details?id=org.zooper.zwpro");
                                        break;
                                    case 1:
                                        if (Utils.isAppInstalled(context, "com.amazon.venezia")) {
                                            Utils.openLinkInChromeCustomTab(context,
                                                    "amzn://apps/android?p=org.zooper.zwpro");
                                        } else {
                                            Utils.openLinkInChromeCustomTab(context,
                                                    "http://www.amazon.com/gp/mas/dl/android?p=org.zooper.zwpro");
                                        }
                                        break;
                                }
                            }
                        })
                        .show();
            }
        });

        CardView cardMU = (CardView) layout.findViewById(R.id.mu_card);
        CardView cardMUInfo = (CardView) layout.findViewById(R.id.mediautilities_info_card);

        if (WITH_MEDIA_UTILITIES_WIDGETS) {
            if (!Utils.isAppInstalled(context, "com.batescorp.notificationmediacontrols.alpha")) {
                cardMU.setVisibility(View.VISIBLE);
                cardMUInfo.setVisibility(View.GONE);
            } else {
                cardMU.setVisibility(View.GONE);
                cardMUInfo.setVisibility(View.VISIBLE);
            }
        } else {
            cardMU.setVisibility(View.GONE);
            cardMUInfo.setVisibility(View.GONE);
        }

        AppCompatButton downloadMU = (AppCompatButton) layout.findViewById(R.id.mu_download_button);
        downloadMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openLinkInChromeCustomTab(context,
                        "https://play.google.com/store/apps/details?id=com.batescorp.notificationmediacontrols.alpha");
            }
        });

        AppCompatButton openMU = (AppCompatButton) layout.findViewById(R.id.mu_open_button);
        openMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent muIntent = context.getPackageManager().getLaunchIntentForPackage(
                        "com.batescorp.notificationmediacontrols.alpha");
                startActivity(muIntent);
            }
        });

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.collapseToolbar(getActivity());
        int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(context, R.color.toolbar_text_dark) :
                ContextCompat.getColor(context, R.color.toolbar_text_light);
        ToolbarColorizer.colorizeToolbar(
                ShowcaseActivity.toolbar,
                iconsColor);
        if (layout != null) {
            setupCards(true, true);
        }
    }

    private void setupCards(boolean fonts, boolean iconsets) {
        CardView installFonts = (CardView) layout.findViewById(R.id.fonts_card);
        if (fonts) {
            if (checkAssetsInstalled("fonts")) {
                installFonts.setVisibility(View.GONE);
            } else {
                installFonts.setVisibility(View.VISIBLE);
                installFonts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkAssetsInstalled("fonts")) {
                            dialog = new MaterialDialog.Builder(context)
                                    .content(R.string.downloading_wallpaper)
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .show();
                            new CopyFilesToStorage(context, dialog, "fonts").execute();
                        } else {
                            Utils.showSimpleSnackbar(context, layout,
                                    getResources().getString(R.string.fonts_installed), 1);
                            setupCards(true, false);
                        }
                    }
                });
            }
        }

        CardView installIconsets = (CardView) layout.findViewById(R.id.iconsets_card);
        if (iconsets) {
            if (checkAssetsInstalled("iconsets")) {
                installIconsets.setVisibility(View.GONE);
            } else {
                installIconsets.setVisibility(View.VISIBLE);
                installIconsets.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!checkAssetsInstalled("iconsets")) {
                            dialog = new MaterialDialog.Builder(context)
                                    .content(R.string.downloading_wallpaper)
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .show();
                            new CopyFilesToStorage(context, dialog, "iconsets").execute();
                        } else {
                            Utils.showSimpleSnackbar(context, layout,
                                    getResources().getString(R.string.iconsets_installed), 1);
                            setupCards(false, true);
                        }
                    }
                });
            }
        }
    }

    private boolean checkAssetsInstalled(String folder) {
        boolean assetsInstalled = true;

        String fileToIgnore1 = "material-design-iconic-font-v2.2.0.ttf";
        String fileToIgnore2 = "materialdrawerfont.ttf";

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(folder);
        } catch (IOException e) {
            //Do nothing
        }

        if (files != null && files.length > 0) {
            for (String filename : files) {
                Utils.showLog(context, "File in assets " + folder + ": " + filename);
                if (!filename.equals(fileToIgnore1) && !filename.equals(fileToIgnore2)) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/ZooperWidget/" + getFolderName(folder) + "/" + filename);
                    if (!file.exists()) {
                        assetsInstalled = false;
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
            default:
                return folder;
        }
    }

}