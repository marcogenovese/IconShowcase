/*
 * Copyright (c) 2016. Jahir Fiquitiva. Android Developer. All rights reserved.
 */

package jahirfiquitiva.iconshowcase.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import jahirfiquitiva.iconshowcase.dialogs.ISDialogs;

public class LicenseUtils {

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";

    public static void checkLicense(Context context, final Preferences mPrefs) {

        boolean installedFromPlayStore = false, enableFromAmazon = mPrefs.getAmazonInstalls();
        String installer = mPrefs.getInstaller();

        try {
            if (installer != null) {
                if (installer.matches("com.google.android.feedback") || installer.matches("com.android.vending")) {
                    installedFromPlayStore = true;
                }
                if (installedFromPlayStore) {
                    ISDialogs.showLicenseSuccessDialog(context, new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            mPrefs.setFeaturesEnabled(true);
                        }
                    });
                } else if (installer.matches("com.amazon.venezia") && enableFromAmazon) {
                    ISDialogs.showLicenseSuccessDialog(context, new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            mPrefs.setFeaturesEnabled(true);
                        }
                    });
                }
            } else {
                showNotLicensedDialog((Activity) context, mPrefs, MARKET_URL);
            }
        } catch (Exception e) {
            Utils.showLog(context, "Error checking license: " + e.getLocalizedMessage());
            showNotLicensedDialog((Activity) context, mPrefs, MARKET_URL);
        }

    }

    private static void showNotLicensedDialog(final Activity act, Preferences mPrefs, final String MARKET_URL) {
        mPrefs.setFeaturesEnabled(false);
        ISDialogs.showLicenseFailDialog(act,
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + act.getPackageName()));
                        act.startActivity(browserIntent);
                    }
                }, new MaterialDialog.SingleButtonCallback() {

                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        act.finish();
                    }
                }, new MaterialDialog.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        act.finish();
                    }
                }, new MaterialDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        act.finish();
                    }
                });
    }

}
