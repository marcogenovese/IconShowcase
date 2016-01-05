/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.apps.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;

import jahirfiquitiva.apps.iconshowcase.R;
import jahirfiquitiva.apps.iconshowcase.tasks.CopyFilesToStorage;
import jahirfiquitiva.apps.iconshowcase.utilities.Util;

public class ZooperFragment extends Fragment {

    private MaterialDialog dialog;
    private ViewGroup layout;

    private TextView downloadZooper, downloadMU, openMU;
    private CardView cardZooper, cardMU, cardMUInfo, installFonts, installIconsets;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

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

        cardZooper = (CardView) layout.findViewById(R.id.zooper_card);
        if (Util.isAppInstalled(context, "org.zooper.zwpro")) {
            cardZooper.setVisibility(View.GONE);
        } else {
            cardZooper.setVisibility(View.VISIBLE);
        }

        downloadZooper = (TextView) layout.findViewById(R.id.download_button);
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
                                        Util.openLinkInChromeCustomTab(context,
                                                "https://play.google.com/store/apps/details?id=org.zooper.zwpro");
                                        break;
                                    case 1:
                                        if (Util.isAppInstalled(context, "com.amazon.venezia")) {
                                            Util.openLinkInChromeCustomTab(context,
                                                    "amzn://apps/android?p=org.zooper.zwpro");
                                        } else {
                                            Util.openLinkInChromeCustomTab(context,
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

        if (!Util.isAppInstalled(context, "com.batescorp.notificationmediacontrols.alpha")) {
            cardMU.setVisibility(View.VISIBLE);
            cardMUInfo.setVisibility(View.GONE);
        } else {
            cardMU.setVisibility(View.GONE);
            cardMUInfo.setVisibility(View.VISIBLE);
        }

        downloadMU = (TextView) layout.findViewById(R.id.mu_download_button);
        downloadMU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.openLinkInChromeCustomTab(context,
                        "https://play.google.com/store/apps/details?id=com.batescorp.notificationmediacontrols.alpha");
            }
        });

        openMU = (TextView) layout.findViewById(R.id.mu_open_button);
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
                    Util.showSimpleSnackbar(layout,
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
                    Util.showSimpleSnackbar(layout,
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