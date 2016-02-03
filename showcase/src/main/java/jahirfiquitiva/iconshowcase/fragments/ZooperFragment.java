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

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.tasks.CopyFilesToStorage;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class ZooperFragment extends Fragment {

    private boolean WITH_MEDIAUTILITIES_WIDGETS = true;

    private MaterialDialog dialog;
    private ViewGroup layout;

    private AppCompatButton downloadZooper, downloadMU, openMU;
    private CardView cardZooper, cardMU, cardMUInfo, installFonts, installIconsets;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.zooper_section, container, false);
        } catch (InflateException e) {

        }

        context = getActivity();

        final int light = ContextCompat.getColor(context, android.R.color.white);
        final int dark = ContextCompat.getColor(context, R.color.grey);

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

        ImageView zooperIV = (ImageView) layout.findViewById(R.id.icon_zooper);
        zooperIV.setImageDrawable(alert);

        ImageView muIV = (ImageView) layout.findViewById(R.id.icon_mu);
        muIV.setImageDrawable(alert);

        ImageView fontsIV = (ImageView) layout.findViewById(R.id.icon_fonts);
        fontsIV.setImageDrawable(fonts);

        ImageView iconsetsIV = (ImageView) layout.findViewById(R.id.icon_iconsets);
        iconsetsIV.setImageDrawable(iconsets);

        cardZooper = (CardView) layout.findViewById(R.id.zooper_card);

        if (Utils.isAppInstalled(context, "org.zooper.zwpro")) {
            cardZooper.setVisibility(View.GONE);
        } else {
            cardZooper.setVisibility(View.VISIBLE);
        }

        downloadZooper = (AppCompatButton) layout.findViewById(R.id.download_button);
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

        cardMU = (CardView) layout.findViewById(R.id.mu_card);
        cardMUInfo = (CardView) layout.findViewById(R.id.mediautilities_info_card);

        if (WITH_MEDIAUTILITIES_WIDGETS) {
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

        downloadMU = (AppCompatButton) layout.findViewById(R.id.mu_download_button);
        downloadMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openLinkInChromeCustomTab(context,
                        "https://play.google.com/store/apps/details?id=com.batescorp.notificationmediacontrols.alpha");
            }
        });

        openMU = (AppCompatButton) layout.findViewById(R.id.mu_open_button);
        openMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent muIntent = context.getPackageManager().getLaunchIntentForPackage(
                        "com.batescorp.notificationmediacontrols.alpha");
                startActivity(muIntent);
            }
        });

        installFonts = (CardView) layout.findViewById(R.id.fonts_card);
        installFonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fontsInstalled(context)) {
                    dialog = new MaterialDialog.Builder(context)
                            .content(R.string.downloading_wallpaper)
                            .progress(true, 0)
                            .cancelable(false)
                            .show();
                    new CopyFilesToStorage(context, dialog, "Fonts").execute();
                } else {
                    Utils.showSimpleSnackbar(layout,
                            getResources().getString(R.string.fonts_installed), 1);
                }
            }
        });

        installIconsets = (CardView) layout.findViewById(R.id.iconsets_card);
        installIconsets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!iconsetsInstalled()) {
                    dialog = new MaterialDialog.Builder(context)
                            .content(R.string.downloading_wallpaper)
                            .progress(true, 0)
                            .cancelable(false)
                            .show();
                    new CopyFilesToStorage(context, dialog, "IconSets").execute();
                } else {
                    Utils.showSimpleSnackbar(layout,
                            getResources().getString(R.string.iconsets_installed), 1);
                }
            }
        });

        return layout;
    }

    private boolean fontsInstalled(Context context) {

        boolean fontsInDevice = true;

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("Fonts");
        } catch (IOException e) {
        }

        for (String filename : files) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/ZooperWidget/Fonts/" + filename);
                if (!file.exists()) {
                    fontsInDevice = false;
                }
            } catch (Exception e) {
            }
        }

        return fontsInDevice;

    }

    private boolean iconsetsInstalled() {

        boolean iconsetsInDevice = true;

        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("IconSets");
        } catch (IOException e) {
        }

        for (String filename : files) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/ZooperWidget/IconSets/" + filename);
                if (!file.exists()) {
                    iconsetsInDevice = false;
                }
            } catch (Exception e) {
            }
        }

        return iconsetsInDevice;

    }

}